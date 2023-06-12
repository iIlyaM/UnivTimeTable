package vsu.cs.univtimetable.screens

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textfield.TextInputLayout.END_ICON_NONE
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vsu.cs.univtimetable.DateManager
import vsu.cs.univtimetable.R
import vsu.cs.univtimetable.SessionManager
import vsu.cs.univtimetable.TimetableClient
import vsu.cs.univtimetable.api.TimetableApi
import vsu.cs.univtimetable.dto.AudienceToMoveResponse
import vsu.cs.univtimetable.dto.ClassDto
import vsu.cs.univtimetable.dto.DayTime
import vsu.cs.univtimetable.dto.MoveClassDto
import vsu.cs.univtimetable.dto.MoveClassRequest
import vsu.cs.univtimetable.dto.MoveClassResponse

class MoveClassTimePageFragment : Fragment() {

    private lateinit var timetableApi: TimetableApi

    private lateinit var subjectCompleteView: AutoCompleteTextView
//    private lateinit var dayAutoCompleteTextView: AutoCompleteTextView
//    private lateinit var classTimeAutoCompleteTextView: AutoCompleteTextView
//    private lateinit var audienceAutoCompleteTextView: AutoCompleteTextView
//    private lateinit var weekTypeAutoCompleteTextView: AutoCompleteTextView
//    private lateinit var dayTimeAutoCompleteTextView: AutoCompleteTextView
    private var subjects = mutableSetOf<String>()
    private var subjDayTime = mutableMapOf<String, List<ClassDto>>()
    private var classes = mutableListOf<ClassDto>()
    private var weekTime = mutableMapOf<String, DayTime>()
    private var audiences = listOf<AudienceToMoveResponse>()
    private var possibleTimes = mutableMapOf<Int, AudienceToMoveResponse>()
    private var days = mutableSetOf<String>()
    private var times = mutableListOf<String>()
    private var dayTimes = mutableListOf<String>()
    private var audienceDayTimeMap = mutableMapOf<DayTime, ClassDto>()
    private var weekTypeList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        timetableApi = TimetableClient.getClient().create(TimetableApi::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_move_class_time_page, container, false)

        subjectCompleteView =
            view.findViewById(R.id.subjAutoCompleteTextView)
        val dayAutoCompleteTextView =
            view.findViewById<AutoCompleteTextView>(R.id.dayAutoCompleteTextView)
        dayAutoCompleteTextView.setVisibility(View.GONE)
        val dayAutoInput =
        view.findViewById<TextInputLayout>(R.id.dayInputLayout)
        dayAutoInput.endIconMode = END_ICON_NONE
        val dayAdapter =
            ArrayAdapter(requireContext(), R.layout.subj_item, DateManager.WEEK_DAYS.toArray())
        dayAutoCompleteTextView.setAdapter(dayAdapter)

        val classTimeAutoCompleteTextView =
            view.findViewById<AutoCompleteTextView>(R.id.classTimeAutoCompleteTextView)
        classTimeAutoCompleteTextView.setVisibility(View.GONE)
        val classTimeInputLayout =
            view.findViewById<TextInputLayout>(R.id.classTimeInputLayout)
        classTimeInputLayout.endIconMode = END_ICON_NONE


        val weekTypeAutoCompleteTextView =
            view.findViewById<AutoCompleteTextView>(R.id.weekTypeAutoCompleteTextView)
        weekTypeAutoCompleteTextView.setVisibility(View.GONE)
        val weekTypeInputLayout =
            view.findViewById<TextInputLayout>(R.id.weekTypeInputLayout)
//        val weekTypeAdapter =
//            ArrayAdapter(requireContext(), R.layout.subj_item, listOf("Числитель", "Знаменатель"))
//        weekTypeAutoCompleteTextView.setAdapter(weekTypeAdapter)
        weekTypeInputLayout.endIconMode = END_ICON_NONE

        val audienceInputLayout =
            view.findViewById<TextInputLayout>(R.id.audienceInputLayout)
        audienceInputLayout.endIconMode = END_ICON_NONE
        val audienceAutoCompleteTextView =
            view.findViewById<AutoCompleteTextView>(R.id.audienceAutoCompleteTextView)
        audienceAutoCompleteTextView.setVisibility(View.GONE)

        val dayTimeAutoCompleteTextView =
            view.findViewById<AutoCompleteTextView>(R.id.dayTimeAutoCompleteTextView)
        dayTimeAutoCompleteTextView.setVisibility(View.GONE)
        val dayTimeInputLayout =
            view.findViewById<TextInputLayout>(R.id.dayTimeInputLayout)
        dayTimeInputLayout.endIconMode = END_ICON_NONE

