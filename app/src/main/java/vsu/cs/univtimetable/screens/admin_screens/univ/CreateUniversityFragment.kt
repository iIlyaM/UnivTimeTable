package vsu.cs.univtimetable.screens.admin_screens.univ

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.github.leandroborgesferreira.loadingbutton.customViews.CircularProgressButton
import vsu.cs.univtimetable.R
import vsu.cs.univtimetable.utils.token_utils.SessionManager
import vsu.cs.univtimetable.TimetableClient
import vsu.cs.univtimetable.api.UnivApi
import vsu.cs.univtimetable.dto.univ.CreateUnivDto
import vsu.cs.univtimetable.repository.UnivRepository
import vsu.cs.univtimetable.utils.NotificationManager
import vsu.cs.univtimetable.utils.Status

class CreateUniversityFragment : Fragment() {

    private lateinit var univApi: UnivApi
    private lateinit var univViewModel: UnivViewModel
    private lateinit var pDialog: ProgressDialog
    private lateinit var confirmBtn: CircularProgressButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        univApi = TimetableClient.getClient().create(UnivApi::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_university, container, false)
        confirmBtn = view.findViewById(R.id.confirmAddUnivBtn)
        val univField = view.findViewById<EditText>(R.id.editUnivNameText)
        val city = view.findViewById<EditText>(R.id.editCityText)
        pDialog = ProgressDialog(context)
        val token = SessionManager.getToken(requireContext())!!
        val univRepository = UnivRepository(univApi, token)

        univViewModel =
            ViewModelProvider(
                requireActivity(),
                UnivViewModelFactory(univRepository, token)
            )[UnivViewModel::class.java]

        val prevPageButton = view.findViewById<ImageButton>(R.id.prevPageButton)
        prevPageButton.setOnClickListener {
            findNavController().navigate(R.id.action_createUniversityFragment_to_univListPageFragment)
        }

        val mainPageButton = view.findViewById<ImageButton>(R.id.mainPageButton)
        mainPageButton.setOnClickListener {
            findNavController().navigate(R.id.action_createUniversityFragment_to_adminMainPageFragment)
        }

        confirmBtn.setOnClickListener {
            confirmBtn.startAnimation()
            addUniversity(univField, city)
        }
        return view
    }

    private fun addUniversity(univField: EditText, city: EditText) {
        val univName: String = univField.text.toString()
        val cityName:String = city.text.toString()
        if (univField.text.isEmpty())
        {
            univField.error = "Введите название университета"
            return
        }
        if (city.text.isEmpty())
        {
            city.error = "Введите город"
            return
        }

        val token: String? = SessionManager.getToken(requireContext())
        Log.d("API Request failed", "${token}")
        univViewModel.addUniversity(CreateUnivDto(univName, cityName)).observe(viewLifecycleOwner) {
            it?.let {
                when (it.status) {
                    Status.SUCCESS -> {
                        pDialog.dismiss()
                        stopAnimation(confirmBtn)
                        univField.text.clear()
                        city.text.clear()
                        NotificationManager.showToastNotification(requireContext(), "$univName успешно добавлен")
                        univViewModel.getUniversities(null, null)
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

    private fun stopAnimation(btn: CircularProgressButton) {
        btn.background = ContextCompat.getDrawable(requireContext(), R.drawable.admin_bg)
        btn.revertAnimation()
    }

}