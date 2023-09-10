package vsu.cs.univtimetable.screens.admin_screens.faculty

import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SearchView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vsu.cs.univtimetable.R
import vsu.cs.univtimetable.utils.token_utils.SessionManager
import vsu.cs.univtimetable.TimetableClient
import vsu.cs.univtimetable.api.FacultyApi
import vsu.cs.univtimetable.dto.faculty.FacultyDto
import vsu.cs.univtimetable.repository.FacultyRepository
import vsu.cs.univtimetable.screens.adapter.FacultyListAdapter
import vsu.cs.univtimetable.screens.adapter.OnFacultiesItemClickInterface
import vsu.cs.univtimetable.screens.adapter.OnFacultyDeleteInterface
import vsu.cs.univtimetable.screens.adapter.OnFacultyEditInterface
import vsu.cs.univtimetable.screens.admin_screens.univ.UnivViewModelFactory
import vsu.cs.univtimetable.utils.NotificationManager
import vsu.cs.univtimetable.utils.NotificationManager.setLoadingDialog
import vsu.cs.univtimetable.utils.Status

class FacultyListPageFragment : Fragment(), OnFacultiesItemClickInterface, OnFacultyEditInterface,
    OnFacultyDeleteInterface {

    private lateinit var facultyApi: FacultyApi
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FacultyListAdapter
    private lateinit var searchView: SearchView
    private lateinit var facultyViewModel: FacultyViewModel
    private lateinit var pDialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        facultyApi = TimetableClient.getClient().create(FacultyApi::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_faculty_list_page, container, false)
        recyclerView = view.findViewById(R.id.facultyRecyclerView)
        initRV(recyclerView)

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
            findNavController().navigate(R.id.action_facultyListPageFragment_to_univListPageFragment)
        }

        val mainPageButton = view.findViewById<ImageButton>(R.id.mainPageButton)
        mainPageButton.setOnClickListener {
            findNavController().navigate(R.id.action_facultyListPageFragment_to_adminMainPageFragment)
        }

        val sortBtn = view.findViewById<ImageButton>(R.id.sortFacultyListBtn)
        var order = "ASC"
        sortBtn.setOnClickListener {
            order = if (order.equals("ASC")) {
                "DESC"
            } else {
                "ASC"
            }
            getFaculties(null, order)
        }

        val addFacultyBtn = view.findViewById<ImageButton>(R.id.addNewFacultyBtn)
        addFacultyBtn.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("univId", getUnivId())
            findNavController().navigate(
                R.id.action_facultyListPageFragment_to_createFacultyPageFragment,
                bundle
            )
        }

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        facultyViewModel.errorMsg.observe(viewLifecycleOwner) {
            NotificationManager.showToastNotification(requireContext(), it)
        }
        NotificationManager.setLoadingDialog(pDialog)
        getFaculties(null, null)
        pDialog.dismiss()
        searchView = view.findViewById(R.id.searchFacultyView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    getFaculties(newText, null)
                }

                return true
            }
        })
    }

    override fun onEditClick(id: Int) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.update_faculty_dialog, null)
        val editFacultyName = dialogView.findViewById<AppCompatEditText>(R.id.updFacultyName)
        val btnUpdate = dialogView.findViewById<AppCompatButton>(R.id.updateFacultyBtn)

        facultyViewModel.getFaculty(id).observe(viewLifecycleOwner) {
            it?.let {
                when (it.status) {
                    Status.SUCCESS -> {
                        pDialog.dismiss()
                        editFacultyName?.setText(it.data?.name)
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

        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)
        val alertDialog = builder.create()
        alertDialog.show()

        btnUpdate.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val name = editFacultyName?.text.toString()
                update(id, FacultyDto(id, name))
                alertDialog.dismiss()
            }
        })
    }


    override fun onDeleteClick(id: Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Удаление факультета")
            .setMessage("Вы уверены что хотите удалить этот факультет?")
            .setCancelable(true)
            .setPositiveButton("Удалить") { _, _ ->
                delete(id)
            }
            .setNegativeButton(
                "Отмена"
            ) { _, _ ->
            }
        builder.create()
        builder.show()
    }

    override fun onItemClick(facultyId: Int) {
        val bundle = Bundle()
        bundle.putInt("facultyId", facultyId)
        bundle.putInt("univId", getUnivId())

        findNavController().navigate(
            R.id.action_facultyListPageFragment_to_facultyMainPageFragment,
            bundle
        )
    }

    private fun update(id: Int, updatedFaculty: FacultyDto) {
        facultyViewModel.editFaculty(id, getUnivId(), updatedFaculty).observe(viewLifecycleOwner) {
            it?.let {
                when (it.status) {
                    Status.SUCCESS -> {
                        getFaculties(null, null)
                        pDialog.dismiss()
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

    private fun delete(id: Int) {
        facultyViewModel.deleteFaculty(id, getUnivId()).observe(viewLifecycleOwner) {
            it?.let {
                when (it.status) {
                    Status.SUCCESS -> {
                        getFaculties(null, null)
                        pDialog.dismiss()
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

    private fun getFaculties(name: String?, order: String?) {
        val token: String? = SessionManager.getToken(requireContext())

        val universityId = getUnivId()
        facultyViewModel.getFaculties(universityId, name, order).observe(viewLifecycleOwner) {
            it?.let {
                when (it.status) {
                    Status.SUCCESS -> {
                        adapter.submitList(it.data)
                        if (name == null && order == null) {
                            if (it.data!!.isEmpty()) {
                                NotificationManager.showToastNotification(
                                    requireContext(),
                                    "Факультеты для этого университета не добавлены"
                                )
                            }
                            pDialog.dismiss()
                        }
                    }

                    Status.ERROR -> {
                        if (name == null && order == null) {
                            pDialog.dismiss()
                        }
                        NotificationManager.showToastNotification(
                            requireContext(),
                            it.message.toString()
                        )
                    }

                    Status.LOADING -> {
                        if (name == null && order == null) {
                            setLoadingDialog(pDialog)
                        }
                    }
                }
            }
        }
    }

    private fun getUnivId(): Int {
        val id = arguments?.getInt("univId")
        var univId: Int = 0
        if (id != null) {
            univId = id
        }
        return univId
    }

    private fun initRV(rv: RecyclerView) {
        adapter = FacultyListAdapter(
            this@FacultyListPageFragment,
            this@FacultyListPageFragment,
            this@FacultyListPageFragment
        )
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter
    }
}