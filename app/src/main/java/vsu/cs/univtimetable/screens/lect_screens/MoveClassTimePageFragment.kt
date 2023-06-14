package vsu.cs.univtimetable.screens.lect_screens

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import android.widget.Toast
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
import vsu.cs.univtimetable.dto.MoveClassRequest
import vsu.cs.univtimetable.dto.MoveClassResponse

class MoveClassTimePageFragment : Fragment() {

    private lateinit var timetableApi: TimetableApi

    private lateinit var subjectCompleteView: AutoCompleteTextView

    private lateinit var dayAutoInput: TextInputLayout
    private lateinit var classTimeInputLayout: TextInputLayout
    private lateinit var weekTypeInputLayout: TextInputLayout
    private lateinit var audienceInputLayout: TextInputLayout
    private lateinit var dayTimeInputLayout: TextInputLayout


    private var subjects = mutableSetOf<String>()
    private var subjDayTime = mutableMapOf<String, List<ClassDto>>()
    private var dayToClassMap = mutableMapOf<String, ClassDto>()
    private var classes = mutableListOf<ClassDto>()
    private var weekTime = mutableMapOf<String, DayTime>()
    private var audiences = listOf<AudienceToMoveResponse>()
    private var possibleTimes = mutableMapOf<Int, AudienceToMoveResponse>()
    private var days = mutableSetOf<String>()
    private var times = mutableSetOf<String>()
    private var dayTimes = mutableListOf<String>()
    private var audienceDayTimeMap = mutableMapOf<DayTime, ClassDto>()
    private var strToAudienceNum = mutableMapOf<String, Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        timetableApi = TimetableClient.getClient().create(TimetableApi::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_move_class_time_page, container, false)

        val prevPageButton = view.findViewById<ImageButton>(R.id.prevPageButton)
        prevPageButton.setOnClickListener {
            findNavController().navigate(R.id.action_moveClassTimePageFragment_to_lecturerMainPageFragment)
        }

        subjectCompleteView =
            view.findViewById(R.id.subjAutoCompleteTextView)
        val dayAutoCompleteTextView =
            view.findViewById<AutoCompleteTextView>(R.id.dayAutoCompleteTextView)
        dayAutoCompleteTextView.visibility = View.GONE
        dayAutoInput =
            view.findViewById<TextInputLayout>(R.id.dayInputLayout)
        dayAutoInput.endIconMode = END_ICON_NONE
        val dayAdapter =
            ArrayAdapter(requireContext(), R.layout.subj_item, DateManager.WEEK_DAYS.toArray())
        dayAutoCompleteTextView.setAdapter(dayAdapter)

        val classTimeAutoCompleteTextView =
            view.findViewById<AutoCompleteTextView>(R.id.classTimeAutoCompleteTextView)
        classTimeAutoCompleteTextView.visibility = View.GONE
        classTimeInputLayout =
            view.findViewById<TextInputLayout>(R.id.classTimeInputLayout)
        classTimeInputLayout.endIconMode = END_ICON_NONE


        val weekTypeAutoCompleteTextView =
            view.findViewById<AutoCompleteTextView>(R.id.weekTypeAutoCompleteTextView)
        weekTypeAutoCompleteTextView.visibility = View.GONE
        weekTypeInputLayout =
            view.findViewById<TextInputLayout>(R.id.weekTypeInputLayout)
        weekTypeInputLayout.endIconMode = END_ICON_NONE

        audienceInputLayout =
            view.findViewById<TextInputLayout>(R.id.audienceInputLayout)
        audienceInputLayout.endIconMode = END_ICON_NONE
        val audienceAutoCompleteTextView =
            view.findViewById<AutoCompleteTextView>(R.id.audienceAutoCompleteTextView)
        audienceAutoCompleteTextView.visibility = View.GONE

        val dayTimeAutoCompleteTextView =
            view.findViewById<AutoCompleteTextView>(R.id.dayTimeAutoCompleteTextView)
        dayTimeAutoCompleteTextView.visibility = View.GONE
        dayTimeInputLayout =
            view.findViewById<TextInputLayout>(R.id.dayTimeInputLayout)
        dayTimeInputLayout.endIconMode = END_ICON_NONE

