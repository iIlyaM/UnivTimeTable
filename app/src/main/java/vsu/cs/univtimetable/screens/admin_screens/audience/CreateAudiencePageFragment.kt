package vsu.cs.univtimetable.screens.admin_screens.audience

import android.app.ProgressDialog
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.github.leandroborgesferreira.loadingbutton.customViews.CircularProgressButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vsu.cs.univtimetable.R
import vsu.cs.univtimetable.SessionManager
import vsu.cs.univtimetable.TimetableClient
import vsu.cs.univtimetable.api.AudienceApi
import vsu.cs.univtimetable.dto.univ.CreateAudienceRequest
import vsu.cs.univtimetable.repository.AudienceRepository
import vsu.cs.univtimetable.screens.admin_screens.univ.UnivViewModelFactory
import vsu.cs.univtimetable.utils.NotificationManager
import vsu.cs.univtimetable.utils.NotificationManager.showToastNotification
import vsu.cs.univtimetable.utils.Status


class CreateAudiencePageFragment : Fragment() {

    private lateinit var audienceApi: AudienceApi
    private lateinit var equipmentLayout: AutoCompleteTextView
    private lateinit var equipments: List<String>
    private lateinit var audienceViewModel: AudienceViewModel
    private lateinit var pDialog: ProgressDialog
    private lateinit var confirmBtn: CircularProgressButton

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

        pDialog = ProgressDialog(context)
        val token = SessionManager.getToken(requireContext())!!
        val audienceRepository = AudienceRepository(audienceApi, token)

        audienceViewModel =
            ViewModelProvider(
                requireActivity(),
                UnivViewModelFactory(audienceRepository, token)
            )[AudienceViewModel::class.java]


        val prevPageButton = view.findViewById<ImageButton>(R.id.prevPageButton)
        prevPageButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("facultyId", getFacultyId())
            bundle.putInt("univId", getUnivId())
            findNavController().navigate(
                R.id.action_createAudiencePageFragment_to_facultyMainPageFragment,
                bundle
            )
        }

        val mainPageButton = view.findViewById<ImageButton>(R.id.mainPageButton)
        mainPageButton.setOnClickListener {
            findNavController().navigate(R.id.action_createAudiencePageFragment_to_adminMainPageFragment)
        }

        val audienceEditText = view.findViewById<EditText>(R.id.addAudienceText)
        audienceEditText.inputType = InputType.TYPE_CLASS_NUMBER
        val editCapacityText = view.findViewById<EditText>(R.id.editCapacityText)
        editCapacityText.inputType = InputType.TYPE_CLASS_NUMBER

        equipmentLayout = view.findViewById(R.id.editEquipmentAutoCompleteTextView)

        confirmBtn = view.findViewById(R.id.confirmCreateAudienceBtn)
        confirmBtn.setOnClickListener {
            confirmBtn.startAnimation()
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
            showToastNotification(requireContext(),"Пожалуйста, заполните все поля")
            stopAnimation(confirmBtn)
            return
        }
        if (editCapacityText.text.isEmpty()) {
            audienceEditText.error = "Введите вместимость аудитории"
            showToastNotification(requireContext(),"Пожалуйста, заполните все поля")
            stopAnimation(confirmBtn)
            return
        }

        audience.audienceNumber = audienceEditText.text.toString().toInt()
        audience.capacity = editCapacityText.text.toString().toInt()

        val univId = arguments?.getInt("univId")
        val facultyId = arguments?.getInt("facultyId")

//        val call =
//            audienceApi.createAudience("Bearer ${token}", univId ?: -1, facultyId ?: -1, audience)
//
//        call.enqueue(object : Callback<Void> {
//            override fun onResponse(
//                call: Call<Void>,
//                response: Response<Void>
//            ) {
//                if (response.isSuccessful) {
//                    Log.d("API Request Successful", "${response.code()}")
//
//                    showToastNotification(requireContext(), "Аудитория успешно создана")
//                } else {
//                    println("Не успешно, ошибка = ${response.code()}")
//                    if (response.code() == 400) {
//                        showToastNotification(
//                            requireContext(),
//                            "Аудитория с таким номером уже существует"
//                        )
//                    }
//                    if (response.code() == 403) {
//                        showToastNotification(
//                            requireContext(),
//                            "Недостаточно прав доступа для выполнения"
//                        )
//                    }
//                    if (response.code() == 404) {
//                        showToastNotification(
//                            requireContext(),
//                            "Переданный инвентарь не существует в базе"
//                        )
//                    }
//                }
//            }
//
//            override fun onFailure(call: Call<Void>, t: Throwable) {
//                println("Ошибка")
//                println(t)
//            }
//        })
    }

    private fun getEquipments() {
        audienceViewModel.getAvailableEquipments().observe(viewLifecycleOwner) {
            it?.let {
                when (it.status) {
                    Status.SUCCESS -> {
                        pDialog.dismiss()
                        equipmentLayout.setAdapter(
                            ArrayAdapter(
                                requireContext(),
                                R.layout.subj_item,
                                it.data!!.toTypedArray()
                            )
                        )
                    }
                    Status.ERROR -> {
                        pDialog.dismiss()
                    }
                    Status.LOADING -> {
                        NotificationManager.setLoadingDialog(pDialog)
                    }
                }
            }
        }
//
//        call.enqueue(object : Callback<List<String>> {
//            override fun onResponse(
//                call: Call<List<String>>,
//                response: Response<List<String>>
//            ) {
//                if (response.isSuccessful) {
//                    Log.d("API Request successful", "Получили ${response.code()}")
//                    val dataResponse = response.body()
//                    println(dataResponse)
//                    if (dataResponse != null) {
//
//                        equipmentLayout.setAdapter(
//                            ArrayAdapter(
//                                requireContext(),
//                                R.layout.subj_item,
//                                dataResponse
//                            )
//                        )
//                    }
//                } else {
//                    println("Не успешно")
//                }
//            }
//
//            override fun onFailure(call: Call<List<String>>, t: Throwable) {
//                println("Ошибка")
//                println(t)
//            }
//        })
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

    private fun getUnivId(): Int {
        val id = arguments?.getInt("univId")
        var univId: Int = 0
        if (id != null) {
            univId = id
        }
        return univId
    }

    private fun getFacultyId(): Int {
        val id = arguments?.getInt("facultyId")
        var facultyId: Int = 0
        if (id != null) {
            facultyId = id
        }
        return facultyId
    }

    private fun sendId() {
        val bundle = Bundle()
        bundle.putInt("facultyId", getFacultyId())
        bundle.putInt("univId", getUnivId())
        findNavController().navigate(
            R.id.action_groupListPageFragment_to_createGroupPageFragment,
            bundle
        )
    }

    private fun stopAnimation(btn: CircularProgressButton) {
        btn.background = ContextCompat.getDrawable(requireContext(), R.drawable.admin_bg)
        btn.revertAnimation()
    }
}