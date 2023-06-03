package vsu.cs.univtimetable.screens

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vsu.cs.univtimetable.R
import vsu.cs.univtimetable.SessionManager
import vsu.cs.univtimetable.TimetableClient
import vsu.cs.univtimetable.api.TimetableApi
import vsu.cs.univtimetable.dto.TimetableResponse
import vsu.cs.univtimetable.dto.UnivResponseDto
import vsu.cs.univtimetable.screens.adapter.DayOfWeekAdapter
import vsu.cs.univtimetable.screens.adapter.LecturerTimetableAdapter

class LecturerTimetablePageFragment : Fragment() {

    private lateinit var timetableApi: TimetableApi
    private lateinit var recyclerView: RecyclerView
    private lateinit var dayAdapter: DayOfWeekAdapter
    private lateinit var timeTableAdapter: LecturerTimetableAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        timetableApi = TimetableClient.getClient().create(TimetableApi::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_lecturer_timetable_page, container, false)
        recyclerView = view.findViewById(R.id.lecturerRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getUniversities()
    }

    private fun getUniversities() {
        val token: String? = SessionManager.getToken(requireContext())
        Log.d("API Request failed", "${token}")
        val call = timetableApi.getTimetable("Bearer ${token}")


        call.enqueue(object : Callback<TimetableResponse> {
            override fun onResponse(
                call: Call<TimetableResponse>,
                response: Response<TimetableResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("API Request successful", "Получили ${response.code()}")
                    val dataResponse = response.body()
                    println(dataResponse)
                    if (dataResponse != null) {
                        timeTableAdapter = LecturerTimetableAdapter(
                            requireContext(),
                            dataResponse.classes["Числитель"]!!["Понедельник"]!!
                        )
                    }
                    recyclerView.adapter = timeTableAdapter

                } else {
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

}