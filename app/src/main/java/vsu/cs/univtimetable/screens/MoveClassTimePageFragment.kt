package vsu.cs.univtimetable.screens

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.annotation.RequiresApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vsu.cs.univtimetable.DateManager
import vsu.cs.univtimetable.R
import vsu.cs.univtimetable.SessionManager
import vsu.cs.univtimetable.TimetableClient
import vsu.cs.univtimetable.api.TimetableApi
import vsu.cs.univtimetable.dto.ClassDto
import vsu.cs.univtimetable.dto.DayTime
import vsu.cs.univtimetable.dto.DayTimes
import vsu.cs.univtimetable.dto.MoveClassDto
import vsu.cs.univtimetable.dto.MoveClassResponse
import vsu.cs.univtimetable.dto.RequestDataDto
import vsu.cs.univtimetable.screens.adapter.GroupAdapter
import java.time.LocalTime

class MoveClassTimePageFragment : Fragment() {


//        arrayListOf("Физика", "ЯП Java", "ТИПИС", "Квант. теория", "Физика", "ЯП Java", "ТИПИС", "Квант. теория", "Физика", "ЯП Java", "ТИПИС", "Квант. теория")

    private lateinit var timetableApi: TimetableApi
    private var subjects = mutableSetOf<String>()
    private var subjDayTime = mutableMapOf<String, List<ClassDto>>()
    private var classes = mutableListOf<ClassDto>()
    private var weekTime = mutableMapOf<String, DayTime>()
    private lateinit var possibleTimes: Map<Int, List<DayTimes>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        timetableApi = TimetableClient.getClient().create(TimetableApi::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_move_class_time_page, container, false)
        val view = inflater.inflate(R.layout.fragment_move_class_time_page, container, false)

        val subjectCompleteView =
            view.findViewById<AutoCompleteTextView>(R.id.subjAutoCompleteTextView)
        val dayAutoCompleteTextView =
            view.findViewById<AutoCompleteTextView>(R.id.dayAutoCompleteTextView)
        val classTimeAutoCompleteTextView =
            view.findViewById<AutoCompleteTextView>(R.id.classTimeAutoCompleteTextView)
        val audienceAutoCompleteTextView =
            view.findViewById<AutoCompleteTextView>(R.id.audienceAutoCompleteTextView)
        val dayTimeAutoCompleteTextView =
            view.findViewById<AutoCompleteTextView>(R.id.dayTimeAutoCompleteTextView)
        val dayAdapter =
            ArrayAdapter(requireContext(), R.layout.subj_item, DateManager.WEEK_DAYS.toArray())
        dayAutoCompleteTextView.setAdapter(dayAdapter)

        val adapter = ArrayAdapter(requireContext(), R.layout.move_class_item, subjects.toList())
        subjectCompleteView.setAdapter(adapter)
//        getRequestData()

        subjectCompleteView.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position) as String
            val classes = subjDayTime[selectedItem]!!
            val days = mutableListOf<String>()
            val times = mutableListOf<String>()
            setDayTime(selectedItem, subjDayTime, days, times)
//            for (day in classes) {
//                days.add(day.dayOfWeek)
//                times.add(day.startTime)
//            }
            setAdapterToView(dayAutoCompleteTextView, days)
            setAdapterToView(classTimeAutoCompleteTextView, times)
        }

        val audAdapter = ArrayAdapter(requireContext(), R.layout.move_class_item, possibleTimes.keys.toList())
        audienceAutoCompleteTextView.setAdapter(audAdapter)
        audienceAutoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position) as String
//            setNewDate()
        }
//
//        setSourceDataListener(
//            subjectCompleteView,
//            subjects.toList(),
//            dayAutoCompleteTextView,
//            classTimeAutoCompleteTextView
//        )