        subjectCompleteView.setOnItemClickListener { parent, view, position, id ->
            subjectCompleteView.setSelection(0)
            val selectedItem = parent.getItemAtPosition(position) as String
            subjectCompleteView.clearFocus()
            dayAutoCompleteTextView.clearFocus()
            classTimeAutoCompleteTextView.clearFocus()
            weekTypeAutoCompleteTextView.clearFocus()
            setDay(selectedItem, subjDayTime, days)
            setAdapterToView(dayAutoCompleteTextView, days.toList())
            dayAutoCompleteTextView.setVisibility(View.VISIBLE)
            dayAutoInput.endIconMode = TextInputLayout.END_ICON_DROPDOWN_MENU

            dayAutoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
                dayAutoCompleteTextView.setSelection(0)
                val selectedItem = parent.getItemAtPosition(position) as String
                subjectCompleteView.clearFocus()
                dayAutoCompleteTextView.clearFocus()
                classTimeAutoCompleteTextView.clearFocus()
                weekTypeAutoCompleteTextView.clearFocus()
                setTime(subjectCompleteView.text.toString(), selectedItem, subjDayTime, times)
                setAdapterToView(classTimeAutoCompleteTextView, times.toList())
                classTimeAutoCompleteTextView.setVisibility(View.VISIBLE)
                classTimeInputLayout.endIconMode = TextInputLayout.END_ICON_DROPDOWN_MENU

                classTimeAutoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
                    classTimeAutoCompleteTextView.setSelection(0)
                    val selectedItem = parent.getItemAtPosition(position) as String
                    subjectCompleteView.clearFocus()
                    dayAutoCompleteTextView.clearFocus()
                    classTimeAutoCompleteTextView.clearFocus()
                    weekTypeAutoCompleteTextView.clearFocus()
                    setAdapterToView(
                        weekTypeAutoCompleteTextView, getWeekTypes(
                            subjectCompleteView.text.toString(),
                            dayAutoCompleteTextView.text.toString(),
                            classTimeAutoCompleteTextView.text.toString()
                        )
                    )
                    weekTypeAutoCompleteTextView.setVisibility(View.VISIBLE)
                    weekTypeInputLayout.endIconMode = TextInputLayout.END_ICON_DROPDOWN_MENU

                    weekTypeAutoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
                        weekTypeAutoCompleteTextView.setSelection(0)
                        val selectedItem = parent.getItemAtPosition(position) as String
                        toPossibleTimes(
                            dayAutoCompleteTextView,
                            classTimeAutoCompleteTextView,
                            weekTypeAutoCompleteTextView
                        )
                        val audAdapter = ArrayAdapter(
                            requireContext(),
                            R.layout.move_class_item,
                            strToAudienceNum.keys.toList()
                        )
                        audienceAutoCompleteTextView.setAdapter(audAdapter)
                        audienceAutoCompleteTextView.setVisibility(View.VISIBLE)
                        audienceInputLayout.endIconMode = TextInputLayout.END_ICON_DROPDOWN_MENU

                        audienceAutoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
                            audienceAutoCompleteTextView.setSelection(0)
                            val selectedItem = parent.getItemAtPosition(position) as String
                            dayTimeAutoCompleteTextView.setVisibility(View.VISIBLE)
                            dayTimeAutoCompleteTextView.setSelection(0)
                            dayTimeInputLayout.endIconMode = TextInputLayout.END_ICON_DROPDOWN_MENU
                            setNewDate(
                                possibleTimes,
                                strToAudienceNum[selectedItem]!!,
                                dayTimes,
                                weekTime
                            )
                            setAdapterToView(dayTimeAutoCompleteTextView, dayTimes)
                        }
                    }
                }
            }
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
        if (dayAutoCompleteTextView.text.isEmpty())
        {
            dayAutoCompleteTextView.error = "Выберите исходную дату переносимого занятия"
            return
        }
        if (classTimeAutoCompleteTextView.text.isEmpty())
        {
            classTimeAutoCompleteTextView.error = "Выберите исходное время переносимого занятия"
            return
        }
        if (audienceAutoCompleteTextView.text.isEmpty() || audienceAutoCompleteTextView.text.toString().toInt() < 0)
        {
            audienceAutoCompleteTextView.error = "Выберите аудиторию для переноса"
            return
        }
        if (dayTimeAutoCompleteTextView.text.isEmpty()) {
            dayTimeAutoCompleteTextView.error = "Выберите день и время для переноса"
            return
        }
        if (weekTypeAutoCompleteTextView.text.isEmpty()) {
            weekTypeAutoCompleteTextView.error = "Выберите тип недели, на которую будет перенос "
            return
        }
        dayTimeAutoCompleteTextView.setSelection(0)
        val token: String? = SessionManager.getToken(requireContext())
        val keyDto = DayTime(
            dayAutoCompleteTextView.text.toString(),
            weekTypeAutoCompleteTextView.text.toString(),
            classTimeAutoCompleteTextView.text.toString(),
            subjectCompleteView.text.toString(),
        )

        val startDto = audienceDayTimeMap[keyDto]
        val bundle = Bundle()

        val call = timetableApi.move(
            "Bearer ${token}",
            MoveClassRequest(
                startDto!!,
                ClassDto(
                    startDto.subjectName,
                    weekTime.get(dayTimeAutoCompleteTextView.text.toString())!!.time,
                    "",
                    strToAudienceNum[audienceAutoCompleteTextView.text.toString()]!!,
                    weekTime.get(dayTimeAutoCompleteTextView.text.toString())!!.dayOfWeek,
                    startDto.typeOfClass,
                    weekTime.get(dayTimeAutoCompleteTextView.text.toString())!!.weekType,
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
                    showToastNotification("Занятие перенесено")
                    findNavController().navigate(R.id.action_moveClassTimePageFragment_to_lecturerMainPageFragment)
                } else {
                    if (response.code() == 400) {
                        showToastNotification("Аудитория занята для переноса")
                    }
                    if (response.code() == 403) {
                        showToastNotification("Недостаточно прав доступа для выполнения")
                    }
                    if (response.code() == 404) {
                        showToastNotification("Неверный username пользователя")
                    }
                    Log.d("Перенос не произошёл", "Получили ${response.code()}")
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
                    showToastNotification("Расписание ещё не сформировано")
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
            for (weekItem in day.weekTimes.entries) {
                for (time in weekItem.value) {
                    val str =
                        "${weekItem.key}, ${time}, ${DateManager.WEEK_DAYS_SHORT[day.dayOfWeek]}"
                    list.add(str)
                    weekTime.put(
                        str,
                        DayTime(day.dayOfWeek, weekItem.key, time, null)
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
            for (aud in audience) {
                possibleTimes[aud.key.audienceNumber] = aud.key
                strToAudienceNum["${aud.key.audienceNumber}, оборудование: ${aud.value}/${classDto.equipments.size}, мест: ${aud.key.capacity} "] =
                    aud.key.audienceNumber
            }
        } else {
            val audience = possibleAudience
            audience.sortBy { it.capacity }
            for (aud in audience) {
                possibleTimes[aud.audienceNumber] = aud
                strToAudienceNum["${aud.audienceNumber}, мест: ${aud.capacity}"] =
                    aud.audienceNumber
            }
        }
    }

    private fun checkEquipment(
        equipment: List<String>,
        audienceList: List<AudienceToMoveResponse>
    ): Map<AudienceToMoveResponse, Int> {
        var responseToIntMap = mutableMapOf<AudienceToMoveResponse, Int>()
        var resultToIntMap = mutableMapOf<AudienceToMoveResponse, Int>()
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
        for (entry in result.entries.reversed()) {
            resultToIntMap[entry.key] = entry.value
        }
        return resultToIntMap
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

    private fun setDay(
        subject: String,
        subjDayTime: MutableMap<String, List<ClassDto>>,
        days: MutableSet<String>,
    ) {
        val list = subjDayTime[subject]
        for (subj in list!!) {
            days.add(subj.dayOfWeek)
            dayToClassMap[subj.dayOfWeek] = subj
        }
    }

    private fun setTime(
        subject: String,
        day: String,
        subjDayTime: MutableMap<String, List<ClassDto>>,
        times: MutableSet<String>,
    ) {
        val list = subjDayTime[subject]
        for (classesDto in list!!) {
            if (day == classesDto.dayOfWeek) {
                times.add(classesDto.startTime)
            }
        }
    }

    private fun setAdapterToView(textView: AutoCompleteTextView, list: List<String>) {
        val adapter = ArrayAdapter(requireContext(), R.layout.move_class_item, list)
        textView.setAdapter(adapter)
        textView.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position) as String
        }
    }

    private fun getWeekTypes(subject: String, day: String, time: String): MutableList<String> {
        val set = mutableSetOf<String>()
        for (classDto in audienceDayTimeMap.values) {
            if (classDto.subjectName == subject && classDto.dayOfWeek == day && classDto.startTime == time)
                set.add(classDto.weekType)
        }
        return set.toMutableList()
    }

    private fun showToastNotification (message: String) {
        val duration = Toast.LENGTH_LONG

        val toast = Toast.makeText(requireContext(), message, duration)
        toast.show()
        val handler = Handler()
        handler.postDelayed({ toast.cancel() }, 1500)
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
//        findNavController().navigate(R.id.action_moveClassTimePageFragment_to_lecturerMainPageFragment)
//    }
}