        subjectCompleteView.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position) as String
            subjectCompleteView.clearFocus()
            dayAutoCompleteTextView.clearFocus()
            classTimeAutoCompleteTextView.clearFocus()
            weekTypeAutoCompleteTextView.clearFocus()
            val classes = subjDayTime[selectedItem]!!
            setDayTime(selectedItem, subjDayTime, days, times)
            setAdapterToView(dayAutoCompleteTextView, days.toList())
            dayAutoCompleteTextView.setVisibility(View.VISIBLE)
            dayAutoInput.endIconMode = TextInputLayout.END_ICON_DROPDOWN_MENU

            dayAutoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
                val selectedItem = parent.getItemAtPosition(position) as String
                subjectCompleteView.clearFocus()
                dayAutoCompleteTextView.clearFocus()
                classTimeAutoCompleteTextView.clearFocus()
                weekTypeAutoCompleteTextView.clearFocus()
                setAdapterToView(classTimeAutoCompleteTextView, times.toList())
                classTimeAutoCompleteTextView.setVisibility(View.VISIBLE)
                classTimeInputLayout.endIconMode = TextInputLayout.END_ICON_DROPDOWN_MENU

                classTimeAutoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
                    val selectedItem = parent.getItemAtPosition(position) as String
                    subjectCompleteView.clearFocus()
                    dayAutoCompleteTextView.clearFocus()
                    classTimeAutoCompleteTextView.clearFocus()
                    weekTypeAutoCompleteTextView.clearFocus()
                    setAdapterToView(weekTypeAutoCompleteTextView, getWeekTypes())
                    weekTypeAutoCompleteTextView.setVisibility(View.VISIBLE)
                    weekTypeInputLayout.endIconMode = TextInputLayout.END_ICON_DROPDOWN_MENU

                    weekTypeAutoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
                        val selectedItem = parent.getItemAtPosition(position) as String
                        toPossibleTimes(
                            dayAutoCompleteTextView,
                            classTimeAutoCompleteTextView,
                            weekTypeAutoCompleteTextView
                        )
                        val audAdapter = ArrayAdapter(
                            requireContext(),
                            R.layout.move_class_item,
                            possibleTimes.keys.toList()
                        )
                        audienceAutoCompleteTextView.setAdapter(audAdapter)
                        audienceAutoCompleteTextView.setVisibility(View.VISIBLE)
                        audienceInputLayout.endIconMode = TextInputLayout.END_ICON_DROPDOWN_MENU

                        audienceAutoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
                            val selectedItem = parent.getItemAtPosition(position) as Int
                            dayTimeAutoCompleteTextView.setVisibility(View.VISIBLE)
                            dayTimeInputLayout.endIconMode = TextInputLayout.END_ICON_DROPDOWN_MENU
                            setNewDate(possibleTimes, selectedItem, dayTimes, weekTime)
                            setAdapterToView(dayTimeAutoCompleteTextView, dayTimes)
                        }
                    }
                }
            }