//        setListener(subjectCompleteView, subjects)
//        setListener(dayAutoCompleteTextView, subjects)
//        setListener(classTimeAutoCompleteTextView, subjects)
//        setListener(audienceAutoCompleteTextView, subjects)
//        setListener(dayTimeAutoCompleteTextView, subjects)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getRequestData()
    }

    private fun move() {
        val token: String? = SessionManager.getToken(requireContext())

        Log.d("API Request failed", "${token}")
        val call = timetableApi.move("Bearer ${token}")

        call.enqueue(object : Callback<Void> {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                if (response.isSuccessful) {
                    Log.d("API Request successful", "Получили ${response.code()}")
                    val dataResponse = response.body()
                    println(dataResponse)
                    if (dataResponse != null) {
                    }
                } else {
                    Log.d("Не успешно", "Получили ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                println("Ошибка")
                println(t)
            }
        })
    }

    private fun getRequestData() {
        val token: String? = SessionManager.getToken(requireContext())

        Log.d("API Request failed", "${token}")
        val call = timetableApi.getMoveClassData("Bearer ${token}")


        call.enqueue(object : Callback<MoveClassResponse> {
            override fun onResponse(
                call: Call<MoveClassResponse>,
                response: Response<MoveClassResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("API Request successful", "Получили ${response.code()}")
                    val dataResponse = response.body()
                    println(dataResponse)
                    if (dataResponse != null) {
                        getSubjects(dataResponse.coursesClasses)
                        toClassDtoMap(subjects, classes, subjDayTime)
                        possibleTimes = dataResponse.possibleTimesInAudience
                    }
                } else {
                    Log.d("Не успешно", "Получили ${response.code()}")
                }
            }

            override fun onFailure(call: Call<MoveClassResponse>, t: Throwable) {
                println("Ошибка")
                println(t)
            }
        })
    }

    private fun setNewDate(
        possibleTimesInAudience: Map<Int, DayTimes>,
        audience: Int,
        list: MutableList<String>,
        weekTime: MutableMap<String, DayTime>
    ) {
        val day = possibleTimesInAudience[audience]!!
        for (weekItem in day.weekTimes.values) {
            for (time in weekItem) {
                val str =
                    "${day.weekTimes.keys.first()}, ${time}, ${DateManager.WEEK_DAYS_SHORT[day.dayOfWeek]}"
                list.add(str)
                weekTime.put(str, DayTime(day.dayOfWeek, day.weekTimes.keys.first(), time))
            }
        }

    }

    private fun getSubjects(coursesClasses: Map<Int, List<MoveClassDto>>) {
        for (list in coursesClasses.values) {
            for (dto in list) {
                for (classDto in dto.groupClasses) {
                    subjects.add(classDto.subjectName)
                    classes.add(classDto)
                }
            }
        }
    }

    private fun toClassDtoMap(
        subjects: Set<String>,
        classes: MutableList<ClassDto>,
        subjDayTime: MutableMap<String, List<ClassDto>>
    ) {
        val list = mutableListOf<ClassDto>()
        for (sub in subjects) {
            for (item in classes) {
                if (sub == item.subjectName) {
                    list.add(item)
                }
            }
            subjDayTime[sub] = list
            list.clear()
        }
    }

    private fun setDayTime(
        subject: String,
        subjDayTime: MutableMap<String, List<ClassDto>>,
        days: MutableList<String>,
        time: MutableList<String>
    ) {
        val list = subjDayTime[subject]
        for (subj in list!!) {
            days.add(subj.dayOfWeek)
            time.add(subj.startTime)
        }
    }


//    private fun setSourceDataListener(
//        textView: AutoCompleteTextView,
//        list: List<String>,
//        dayView: AutoCompleteTextView,
//        timeView: AutoCompleteTextView
//    ) {
//        val adapter = ArrayAdapter(requireContext(), R.layout.move_class_item, list)
//        textView.setAdapter(adapter)
//        textView.setOnItemClickListener { parent, view, position, id ->
//            val selectedItem = parent.getItemAtPosition(position) as String
//            val classes = subjDayTime[selectedItem]!!
//            val days = mutableListOf<String>()
//            val times = mutableListOf<String>()
//            for (day in classes) {
//                days.add(day.dayOfWeek)
//                times.add(day.startTime)
//            }
//            setAdapterToView(dayView, days)
//            setAdapterToView(timeView, times)
//        }
//    }

    private fun setTargetDataListener(
        textView: AutoCompleteTextView,
        list: List<String>,
        audienceView: AutoCompleteTextView,
        timeView: AutoCompleteTextView
    ) {
        val adapter = ArrayAdapter(requireContext(), R.layout.move_class_item, list)
        textView.setAdapter(adapter)
        textView.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position) as String
//            setNewDate()
        }
    }

    private fun setAdapterToView(textView: AutoCompleteTextView, list: List<String>) {
        val adapter = ArrayAdapter(requireContext(), R.layout.move_class_item, list)
        textView.setAdapter(adapter)
        textView.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position) as String
        }
    }
}