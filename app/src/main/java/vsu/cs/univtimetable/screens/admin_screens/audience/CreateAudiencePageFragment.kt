package vsu.cs.univtimetable.screens.admin_screens.audience

import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Build
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
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.leandroborgesferreira.loadingbutton.customViews.CircularProgressButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vsu.cs.univtimetable.R
import vsu.cs.univtimetable.SessionManager
import vsu.cs.univtimetable.TimetableClient
import vsu.cs.univtimetable.api.AudienceApi
import vsu.cs.univtimetable.dto.audience.AudienceDto
import vsu.cs.univtimetable.dto.group.GroupDto
import vsu.cs.univtimetable.dto.univ.CreateAudienceRequest
import vsu.cs.univtimetable.repository.AudienceRepository
import vsu.cs.univtimetable.screens.adapter.EquipmentsAdapter
import vsu.cs.univtimetable.screens.adapter.OnEquipmentDeleteInterface
import vsu.cs.univtimetable.screens.admin_screens.univ.UnivViewModelFactory
import vsu.cs.univtimetable.utils.NotificationManager
import vsu.cs.univtimetable.utils.NotificationManager.setLoadingDialog
import vsu.cs.univtimetable.utils.NotificationManager.showToastNotification
import vsu.cs.univtimetable.utils.Status


class CreateAudiencePageFragment : Fragment(), OnEquipmentDeleteInterface {

    private lateinit var audienceApi: AudienceApi

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EquipmentsAdapter
    private lateinit var audienceViewModel: AudienceViewModel
    private lateinit var pDialog: ProgressDialog
    private lateinit var confirmBtn: CircularProgressButton

    private var neededEquipments: MutableSet<String> = mutableSetOf()
    private var equipments: MutableList<String> = mutableListOf()


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

        recyclerView = view.findViewById(R.id.equipmentsRecyclerView)
        initRV(recyclerView)

        audienceViewModel =
            ViewModelProvider(
                requireActivity(),
                UnivViewModelFactory(audienceRepository, token)
            )[AudienceViewModel::class.java]


        val editEquipmentBtn = view.findViewById<AppCompatButton>(R.id.editEquipmentBtn)

        val prevPageButton = view.findViewById<ImageButton>(R.id.prevPageButton)
        prevPageButton.setOnClickListener {
            sendId()
        }

        val mainPageButton = view.findViewById<ImageButton>(R.id.mainPageButton)
        mainPageButton.setOnClickListener {
            findNavController().navigate(R.id.action_createAudiencePageFragment_to_adminMainPageFragment)
        }

        val audienceEditText = view.findViewById<EditText>(R.id.addAudienceText)
        audienceEditText.inputType = InputType.TYPE_CLASS_NUMBER
        val editCapacityText = view.findViewById<EditText>(R.id.editCapacityText)
        editCapacityText.inputType = InputType.TYPE_CLASS_NUMBER

        setFieldsIfEdit(audienceEditText, editCapacityText)

        confirmBtn = view.findViewById(R.id.confirmCreateAudienceBtn)
        confirmBtn.setOnClickListener {
            confirmBtn.startAnimation()
            createAudience(view)
        }

        getEquipments()

        editEquipmentBtn.setOnClickListener {
            showDialog("Инвентарь", equipments.toTypedArray())
        }

