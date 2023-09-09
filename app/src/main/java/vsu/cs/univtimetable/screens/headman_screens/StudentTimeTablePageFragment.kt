package vsu.cs.univtimetable.screens.headman_screens

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.leandroborgesferreira.loadingbutton.customViews.CircularProgressButton
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vsu.cs.univtimetable.utils.date_utils.DateManager
import vsu.cs.univtimetable.utils.date_utils.DateManager.Companion.WEEK_DAYS
import vsu.cs.univtimetable.utils.date_utils.DateManager.Companion.checkWeekType
import vsu.cs.univtimetable.R
import vsu.cs.univtimetable.utils.token_utils.SessionManager
import vsu.cs.univtimetable.TimetableClient
import vsu.cs.univtimetable.api.TimetableApi
import vsu.cs.univtimetable.dto.classes.ClassDto
import vsu.cs.univtimetable.dto.datetime.DateDto
import vsu.cs.univtimetable.dto.classes.TimetableResponse
import vsu.cs.univtimetable.screens.adapter.HeadmanTimetableAdapter
import vsu.cs.univtimetable.screens.adapter.StudentDayOfWeekAdapter
import vsu.cs.univtimetable.utils.NotificationManager
import vsu.cs.univtimetable.utils.permissions_utils.PermissionsHandler
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class StudentTimeTablePageFragment : Fragment() {
    private lateinit var timetableApi: TimetableApi
    private lateinit var recyclerView: RecyclerView
    private lateinit var lectWeekView: RecyclerView
    private lateinit var dayAdapter: StudentDayOfWeekAdapter
    private lateinit var timeTableAdapter: HeadmanTimetableAdapter
    private lateinit var toLeftView: ImageView
    private lateinit var toRightView: ImageView
    private lateinit var saveTimeTable: CircularProgressButton
    private lateinit var pDialog: ProgressDialog

    private lateinit var timetable: MutableMap<String, List<ClassDto>>


    private var weekPointer = 0
    private var tempWeekPointer = 0
    private var currDayInd = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        timetableApi = TimetableClient.getClient().create(TimetableApi::class.java)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_student_time_table_page, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        pDialog = ProgressDialog(context)
        lectWeekView = view.findViewById(R.id.recyclerView2)
        lectWeekView.layoutManager = LinearLayoutManager(requireContext())

        toLeftView = view.findViewById(R.id.toPrevDayView)
        toRightView = view.findViewById(R.id.toNextDayView)
        if (getCurrDayOfWeek() == "Воскресенье") {
            toRightView.visibility = View.INVISIBLE
        }
        if (getCurrDayOfWeek() == "Понедельник") {
            toLeftView.visibility = View.INVISIBLE
        }

        toLeftView.setOnClickListener {
            getPrevDay(toRightView, toLeftView)
        }

        toRightView.setOnClickListener {
            getNextDay(toRightView, toLeftView)
        }

        val prevPageButton = view.findViewById<ImageButton>(R.id.prevPageButton)
        prevPageButton.setOnClickListener {
            findNavController().navigate(R.id.action_studentTimeTablePageFragment_to_loginFragment)
        }

        saveTimeTable = view.findViewById(R.id.saveTimeTable)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            PermissionsHandler.checkPermissionsButton(requireContext(), saveTimeTable)
        }
        saveTimeTable.setOnClickListener {
            saveTimeTable.startAnimation()
            downloadTimetable()
        }
        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getTimetable()
    }

    private fun getTimetable() {
        val token: String? = SessionManager.getToken(requireContext())
        Log.d("API Request failed", "${token}")
        val call = timetableApi.getTimetable("Bearer ${token}")
        NotificationManager.setLoadingDialog(pDialog)

        call.enqueue(object : Callback<TimetableResponse> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(
                call: Call<TimetableResponse>,
                response: Response<TimetableResponse>
            ) {
                if (response.isSuccessful) {
                    pDialog.dismiss()
                    Log.d("API Request successful", "Получили ${response.code()}")
                    val dataResponse = response.body()
                    var weekType = ""
                    println(dataResponse)
                    if (dataResponse != null) {
                        weekType = checkWeekType()
                        timetable = dataResponse.classes[weekType]!!
                    }
                    weekPointer = WEEK_DAYS.indexOf(getCurrDayOfWeek())
                    tempWeekPointer = weekPointer
                    getDayTimetable(timetable, weekType, getCurrDayOfWeek())
                } else {
                    pDialog.dismiss()
                    if (response.code() == 400) {
                        NotificationManager.showToastNotification(requireContext(), "Расписание ещё не сформировано")
                    }
                    if (response.code() == 403) {
                        NotificationManager.showToastNotification(requireContext(), "Недостаточно прав доступа для выполнения")
                    }
                    if (response.code() == 404) {
                        NotificationManager.showToastNotification(requireContext(), "Неверный username пользователя")
                    }
                    Log.d("ошибка", "Получили ошибку - ${response.code()}")
                    Log.d("ошибка", "с ошибкой пришло - ${response.body()}")
                }
            }

            override fun onFailure(call: Call<TimetableResponse>, t: Throwable) {
                pDialog.dismiss()
                println("Ошибка")
                println(t)
            }
        })
    }

    private fun downloadTimetable() {
        val token: String? = SessionManager.getToken(requireContext())
        Log.d("API Request failed", "${token}")
        val call = timetableApi.downloadFile("Bearer ${token}")

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    val fileName = "расписание.xlsx"
                    val contentResolver = requireContext().contentResolver

                    val contentValues = ContentValues().apply {
                        put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                        put(
                            MediaStore.Downloads.MIME_TYPE,
                            "application/vnd.ms-excel"
                        )
                        put(MediaStore.Downloads.IS_PENDING, 1)
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
                    }
                    try {
                        val resolver = requireContext().contentResolver

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            PermissionsHandler.loadFile(
                                contentResolver,
                                resolver,
                                contentValues,
                                response
                            )
                            stopAnimation(saveTimeTable)
                            NotificationManager.showToastNotification(
                                requireContext(),
                                "Файл сохранен"
                            )
                        } else {
                            val file = File(
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                                fileName
                            )

                            val inputStream = response.body()?.byteStream()
                            val outputStream = FileOutputStream(file)

                            inputStream?.use { input ->
                                outputStream.use { output ->
                                    input.copyTo(output)
                                }
                            }

                            stopAnimation(saveTimeTable)
                            NotificationManager.showToastNotification(
                                requireContext(),
                                "Файл сохранен"
                            )
                        }

                    } catch (e: IOException) {
                        stopAnimation(saveTimeTable)
                        Log.e("Error", "Failed to save file to Downloads directory: ${e.message}")
                        NotificationManager.showToastNotification(requireContext(), "ОШИБКА")
                    }
                } else {
                    stopAnimation(saveTimeTable)
                    if (response.code() == 400) {
                        NotificationManager.showToastNotification(requireContext(), "Расписание ещё не сформировано")
                    }
                    if (response.code() == 403) {
                        NotificationManager.showToastNotification(requireContext(), "Недостаточно прав доступа для выполнения")
                    }
                    if (response.code() == 404) {
                        NotificationManager.showToastNotification(requireContext(), "Неверный username пользователя")
                    }
                    Log.d("ошибка", "Получили ошибку - ${response.code()}")
                    Log.d("ошибка", "с ошибкой пришло - ${response.body()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                stopAnimation(saveTimeTable)
                println("Ошибка")
                println(t)
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDayTimetable(
        timetable: MutableMap<String, List<ClassDto>>,
        weekType: String,
        dayOfWeek: String
    ) {

        timeTableAdapter = HeadmanTimetableAdapter(
            requireContext(),
            timetable[dayOfWeek]!!
        )
        recyclerView.adapter = timeTableAdapter

        setDayAdapter(weekType, dayOfWeek)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrDayOfWeek(): String {
        val currDay =
            LocalDate.now().dayOfWeek.getDisplayName(TextStyle.FULL, Locale("ru")).capitalize()
        currDayInd = DateManager.WEEK_DAYS.toList().indexOf(currDay)
        return currDay
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getNextDay(rightImg: ImageView, leftImg: ImageView) {
        val week = WEEK_DAYS.toList()
        tempWeekPointer++
        getDayTimetable(timetable, checkWeekType(), week[tempWeekPointer])
        if (tempWeekPointer == 1) {
            leftImg.visibility = View.VISIBLE
        } else
            if (tempWeekPointer == week.size - 1) {
                rightImg.visibility = View.INVISIBLE
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getPrevDay(rightImg: ImageView, leftImg: ImageView) {
        val week = WEEK_DAYS.toList()
        tempWeekPointer--
        getDayTimetable(timetable, checkWeekType(), week[tempWeekPointer])
        if (tempWeekPointer == week.size - 2) {
            rightImg.visibility = View.VISIBLE
        } else
            if (tempWeekPointer == 0) {
                leftImg.visibility = View.INVISIBLE
            }
    }

    private fun setDayAdapter(weekType: String, day: String) {
        dayAdapter = StudentDayOfWeekAdapter(
            requireContext(),
            DateDto(DateManager.getDayOfWeek(day, weekPointer), weekType)
        )
        lectWeekView.adapter = dayAdapter
    }


    private fun stopAnimation(btn: CircularProgressButton) {
        btn.background = ContextCompat.getDrawable(requireContext(), R.drawable.headman_bg)
        btn.revertAnimation()
    }

}