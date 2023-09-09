package vsu.cs.univtimetable.screens.lect_screens

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.github.leandroborgesferreira.loadingbutton.customViews.CircularProgressButton
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vsu.cs.univtimetable.R
import vsu.cs.univtimetable.utils.token_utils.SessionManager
import vsu.cs.univtimetable.TimetableClient
import vsu.cs.univtimetable.api.TimetableApi
import vsu.cs.univtimetable.dto.group.GroupResponse
import vsu.cs.univtimetable.dto.datetime.ImpossibleTimeDto
import vsu.cs.univtimetable.dto.classes.RequestDataDto
import vsu.cs.univtimetable.dto.classes.SendRequest
import vsu.cs.univtimetable.screens.adapter.GroupAdapter
import vsu.cs.univtimetable.utils.NotificationManager.showToastNotification


class AddSubjectPageFragment : Fragment(), GroupAdapter.OnItemClickListener {

    private var classTypes =
        arrayOf("Лекция", "Семинар")


    private lateinit var selectedEquipment: BooleanArray
    private lateinit var groupAdapter: GroupAdapter
    private var groupMap = mutableMapOf<String, GroupResponse>()
    private var groupList = ArrayList<String>()
    private var groups = mutableListOf<GroupResponse>()
    private var equipList = ArrayList<String>()
    private var chosenEquipment = mutableListOf<String>()
    private lateinit var dayWeekTimeMap: ImpossibleTimeDto
    private lateinit var confirm: CircularProgressButton


    private lateinit var timetableApi: TimetableApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        timetableApi = TimetableClient.getClient().create(TimetableApi::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_subject_page, container, false)


        val typeAdapter = ArrayAdapter(requireContext(), R.layout.subj_item, classTypes)

        val equipListView = view.findViewById<TextView>(R.id.selectEquipmentView)
        val editSubjectNameText = view.findViewById<EditText>(R.id.editSubjectNameText)
        val editHoursCountText = view.findViewById<EditText>(R.id.editHoursCountText)

        val classTypeCompleteView =
            view.findViewById<AutoCompleteTextView>(R.id.typeAutoCompleteText)
        confirm = view.findViewById(R.id.confirmSubjectBtn)
        classTypeCompleteView.setAdapter(typeAdapter)
        classTypeCompleteView.setOnItemClickListener { parent, view2, position, id ->
            val selectedItem = parent.getItemAtPosition(position) as String

        }

        val groupTextInputLayout =
            view.findViewById<TextInputLayout>(R.id.selectGroupView)
        groupTextInputLayout.boxStrokeColor =
            ContextCompat.getColor(requireContext(), R.color.lecturerColor)
        groupTextInputLayout.setBoxStrokeColorStateList(
            ContextCompat.getColorStateList(
                requireContext(),
                R.color.lecturer_selector
            )!!
        )
        groupTextInputLayout.boxStrokeWidth = resources.getDimensionPixelSize(R.dimen.new_stroke_width)

        val classTypeInputLayout = view.findViewById<TextInputLayout>(R.id.classTypeInputLayout)
        classTypeInputLayout.boxStrokeColor =
            ContextCompat.getColor(requireContext(), R.color.lecturerColor)
        classTypeInputLayout.setBoxStrokeColorStateList(
            ContextCompat.getColorStateList(
                requireContext(),
                R.color.lecturer_selector
            )!!
        )
        classTypeInputLayout.boxStrokeWidth = resources.getDimensionPixelSize(R.dimen.new_stroke_width)

        val prevPageButton = view.findViewById<ImageButton>(R.id.prevPageButton)
        prevPageButton.setOnClickListener {
            findNavController().navigate(R.id.action_addSubjectPageFragment_to_selectUnwantedTimePageFragment)
        }

        val mainPageButton = view.findViewById<ImageButton>(R.id.mainPageButton)
        mainPageButton.setOnClickListener {
            findNavController().navigate(R.id.action_addSubjectPageFragment_to_lecturerMainPageFragment)
        }

        val selectGroupAutoCompleteText =
            view.findViewById<AutoCompleteTextView>(R.id.selectGroupAutoCompleteText)

        val groupAdapter = ArrayAdapter(requireContext(), R.layout.subj_item, groupList)
        selectGroupAutoCompleteText.setAdapter(groupAdapter)
        val items = arrayOfNulls<String>(equipList.size)
        equipListView.setOnClickListener {
            showEquipmentDialog(equipList.toArray(items))
        }

        confirm.setOnClickListener {
            confirm.startAnimation()
            sendRequest(
                classTypeCompleteView,
                selectGroupAutoCompleteText,
                editSubjectNameText,
                editHoursCountText
            )
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getRequestData()
    }