        return view
    }

    private fun createAudience(view: View) {
        val audienceEditText = view.findViewById<EditText>(R.id.addAudienceText)
        val editCapacityText = view.findViewById<EditText>(R.id.editCapacityText)
        if (audienceEditText.text.isEmpty()) {
            audienceEditText.error = "Введите номер аудитории"
            showToastNotification(requireContext(), "Пожалуйста, заполните все поля")
            stopAnimation(confirmBtn)
            return
        }
        if (editCapacityText.text.isEmpty()) {
            audienceEditText.error = "Введите вместимость аудитории"
            showToastNotification(requireContext(), "Пожалуйста, заполните все поля")
            stopAnimation(confirmBtn)
            return
        }

        audience = CreateAudienceRequest(0, 0, arrayListOf())
        audience.audienceNumber = audienceEditText.text.toString().toInt()
        audience.capacity = editCapacityText.text.toString().toInt()
        audience.equipments = neededEquipments.toList()

        val editable = arguments?.getBoolean("editable");
        if (editable != null && editable) {
            val id = arguments?.getInt("id") ?: -1
            audienceViewModel.editUser(
                id,
                AudienceDto(id, audience.audienceNumber, audience.capacity, audience.equipments)
            ).observe(viewLifecycleOwner) {
                it?.let {
                    when (it.status) {
                        Status.SUCCESS -> {
                            stopAnimation(confirmBtn)
                            showToastNotification(requireContext(), "Информация о группе изменена")
                            sendId()
                        }

                        Status.ERROR -> {
                            showToastNotification(requireContext(), it.message.toString())
                            stopAnimation(confirmBtn)
                        }

                        Status.LOADING -> {}
                    }
                }
            }
        } else {
            audienceViewModel.addAudience(
                getUnivId(),
                getFacultyId(),
                audience
            ).observe(viewLifecycleOwner) {
                it?.let {
                    when (it.status) {
                        Status.SUCCESS -> {
                            stopAnimation(confirmBtn)
                            audienceEditText.text.clear()
                            editCapacityText.text.clear()
                            neededEquipments.clear()
                            adapter.submitList(neededEquipments.toList())
                            recyclerView.visibility=View.GONE
                            sendId()
                            showToastNotification(requireContext(), "Группа добавлена")
                        }

                        Status.ERROR -> {
                            showToastNotification(requireContext(), it.message.toString())
                            stopAnimation(confirmBtn)
                        }

                        Status.LOADING -> {}
                    }
                }
            }
        }
    }

    private fun setFieldsIfEdit(
        audienceEditText: EditText,
        editCapacityText: EditText
    ) {
        val editable = arguments?.getBoolean("editable")
        if (editable != null && editable) {
            audienceViewModel.getAudience(arguments?.getInt("id")!!).observe(viewLifecycleOwner) {
                it?.let {
                    when (it.status) {
                        Status.SUCCESS -> {
                            pDialog.dismiss()
                            audienceEditText.setText(it.data!!.audienceNumber.toString())
                            editCapacityText.setText(it.data.capacity.toString())
                            neededEquipments.addAll(it.data.equipments)
                            adapter.submitList(it.data.equipments)
                        }

                        Status.ERROR -> {
                            pDialog.dismiss()
                            showToastNotification(requireContext(), it.message.toString())
                        }

                        Status.LOADING -> {
                            setLoadingDialog(pDialog)
                        }
                    }
                }
            }
        }
    }


    private fun getEquipments() {
        audienceViewModel.getAvailableEquipments().observe(viewLifecycleOwner) {
            it?.let {
                when (it.status) {
                    Status.SUCCESS -> {
                        pDialog.dismiss()
                        equipments.addAll(it.data!!)
                    }

                    Status.ERROR -> {
                        pDialog.dismiss()
                    }

                    Status.LOADING -> {
                        setLoadingDialog(pDialog)
                    }
                }
            }
        }
    }

    override fun onDeleteClick(equipment: String) {
        neededEquipments.remove(equipment)
        adapter.submitList(neededEquipments.toMutableList())
    }


    private fun showDialog(title: String, options: Array<String>) {
        val checkedItems = BooleanArray(options.size) { false }

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)
            .setMultiChoiceItems(options, checkedItems) { _, which, isChecked ->
                checkedItems[which] = isChecked
            }
            .setPositiveButton("Ок") { dialog, _ ->
                for (i in options.indices) {
                    if (checkedItems[i]) {
                        neededEquipments.add(options[i])
                    }
                }
                recyclerView.visibility=View.VISIBLE
                adapter.submitList(neededEquipments.toMutableList())
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
            R.id.action_createAudiencePageFragment_to_audienceListPageFragment,
            bundle
        )
    }


    private fun stopAnimation(btn: CircularProgressButton) {
        btn.background = ContextCompat.getDrawable(requireContext(), R.drawable.admin_bg)
        btn.revertAnimation()
    }

    private fun initRV(rv: RecyclerView) {
        adapter = EquipmentsAdapter(
            this@CreateAudiencePageFragment
        )
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter
    }


}