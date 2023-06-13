package vsu.cs.univtimetable.screens.lect_screens

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vsu.cs.univtimetable.R
import vsu.cs.univtimetable.SessionManager
import vsu.cs.univtimetable.TimetableClient
import vsu.cs.univtimetable.api.TimetableApi
import vsu.cs.univtimetable.dto.GroupResponse
import vsu.cs.univtimetable.dto.ImpossibleTimeDto
import vsu.cs.univtimetable.dto.RequestDataDto
import vsu.cs.univtimetable.dto.SendRequest
import vsu.cs.univtimetable.screens.adapter.GroupAdapter


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
        val classTypeCompleteView =
            view.findViewById<AutoCompleteTextView>(R.id.typeAutoCompleteText)
        val confirm = view.findViewById<AppCompatButton>(R.id.confirmSubjectBtn)
        val lectRequestsButton = view.findViewById<AppCompatButton>(R.id.lectRequestsButton)

        val typeAdapter = ArrayAdapter(requireContext(), R.layout.subj_item, classTypes)

        val equipListView = view.findViewById<TextView>(R.id.selectEquipmentView)
        val editSubjectNameText = view.findViewById<EditText>(R.id.editSubjectNameText)
        val editHoursCountText = view.findViewById<EditText>(R.id.editHoursCountText)

        classTypeCompleteView.setAdapter(typeAdapter)
        classTypeCompleteView.setOnItemClickListener { parent, view2, position, id ->
            val selectedItem = parent.getItemAtPosition(position) as String

        }

        val groupTextInputLayout =
            view.findViewById<TextInputLayout>(R.id.selectGroupView)
        groupTextInputLayout.boxStrokeColor =
            ContextCompat.getColor(requireContext(), R.color.adminsColor)

        val selectGroupAutoCompleteText =
            view.findViewById<AutoCompleteTextView>(R.id.selectGroupAutoCompleteText)

        val groupAdapter = ArrayAdapter(requireContext(), R.layout.subj_item, groupList)
        selectGroupAutoCompleteText.setAdapter(groupAdapter)
        val items = arrayOfNulls<String>(equipList.size)
        equipListView.setOnClickListener {
            showEquipmentDialog(equipList.toArray(items))
        }

        confirm.setOnClickListener {
            sendRequest(classTypeCompleteView, selectGroupAutoCompleteText, editSubjectNameText, editHoursCountText)
        }

        lectRequestsButton.setOnClickListener {
            findNavController().navigate(R.id.action_addSubjectPageFragment_to_generateTimetablePageFragment)
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
                    Log.d("API Request Successful", "${response.code()}")
                    showToastNotification("Заявка отправлена")
                } else {
                    if(response.code() == 403){
                        showToastNotification("Недостаточно прав доступа для выполнения")
                    }
                    if(response.code() == 404){
                        showToastNotification("Id переданной группы не было найдено/\n" +
                                "Переданного инвентаря не существует в базе/\n" +
                                "Неверный username пользователя")
                    }
                    println("Не успешно")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                println("Ошибка")
                println(t)
            }
        })
        editSubjectNameText.text.clear()
        editHoursCountText.text.clear()
    }

    private fun getUnwantedTime(): ImpossibleTimeDto {
        return requireArguments().getSerializable("map") as ImpossibleTimeDto
    }

    private fun getRequestData() {
        val token: String? = SessionManager.getToken(requireContext())

        Log.d("API Request failed", "${token}")
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
                    //TODO:
                } else {
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

    private fun showToastNotification(message: String) {
        val duration = Toast.LENGTH_LONG

        val toast = Toast.makeText(requireContext(), message, duration)
        toast.show()
        val handler = Handler()
        handler.postDelayed({ toast.cancel() }, 1500)
    }
}