package vsu.cs.univtimetable.screens.lect_screens

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vsu.cs.univtimetable.R
import vsu.cs.univtimetable.SessionManager
import vsu.cs.univtimetable.TimetableClient
import vsu.cs.univtimetable.api.TimetableApi
import vsu.cs.univtimetable.dto.RequestDto
import vsu.cs.univtimetable.dto.SendRequest

class GenerateTimetablePageFragment : Fragment() {

    private lateinit var timetableApi: TimetableApi
    private lateinit var lecturers: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        timetableApi = TimetableClient.getClient().create(TimetableApi::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_generate_timetable_page, container, false)

        val lecturersRecyclerView = view.findViewById<RecyclerView>(R.id.lecturersRecyclerView)

        val generateBtn = view.findViewById<AppCompatButton>(R.id.generateTTBtn)
        generateBtn.setOnClickListener {
            generate()
        }
        return view
    }

    private fun getLecturers() {
//        val token: String? = SessionManager.getToken(requireContext())
//        Log.d("API Request failed", "${token}")
//
//        val call = timetableApi.getLecturers(
//            "Bearer ${token}"
//        )
//
//        call.enqueue(object : Callback<List<RequestDto>> {
//            override fun onResponse(
//                call: Call<List<RequestDto>>,
//                response: Response<List<RequestDto>>
//            ) {
//                if (response.isSuccessful) {
//                    Log.d("API Request Successful", "${response.code()}")
//                    val dataResponse = response.body()
//                    println(dataResponse)
//                        lecturers = getLecturersList(response.body()!!)
//                } else {
//                    println("Не успешно")
//                }
//            }
//
//            override fun onFailure(call: Call<List<RequestDto>>, t: Throwable) {
//                println("Ошибка")
//                println(t)
//            }
//        })
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

    private fun getLecturersList(requests: List<RequestDto>): MutableList<String> {
        val names = mutableListOf<String>()
        for (request in requests) {
            val fullName = request.user.fullName
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
        return names
    }
}