    private fun sendRequest(
        classTypeCompleteView: AutoCompleteTextView,
        selectGroupAutoCompleteText: AutoCompleteTextView,
        editSubjectNameText: EditText,
        editHoursCountText: EditText,
    ) {
        val subject: String = editSubjectNameText.text.toString()
        val hours: Int = editHoursCountText.text.toString().toInt()
        val classType: String = classTypeCompleteView.text.toString()
        val group: String = selectGroupAutoCompleteText.text.toString()

        if (editSubjectNameText.text.isEmpty()) {
            editSubjectNameText.error = "Введите название предмета"
            stopAnimation(confirm)
            return
        }
        if (classTypeCompleteView.text.isEmpty()) {
            classTypeCompleteView.error = "Выберите тип предмета"
            stopAnimation(confirm)
            return
        }
        if (selectGroupAutoCompleteText.text.isEmpty()) {
            selectGroupAutoCompleteText.error = "Выберите группы"
            stopAnimation(confirm)
            return
        }
        if (editHoursCountText.text.isEmpty() || (hours <= 0 || hours > 9)) {
            editHoursCountText.error = "Введите количество часов в пределах от 0 до 9"
            stopAnimation(confirm)
            return
        }

        val token: String? = SessionManager.getToken(requireContext())
        Log.d("API Request failed", "${token}")

        val call = timetableApi.postSubject(
            "Bearer ${token}",
            SendRequest(
                subject,
                groupMap[group]!!,
                hours,
                classType,
                chosenEquipment,
                getUnwantedTime().impossibleTime
            )
        )

        call.enqueue(object : Callback<Void> {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                if (response.isSuccessful) {
                    stopAnimation(confirm)
                    Log.d("API Request Successful", "${response.code()}")
                    showToastNotification(requireContext(), "Заявка отправлена")
                    editSubjectNameText.text.clear()
                    editHoursCountText.text.clear()
                } else {
                    stopAnimation(confirm)
                    if (response.code() == 403) {
                        showToastNotification(
                            requireContext(),
                            "Недостаточно прав доступа для выполнения"
                        )
                    }
                    if (response.code() == 404) {
                        showToastNotification(
                            requireContext(), "Id переданной группы не было найдено/\n" +
                                    "Переданного инвентаря не существует в базе/\n" +
                                    "Неверный username пользователя"
                        )
                    }
                    println("Не успешно")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                stopAnimation(confirm)
                println("Ошибка")
                println(t)
            }
        })

    }

    private fun getUnwantedTime(): ImpossibleTimeDto {
        return requireArguments().getSerializable("map") as ImpossibleTimeDto
    }

    private fun getRequestData() {
        val token: String? = SessionManager.getToken(requireContext())

        val call = timetableApi.getRequestData("Bearer ${token}")


        call.enqueue(object : Callback<RequestDataDto> {
            override fun onResponse(
                call: Call<RequestDataDto>,
                response: Response<RequestDataDto>
            ) {
                if (response.isSuccessful) {
                    Log.d("API Request successful", "Получили ${response.code()}")
                    val dataResponse = response.body()
                    println(dataResponse)
                    if (dataResponse != null) {
                        groupAdapter = GroupAdapter(
                            dataResponse.groupsOfCourse
                        )
                        equipList = dataResponse.equipments as ArrayList<String>
                    }
                    for (group in dataResponse!!.groupsOfCourse) {
                        val str = "${group.courseNumber}к., ${group.groupNumber}гр."
                        groupList.add(str)
                        groupMap[str] = group
                    }
                } else {
                    stopAnimation(confirm)
                    println("Не успешно")
                }
            }

            override fun onFailure(call: Call<RequestDataDto>, t: Throwable) {
                println("Ошибка")
                println(t)
            }
        })
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onItemClick(position: Int) {
        showDialog(groupList.toArray() as Array<String>)
    }

    private fun showDialog(options: Array<String>) {
        val checkedItems = BooleanArray(options.size) { false }

        val builder = AlertDialog.Builder(requireContext())
            .setMultiChoiceItems(options, checkedItems) { _, which, isChecked ->
                checkedItems[which] = isChecked
            }
            .setPositiveButton("Ок") { dialog, _ ->
                val selectedOptions = ArrayList<String>()
                for (i in options.indices) {
                    if (checkedItems[i]) {
                        groups.add(groupMap[options[i]]!!)
                    }
                }
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
        builder.create().show()
    }

    private fun showEquipmentDialog(options: Array<String>) {
        val checkedItems = BooleanArray(options.size) { false }

        val builder = AlertDialog.Builder(requireContext())
            .setMultiChoiceItems(options, checkedItems) { _, which, isChecked ->
                checkedItems[which] = isChecked
            }
            .setPositiveButton("Ок") { dialog, _ ->
                for (i in options.indices) {
                    if (checkedItems[i]) {
                        chosenEquipment.add(options[i])
                    }
                }
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
        builder.create().show()
    }

    private fun stopAnimation(btn: CircularProgressButton) {
        btn.background = ContextCompat.getDrawable(requireContext(), R.drawable.lecturer_bg)
        btn.revertAnimation()
    }
}