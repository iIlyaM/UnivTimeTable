package vsu.cs.univtimetable.screens

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vsu.cs.univtimetable.DateManager.Companion.WEEK_DAYS
import vsu.cs.univtimetable.DateManager.Companion.checkWeekType
import vsu.cs.univtimetable.DateManager.Companion.getDayOfWeek
import vsu.cs.univtimetable.R
import vsu.cs.univtimetable.SessionManager
import vsu.cs.univtimetable.TimetableClient
import vsu.cs.univtimetable.api.TimetableApi
import vsu.cs.univtimetable.dto.ClassDto
import vsu.cs.univtimetable.dto.DateDto
import vsu.cs.univtimetable.dto.TimetableResponse
import vsu.cs.univtimetable.screens.adapter.DayOfWeekAdapter
import vsu.cs.univtimetable.screens.adapter.LecturerTimetableAdapter
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class LecturerTimetablePageFragment : Fragment() {

    private lateinit var timetableApi: TimetableApi
    private lateinit var recyclerView: RecyclerView
    private lateinit var lectWeekView: RecyclerView
    private lateinit var dayAdapter: DayOfWeekAdapter
    private lateinit var timeTableAdapter: LecturerTimetableAdapter
    private lateinit var toLeftView: ImageView
    private lateinit var toRightView: ImageView

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
        val view = inflater.inflate(R.layout.fragment_lecturer_timetable_page, container, false)
        recyclerView = view.findViewById(R.id.lecturerRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        lectWeekView = view.findViewById(R.id.lectWeekView)
        lectWeekView.layoutManager = LinearLayoutManager(requireContext())

        toLeftView = view.findViewById(R.id.toLeftView)
        toRightView = view.findViewById(R.id.toRightView)
        if(getCurrDayOfWeek() == "Воскресенье") {
            toRightView.visibility = View.INVISIBLE
        }
        if(getCurrDayOfWeek() == "Понедельник") {
            toLeftView.visibility = View.INVISIBLE
        }

        toLeftView.setOnClickListener {
            getPrevDay(toRightView, toLeftView)
        }

        toRightView.setOnClickListener {
            getNextDay(toRightView, toLeftView)
        }

        val downloadLectTTBtn = view.findViewById<AppCompatButton>(R.id.downloadLectTTBtn)
        downloadLectTTBtn.setOnClickListener {
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


        call.enqueue(object : Callback<TimetableResponse> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(
                call: Call<TimetableResponse>,
                response: Response<TimetableResponse>
            ) {
                if (response.isSuccessful) {
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
                    if (response.code() == 400) {
                        showToastNotification("Расписание ещё не сформировано")
                    }
                    if (response.code() == 403) {
                        showToastNotification("Недостаточно прав доступа для выполнения")
                    }
                    if (response.code() == 404) {
                        showToastNotification("Неверный username пользователя")
                    }
                    Log.d("ошибка", "Получили ошибку - ${response.code()}")
                    Log.d("ошибка", "с ошибкой пришло - ${response.body()}")
                }
            }

            override fun onFailure(call: Call<TimetableResponse>, t: Throwable) {
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
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    val file = File(context!!.getExternalFilesDir(null), "example.xlsx")
                    val inputStream = response.body()?.byteStream()
                    val outputStream = FileOutputStream(file)

                    inputStream?.use { input ->
                        outputStream.use { output ->
                            input.copyTo(output)
                        }
                    }
                    showToastNotification("Файл сохранён")
                } else {
                    if (response.code() == 400) {
                        showToastNotification("Расписание ещё не сформировано")
                    }
                    if (response.code() == 403) {
                        showToastNotification("Недостаточно прав доступа для выполнения")
                    }
                    if (response.code() == 404) {
                        showToastNotification("Неверный username пользователя")
                    }
                    Log.d("ошибка", "Получили ошибку - ${response.code()}")
                    Log.d("ошибка", "с ошибкой пришло - ${response.body()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                println("Ошибка")
                println(t)
            }
        })
    }

    private fun showToastNotification (message: String) {
        val duration = Toast.LENGTH_LONG

        val toast = Toast.makeText(requireContext(), message, duration)
        toast.show()
        val handler = Handler()
        handler.postDelayed({ toast.cancel() }, 1500)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDayTimetable(
        timetable: MutableMap<String, List<ClassDto>>,
        weekType: String,
        dayOfWeek: String
    ) {


        timeTableAdapter = LecturerTimetableAdapter(
            requireContext(),
            timetable[dayOfWeek]!!
        )
        recyclerView.adapter = timeTableAdapter

        setDayAdapter(weekType, dayOfWeek)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrDayOfWeek(): String {
        val currDay = LocalDate.now().dayOfWeek.getDisplayName(TextStyle.FULL, Locale("ru")).capitalize()
        currDayInd = WEEK_DAYS.toList().indexOf(currDay)
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
        dayAdapter = DayOfWeekAdapter(
            requireContext(),
            DateDto(getDayOfWeek(day, weekPointer), weekType)
        )
        lectWeekView.adapter = dayAdapter
    }

//    private fun showDialog() {
//        val builder = AlertDialog.Builder(requireContext())
//
//        builder.setMessage("Расписание ещё не сформировано")
//        val alert = builder.create()
//        alert.show()
//        alert.window?.setGravity(Gravity.BOTTOM)
//
//        Handler().postDelayed({
//            alert.dismiss()
//        }, 2000)
//        findNavController().navigate(R.id.action_lecturerTimetablePageFragment_to_lecturerMainPageFragment)
//
//    }

}