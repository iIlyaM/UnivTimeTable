package vsu.cs.univtimetable.screens.admin_screens.audience

import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vsu.cs.univtimetable.R
import vsu.cs.univtimetable.utils.token_utils.SessionManager
import vsu.cs.univtimetable.TimetableClient
import vsu.cs.univtimetable.api.AudienceApi
import vsu.cs.univtimetable.repository.AudienceRepository
import vsu.cs.univtimetable.screens.adapter.AudienceListAdapter
import vsu.cs.univtimetable.screens.adapter.OnAudienceDeleteInterface
import vsu.cs.univtimetable.screens.adapter.OnAudienceEditInterface
import vsu.cs.univtimetable.screens.admin_screens.univ.UnivViewModelFactory
import vsu.cs.univtimetable.utils.NotificationManager.setLoadingDialog
import vsu.cs.univtimetable.utils.NotificationManager.showToastNotification
import vsu.cs.univtimetable.utils.Status

class AudienceListPageFragment : Fragment(), OnAudienceEditInterface, OnAudienceDeleteInterface {

    private lateinit var audienceApi: AudienceApi
    private lateinit var recyclerView: RecyclerView
    private lateinit var audienceViewModel: AudienceViewModel
    private lateinit var adapter: AudienceListAdapter
    private lateinit var pDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        audienceApi = TimetableClient.getClient().create(AudienceApi::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_audience_list_page, container, false)
        recyclerView = view.findViewById(R.id.audiencesRecyclerView)
        initRV(recyclerView)

        pDialog = ProgressDialog(context)
        val token = SessionManager.getToken(requireContext())!!
        val audienceRepository = AudienceRepository(audienceApi, token)

        audienceViewModel =
            ViewModelProvider(
                requireActivity(),
                UnivViewModelFactory(audienceRepository, token)
            )[AudienceViewModel::class.java]

        val addBtn = view.findViewById<ImageButton>(R.id.addNewAudienceBtn)
        addBtn.setOnClickListener {
            sendId()
        }

        val prevPageButton = view.findViewById<ImageButton>(R.id.prevPageButton)
        prevPageButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("facultyId", getFacultyId())
            bundle.putInt("univId", getUnivId())
            findNavController().navigate(
                R.id.action_audienceListPageFragment_to_facultyMainPageFragment,
                bundle
            )
        }

        val mainPageButton = view.findViewById<ImageButton>(R.id.mainPageButton)
        mainPageButton.setOnClickListener {
            findNavController().navigate(R.id.action_audienceListPageFragment_to_adminMainPageFragment)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        audienceViewModel.audienceList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        audienceViewModel.errorMsg.observe(viewLifecycleOwner) {
            showToastNotification(requireContext(), it)
        }
        getAudiences()
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
            R.id.action_audienceListPageFragment_to_createAudiencePageFragment,
            bundle
        )
    }

    private fun initRV(rv: RecyclerView) {
        adapter = AudienceListAdapter(
            this@AudienceListPageFragment,
            this@AudienceListPageFragment,
        )
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter
    }

    override fun onEditClick(id: Int) {
        val bundle = Bundle()
        audienceViewModel.getAudience(id)
        bundle.putInt("facultyId", getFacultyId())
        bundle.putInt("univId", getUnivId())
        bundle.putInt("id", id)
        bundle.putBoolean("editable", true)
        findNavController().navigate(
            R.id.action_audienceListPageFragment_to_createAudiencePageFragment,
            bundle
        )
    }

    override fun onDeleteClick(id: Int) {
        val builder = AlertDialog.Builder(requireContext())
        audienceViewModel.getAudience(id).observe(viewLifecycleOwner) {
            it?.let {
                when (it.status) {
                    Status.SUCCESS -> {
                        builder.setTitle("Удаление группы")
                            .setMessage(
                                "Вы уверены что хотите удалить аудиторию: ${it.data!!.audienceNumber} из списка?"
                            )
                            .setCancelable(true)
                            .setPositiveButton("Удалить") { _, _ ->
                                delete(id)
                                getAudiences()
                            }
                            .setNegativeButton(
                                "Отмена"
                            ) { _, _ ->
                            }
                        builder.create()
                        builder.show()
                    }

                    Status.ERROR -> {}
                    Status.LOADING -> {
                        setLoadingDialog(pDialog)
                    }
                }
            }
        }
    }

    private fun delete(id: Int) {
        audienceViewModel.deleteAudience(id).observe(viewLifecycleOwner) {
            it?.let {
                when (it.status) {
                    Status.SUCCESS -> {
                        pDialog.dismiss()
                        getAudiences()
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

    private fun getAudiences() {
        audienceViewModel.getAudiences(getFacultyId()).observe(viewLifecycleOwner) {
            it?.let {
                when(it.status) {
                    Status.SUCCESS -> {
                        pDialog.dismiss()
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

}