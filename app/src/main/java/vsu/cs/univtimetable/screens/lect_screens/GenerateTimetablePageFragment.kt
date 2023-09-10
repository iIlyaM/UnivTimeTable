package vsu.cs.univtimetable.screens.lect_screens

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.leandroborgesferreira.loadingbutton.customViews.CircularProgressButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vsu.cs.univtimetable.R
import vsu.cs.univtimetable.utils.token_utils.SessionManager
import vsu.cs.univtimetable.TimetableClient
import vsu.cs.univtimetable.api.TimetableApi
import vsu.cs.univtimetable.dto.classes.RequestDto
import vsu.cs.univtimetable.screens.adapter.LecturerNameAdapter
import vsu.cs.univtimetable.utils.NotificationManager.showToastNotification

class GenerateTimetablePageFragment : Fragment() {

    private lateinit var timetableApi: TimetableApi
    private lateinit var lecturers: List<String>

    private lateinit var lecturersRecyclerView: RecyclerView
    private lateinit var generateBtn: CircularProgressButton
    private lateinit var clearTTBtn: CircularProgressButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        timetableApi = TimetableClient.getClient().create(TimetableApi::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_generate_timetable_page, container, false)

        lecturersRecyclerView = view.findViewById(R.id.lecturersRecyclerView)
        lecturersRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        generateBtn = view.findViewById(R.id.generateTTBtn)
        generateBtn.setOnClickListener {
            generateBtn.startAnimation()
            generate()
        }

        clearTTBtn = view.findViewById(R.id.clearTTBtn)
        clearTTBtn.setOnClickListener {
            clearTTBtn.startAnimation()
            clear()
        }

        val prevPageButton = view.findViewById<ImageButton>(R.id.prevPageButton)
        prevPageButton.setOnClickListener {
            findNavController().navigate(R.id.action_generateTimetablePageFragment_to_lecturerMainPageFragment)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getLecturers()
    }

    private fun getLecturers() {
        val token: String? = SessionManager.getToken(requireContext())
        Log.d("API Request failed", "${token}")

        val call = timetableApi.getLecturers(
            "Bearer ${token}"
        )

        call.enqueue(object : Callback<List<RequestDto>> {
            override fun onResponse(
                call: Call<List<RequestDto>>,
                response: Response<List<RequestDto>>
            ) {
                if (response.isSuccessful) {
                    Log.d("API Request Successful", "${response.code()}")
                    val dataResponse = response.body()
                    println(dataResponse)
                    if (dataResponse != null) {
                        lecturers = getLecturersList(dataResponse)
                        val lectAdapter = LecturerNameAdapter(lecturers)
                        lecturersRecyclerView.adapter = lectAdapter
                    }
                } else {
                    println("Не успешно")
                    if (response.code() == 403){
                        showToastNotification(requireContext(), "Недостаточно прав доступа для выполнения")
                    }
                }
            }

            override fun onFailure(call: Call<List<RequestDto>>, t: Throwable) {
                println("Ошибка")
                println(t)
            }
        })
    }

    private fun generate() {
        val token: String? = SessionManager.getToken(requireContext())
        Log.d("API Request failed", "${token}")

        val call = timetableApi.generate(
            "Bearer ${token}"
        )

        call.enqueue(object : Callback<Void> {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                if (response.isSuccessful) {
                    stopAnimation(generateBtn)
                    Log.d("API Request Successful", "${response.code()}")
                    showToastNotification(requireContext(), "Расписание сформировано")
                } else {
                    stopAnimation(generateBtn)
                    if (response.code() == 400) {
                        showToastNotification(requireContext(), "Расписание не может быть составлено,\n" +
                                "Расписание уже было составлено")
                    }
                    if (response.code() == 403) {
                        showToastNotification(requireContext(), "Недостаточно прав доступа для выполнения")
                    }
                    if (response.code() == 404) {
                        showToastNotification(requireContext(), "Неверный username пользователя")
                    }
                    println("Не успешно")
                    Log.d("Не успешно", "Ошибка, код - ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                stopAnimation(generateBtn)
                println("Ошибка")
                println(t)
            }
        })
    }

    private fun clear() {
        val token: String? = SessionManager.getToken(requireContext())
        Log.d("API Request failed", "${token}")

        val call = timetableApi.clearTimetable(
            "Bearer $token"
        )

        call.enqueue(object : Callback<Void> {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                if (response.isSuccessful) {
                    clearButtonStopAnimation(clearTTBtn)
                    Log.d("API Request Successful", "${response.code()}")
                    showToastNotification(requireContext(), "Расписание очищено")
                } else {
                    clearButtonStopAnimation(clearTTBtn)
                    if (response.code() == 404) {
                        showToastNotification(requireContext(), "Неверный username пользователя")
                    }
                    if (response.code() == 403) {
                        showToastNotification(requireContext(), "Недостаточно прав доступа для выполнения")
                    }
                    println("Не успешно")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                clearButtonStopAnimation(clearTTBtn)
                println("Ошибка")
                println(t)
            }
        })
    }

    private fun getLecturersList(requests: List<RequestDto>): List<String> {
        val names = mutableSetOf<String>()
        for (request in requests) {
            val fullName = request.userDto.fullName
            val parts = fullName.split(" ")
            val shortenedParts = parts.mapIndexed { index, part ->
                if (index == 0) {
                    part
                } else {
                    "${part.first()}."
                }
            }
            names.add(shortenedParts.joinToString(" "))
        }
        return names.toList()
    }

    private fun stopAnimation(btn: CircularProgressButton) {
        btn.background = ContextCompat.getDrawable(requireContext(), R.drawable.lecturer_bg)
        btn.revertAnimation()
    }

    private fun clearButtonStopAnimation(btn: CircularProgressButton) {
        btn.background = ContextCompat.getDrawable(requireContext(), R.drawable.clear_btn)
        btn.revertAnimation()
    }
}