//            val audAdapter = ArrayAdapter(
//                requireContext(),
//                R.layout.move_class_item,
//                possibleTimes.keys.toList()
//            )
//            audienceAutoCompleteTextView.setAdapter(audAdapter)
        }

        val confirmSubjectBtn = view.findViewById<AppCompatButton>(R.id.confirmSubjectBtn)
        confirmSubjectBtn.setOnClickListener {
            move(
                dayAutoCompleteTextView,
                classTimeAutoCompleteTextView,
                audienceAutoCompleteTextView,
                dayTimeAutoCompleteTextView,
                weekTypeAutoCompleteTextView
            )
        }
        val requestListButton = view.findViewById<AppCompatButton>(R.id.requestListButton)
        requestListButton.setOnClickListener {
            findNavController().navigate(R.id.action_addSubjectPageFragment_to_generateTimetablePageFragment)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getRequestData()
    }

    private fun move(
        dayAutoCompleteTextView: AutoCompleteTextView,
        classTimeAutoCompleteTextView: AutoCompleteTextView,
        audienceAutoCompleteTextView: AutoCompleteTextView,
        dayTimeAutoCompleteTextView: AutoCompleteTextView,
        weekTypeAutoCompleteTextView: AutoCompleteTextView
    ) {
        val token: String? = SessionManager.getToken(requireContext())

        val keyDto = DayTime(
            dayAutoCompleteTextView.text.toString(),
            weekTypeAutoCompleteTextView.text.toString(),
            classTimeAutoCompleteTextView.text.toString(),
            subjectCompleteView.text.toString(),
        )

        val startDto = audienceDayTimeMap[keyDto]
        val audience = audienceAutoCompleteTextView.text.toString().toInt()
        val bundle = Bundle()

        val call = timetableApi.move(
            "Bearer ${token}",
            MoveClassRequest(
                startDto!!,
                ClassDto(
                    startDto.subjectName,
                    weekTime.get(dayTimeAutoCompleteTextView.text.toString())!!.time,
                    "",
                    audienceAutoCompleteTextView.text.toString().toInt(),
                    weekTime.get(dayTimeAutoCompleteTextView.text.toString())!!.dayOfWeek,
                    startDto.typeOfClass,
                    weekTypeAutoCompleteTextView.text.toString(),
                    startDto.courseNumber,
                    startDto.groupsNumber,
                    startDto.capacity,
                    startDto.equipments
                )
            )
        )

        call.enqueue(object : Callback<Void> {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                if (response.isSuccessful) {
                    Log.d("API Request successful", "Получили ${response.code()}")
                    val dataResponse = response.body()
                    println(dataResponse)
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
                        audiences = dataResponse.audienceToMoveResponses
                        val adapter = ArrayAdapter(
                            requireContext(),
                            R.layout.move_class_item,
                            subjects.toList()
                        )
                        subjectCompleteView.setAdapter(adapter)
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
        possibleTimesInAudience: Map<Int, AudienceToMoveResponse>,
        audience: Int,
        list: MutableList<String>,
        weekTime: MutableMap<String, DayTime>
    ) {
        val dayTimeItem = possibleTimesInAudience[audience]!!.dayTimes
        for (day in dayTimeItem) {
            for (weekItem in day.weekTimes.values) {
                for (time in weekItem) {
                    val str =
                        "${day.weekTimes.keys.first()}, ${time}, ${DateManager.WEEK_DAYS_SHORT[day.dayOfWeek]}"
                    list.add(str)
                    weekTime.put(
                        str,
                        DayTime(day.dayOfWeek, day.weekTimes.keys.first(), time, null)
                    )
                }
            }
        }
    }

    private fun getSubjects(coursesClasses: Map<Int, List<ClassDto>>) {
        for (list in coursesClasses.values) {
            for (dto in list) {
                subjects.add(dto.subjectName)
                classes.add(dto)
            }
        }
    }

    private fun toClassDtoMap(
        subjects: Set<String>,
        classes: MutableList<ClassDto>,
        subjDayTime: MutableMap<String, List<ClassDto>>
    ) {

        for (sub in subjects) {
            var list = mutableListOf<ClassDto>()
            for (item in classes) {
                if (sub == item.subjectName) {
                    list.add(item)
                    audienceDayTimeMap.put(
                        DayTime(
                            item.dayOfWeek,
                            item.weekType,
                            item.startTime,
                            item.subjectName
                        ), item
                    )
                }
            }
            subjDayTime[sub] = list
        }
    }

    private fun toPossibleTimes(
        dayAutoCompleteTextView: AutoCompleteTextView,
        classTimeAutoCompleteTextView: AutoCompleteTextView,
        weekTypeAutoCompleteTextView: AutoCompleteTextView
    ) {
        var possibleAudience = mutableListOf<AudienceToMoveResponse>()

        val classDto = getClassByDayTimeFields(
            dayAutoCompleteTextView,
            classTimeAutoCompleteTextView,
            weekTypeAutoCompleteTextView
        )
        for (obj in audiences) {
            if (obj.capacity >= classDto.capacity) {
                possibleAudience.add(obj)
            }
        }
        if (classDto.equipments.isNotEmpty()) {
            val audience = checkEquipment(classDto.equipments, possibleAudience)
            possibleTimes[audience.audienceNumber] = audience
        } else {
            val audience = possibleAudience
            audience.sortBy { it.capacity }
            val aud = audience.first()
            possibleTimes[aud.audienceNumber] = aud
        }

    }

    private fun checkEquipment(
        equipment: List<String>,
        audienceList: List<AudienceToMoveResponse>
    ): AudienceToMoveResponse {
        var responseToIntMap = mutableMapOf<AudienceToMoveResponse, Int>()
        for (item in equipment) {
            for (audience in audienceList) {
                var count = 0
                for (audienceItem in audience.equipments) {
                    if (item == audienceItem) {
                        count += 1
                    }
                }
                responseToIntMap[audience] = count
            }
        }
        val result = responseToIntMap.toList().sortedBy { (_, value) -> value }.toMap()
        return result.entries.toList()[result.size - 1].key
    }

    private fun getClassByDayTimeFields(
        dayAutoCompleteTextView: AutoCompleteTextView,
        classTimeAutoCompleteTextView: AutoCompleteTextView,
        weekTypeAutoCompleteTextView: AutoCompleteTextView
    ): ClassDto {
        val key = DayTime(
            dayAutoCompleteTextView.text.toString(),
            weekTypeAutoCompleteTextView.text.toString(),
            classTimeAutoCompleteTextView.text.toString(),
            subjectCompleteView.text.toString(),
        )
        return audienceDayTimeMap[key]!!
    }

    private fun setDayTime(
        subject: String,
        subjDayTime: MutableMap<String, List<ClassDto>>,
        days: MutableSet<String>,
        time: MutableList<String>
    ) {
        val list = subjDayTime[subject]
        for (subj in list!!) {
            days.add(subj.dayOfWeek)
            time.add(subj.startTime)
        }
    }

    private fun setAdapterToView(textView: AutoCompleteTextView, list: List<String>) {
        val adapter = ArrayAdapter(requireContext(), R.layout.move_class_item, list)
        textView.setAdapter(adapter)
        textView.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position) as String
        }
    }

    private fun getWeekTypes(): MutableList<String> {
        val set = mutableSetOf<String>()
        for (classDto in audienceDayTimeMap.values) {
            set.add(classDto.weekType)
        }
        return set.toMutableList()
    }
}