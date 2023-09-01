package vsu.cs.univtimetable.screens.admin_screens.audience

import android.os.Bundle
import android.os.Handler
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vsu.cs.univtimetable.R
import vsu.cs.univtimetable.SessionManager
import vsu.cs.univtimetable.TimetableClient
import vsu.cs.univtimetable.api.AudienceApi
import vsu.cs.univtimetable.dto.univ.CreateAudienceRequest


class CreateAudiencePageFragment : Fragment() {

    private lateinit var audienceApi: AudienceApi
    private lateinit var equipmentLayout: AutoCompleteTextView
    private lateinit var equipments: List<String>

    private var neededEquipments: ArrayList<String> = ArrayList()

    private lateinit var audience: CreateAudienceRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        audienceApi = TimetableClient.getClient().create(AudienceApi::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_audience_page, container, false)

        val prevPageButton = view.findViewById<ImageButton>(R.id.prevPageButton)
        prevPageButton.setOnClickListener {
            findNavController().navigate(R.id.action_createAudiencePageFragment_to_facultyMainPageFragment)
        }

        val mainPageButton = view.findViewById<ImageButton>(R.id.mainPageButton)
        mainPageButton.setOnClickListener {
            findNavController().navigate(R.id.action_createAudiencePageFragment_to_adminMainPageFragment)
        }

        val audienceEditText = view.findViewById<EditText>(R.id.addAudienceText)
        audienceEditText.inputType = InputType.TYPE_CLASS_NUMBER
        val editCapacityText = view.findViewById<EditText>(R.id.editCapacityText)
        editCapacityText.inputType = InputType.TYPE_CLASS_NUMBER

        equipmentLayout = view.findViewById(R.id.editEquipmentText)

        val confirmBtn = view.findViewById<AppCompatButton>(R.id.confirmCreateAudienceBtn)
        confirmBtn.setOnClickListener {
            createAudience(view)
        }

        getEquipments()

        equipmentLayout.setOnClickListener {
            showDialog("Инвентарь", equipments.toTypedArray())
        }

        return view
    }

    private fun createAudience(view: View) {
        val token: String? = SessionManager.getToken(requireContext())

        val audienceEditText = view.findViewById<EditText>(R.id.addAudienceText)
        val editCapacityText = view.findViewById<EditText>(R.id.editCapacityText)
        if (audienceEditText.text.isEmpty()) {
            audienceEditText.error = "Введите номер аудитории"
            showToastNotification("Пожалуйста, заполните все поля")
            return
        }
        if (editCapacityText.text.isEmpty()) {
            audienceEditText.error = "Введите вместимость аудитории"
            showToastNotification("Пожалуйста, заполните все поля")
            return
        }

        audience.audienceNumber = audienceEditText.text.toString().toInt()
        audience.capacity = editCapacityText.text.toString().toInt()

        val univId = arguments?.getInt("univId")
        val facultyId = arguments?.getInt("facultyId")

        val call =
            audienceApi.createAudience("Bearer ${token}", univId ?: -1, facultyId ?: -1, audience)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                if (response.isSuccessful) {
                    Log.d("API Request Successful", "${response.code()}")

                    showToastNotification("Аудитория успешно создана")
                } else {
                    println("Не успешно, ошибка = ${response.code()}")
                    if (response.code() == 400) {
                        showToastNotification("Аудитория с таким номером уже существует")
                    }
                    if (response.code() == 403) {
                        showToastNotification("Недостаточно прав доступа для выполнения")
                    }
                    if (response.code() == 404) {
                        showToastNotification("Переданный инвентарь не существует в базе")
                    }
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                println("Ошибка")
                println(t)
            }
        })
    }

    private fun getEquipments() {
        val token: String? = SessionManager.getToken(requireContext())
        Log.d("API Request failed", "${token}")
        val call = audienceApi.getAvailableEquipments("Bearer ${token}")

        call.enqueue(object : Callback<List<String>> {
            override fun onResponse(
                call: Call<List<String>>,
                response: Response<List<String>>
            ) {
                if (response.isSuccessful) {
                    Log.d("API Request successful", "Получили ${response.code()}")
                    val dataResponse = response.body()
                    println(dataResponse)
                    if (dataResponse != null) {

                        equipmentLayout.setAdapter(
                            ArrayAdapter(
                                requireContext(),
                                R.layout.subj_item,
                                dataResponse
                            )
                        )
                    }
                } else {
                    println("Не успешно")
                }
            }

            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                println("Ошибка")
                println(t)
            }
        })
    }

    private fun showDialog(title: String, options: Array<String>) {
        val checkedItems = BooleanArray(options.size) { false }

        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setTitle(title)
            .setMultiChoiceItems(options, checkedItems) { _, which, isChecked ->
                checkedItems[which] = isChecked
            }
            .setPositiveButton("Ок") { dialog, _ ->
                for (i in options.indices) {
                    if (checkedItems[i]) {
                        neededEquipments.add(options[i])
                    } else {
                        if (neededEquipments.contains(options[i])) {
                            neededEquipments.remove(options[i])
                        }
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