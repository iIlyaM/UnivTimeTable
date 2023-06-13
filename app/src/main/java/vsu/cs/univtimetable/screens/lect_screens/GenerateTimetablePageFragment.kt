package vsu.cs.univtimetable.screens.lect_screens

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vsu.cs.univtimetable.R
import vsu.cs.univtimetable.SessionManager
import vsu.cs.univtimetable.TimetableClient
import vsu.cs.univtimetable.api.TimetableApi
import vsu.cs.univtimetable.dto.RequestDto
import vsu.cs.univtimetable.screens.adapter.LecturerNameAdapter

class GenerateTimetablePageFragment : Fragment() {

    private lateinit var timetableApi: TimetableApi
    private lateinit var lecturers: List<String>

    private lateinit var lecturersRecyclerView: RecyclerView

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

        val generateBtn = view.findViewById<AppCompatButton>(R.id.generateTTBtn)
        generateBtn.setOnClickListener {
            generate()
        }

        val clearTTBtn = view.findViewById<AppCompatButton>(R.id.clearTTBtn)
        clearTTBtn.setOnClickListener {
            clear()
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
                    Log.d("API Request Successful", "${response.code()}")
                    showToastNotification("Расписание сформировано")
                } else {
                    println("Не успешно")
                    Log.d("Не успешно", "Ошибка, код - ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
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
                    Log.d("API Request Successful", "${response.code()}")
                    showToastNotification("Расписание очищено")
                } else {
                    println("Не успешно")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
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

    private fun showToastNotification(message: String) {
        val duration = Toast.LENGTH_LONG

        val toast = Toast.makeText(requireContext(), message, duration)
        toast.show()
        val handler = Handler()
        handler.postDelayed({ toast.cancel() }, 1500)
    }
}