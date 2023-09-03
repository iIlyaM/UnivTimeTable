package vsu.cs.univtimetable.screens.admin_screens.faculty

import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.github.leandroborgesferreira.loadingbutton.customViews.CircularProgressButton
import vsu.cs.univtimetable.R
import vsu.cs.univtimetable.SessionManager
import vsu.cs.univtimetable.TimetableClient
import vsu.cs.univtimetable.api.FacultyApi
import vsu.cs.univtimetable.dto.univ.CreateFacultyDto
import vsu.cs.univtimetable.repository.FacultyRepository
import vsu.cs.univtimetable.screens.admin_screens.univ.UnivViewModelFactory
import vsu.cs.univtimetable.utils.NotificationManager
import vsu.cs.univtimetable.utils.Status

class CreateFacultyPageFragment : Fragment() {

    private lateinit var facultyApi: FacultyApi
    private lateinit var facultyViewModel: FacultyViewModel
    private lateinit var confirmBtn: CircularProgressButton
    private lateinit var pDialog: ProgressDialog
    private var universityId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        facultyApi = TimetableClient.getClient().create(FacultyApi::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_faculty_page, container, false)
        val facultyNameField = view.findViewById<EditText>(R.id.editFacultyNameText)
        val confirmBtn = view.findViewById<AppCompatButton>(R.id.confirmFacultyCreateBtn)

        pDialog = ProgressDialog(context)
        val token = SessionManager.getToken(requireContext())!!
        val facultyRepository = FacultyRepository(facultyApi, token)

        facultyViewModel =
            ViewModelProvider(
                requireActivity(),
                UnivViewModelFactory(facultyRepository, token)
            )[FacultyViewModel::class.java]

        val prevPageButton = view.findViewById<ImageButton>(R.id.prevPageButton)
        prevPageButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("univId", universityId)
            findNavController().navigate(
                R.id.action_createFacultyPageFragment_to_facultyListPageFragment,
                bundle
            )
        }

        val mainPageButton = view.findViewById<ImageButton>(R.id.mainPageButton)
        mainPageButton.setOnClickListener {
            findNavController().navigate(R.id.action_createFacultyPageFragment_to_adminMainPageFragment)
        }


        confirmBtn.setOnClickListener {
            addFaculty(facultyNameField)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        universityId = getUnivId()
    }

    private fun addFaculty(facultyField: EditText) {
        val name: String = facultyField.text.toString()

        if (facultyField.text.isEmpty()) {
            facultyField.error = "Введите название факультета"
            stopAnimation(confirmBtn)
            return
        }
        val token: String? = SessionManager.getToken(requireContext())
        Log.d("API Request failed", "${token}")

        val id = arguments?.getInt("univId")

        if (id != null) {
            facultyViewModel.addFaculty(id, CreateFacultyDto(name)).observe(viewLifecycleOwner) {
                it?.let {
                    when (it.status) {
                        Status.SUCCESS -> {
                            pDialog.dismiss()
                            showToastNotification("Факультет успешно создан")
                            facultyField.text.clear()
                            val bundle = Bundle()
                            bundle.putInt("univId", universityId)
                            findNavController().navigate(
                                R.id.action_createFacultyPageFragment_to_facultyListPageFragment,
                                bundle
                            )
                        }

                        Status.ERROR -> {
                            pDialog.dismiss()
                            NotificationManager.showToastNotification(
                                requireContext(),
                                it.message.toString()
                            )
                        }
                        Status.LOADING -> {
                            NotificationManager.setLoadingDialog(pDialog)
                        }
                    }
                }
            }

        }
    }

    private fun showToastNotification(message: String) {
        val duration = Toast.LENGTH_LONG

        val toast = Toast.makeText(requireContext(), message, duration)
        toast.show()
        val handler = Handler()
        handler.postDelayed({ toast.cancel() }, 1500)
    }

    private fun getUnivId(): Int {
        val id = arguments?.getInt("univId")
        var univId: Int = 0
        if (id != null) {
            univId = id
        }
        return univId
    }

    private fun stopAnimation(btn: CircularProgressButton) {
        btn.background = ContextCompat.getDrawable(requireContext(), R.drawable.admin_bg)
        btn.revertAnimation()
    }

}