package vsu.cs.univtimetable.screens.admin_screens.group

import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vsu.cs.univtimetable.R
import vsu.cs.univtimetable.utils.token_utils.SessionManager
import vsu.cs.univtimetable.TimetableClient
import vsu.cs.univtimetable.api.GroupApi
import vsu.cs.univtimetable.repository.GroupRepository
import vsu.cs.univtimetable.screens.adapter.GroupListAdapter
import vsu.cs.univtimetable.screens.adapter.OnGroupDeleteClickInterface
import vsu.cs.univtimetable.screens.adapter.OnGroupEditClickInterface
import vsu.cs.univtimetable.screens.admin_screens.univ.UnivViewModelFactory
import vsu.cs.univtimetable.utils.NotificationManager.setLoadingDialog
import vsu.cs.univtimetable.utils.NotificationManager.showToastNotification
import vsu.cs.univtimetable.utils.Status

class GroupListPageFragment : Fragment(), OnGroupEditClickInterface, OnGroupDeleteClickInterface {


    private lateinit var groupApi: GroupApi
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GroupListAdapter
    private lateinit var searchByCourseView: SearchView
    private lateinit var searchByGroupView: SearchView
    private lateinit var groupViewModel: GroupViewModel
    private lateinit var pDialog: ProgressDialog


    private var courseSearch: Int? = null
    private var groupSeacrh: Int? = null
    private var order = "ASC"

    private var universityId = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        groupApi = TimetableClient.getClient().create(GroupApi::class.java)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_group_list_page, container, false)
        recyclerView = view.findViewById(R.id.groupRecyclerView)
        initRV(recyclerView)

        pDialog = ProgressDialog(context)
        val token = SessionManager.getToken(requireContext())!!
        val groupRepository = GroupRepository(groupApi, token)

        groupViewModel =
            ViewModelProvider(
                requireActivity(),
                UnivViewModelFactory(groupRepository, token)
            )[GroupViewModel::class.java]

        val addGroupBtn = view.findViewById<ImageButton>(R.id.addNewGroupBtn)
        addGroupBtn.setOnClickListener {
            sendId()
        }

        val sortBtn = view.findViewById<ImageButton>(R.id.sortByCourseBtn)
        sortBtn.setOnClickListener {
            order = if (order.equals("ASC")) {
                "DESC"
            } else {
                "ASC"
            }
            getGroups(courseSearch, order, groupSeacrh)
        }

        val prevPageButton = view.findViewById<ImageButton>(R.id.prevPageButton)
        prevPageButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("facultyId", getFacultyId())
            bundle.putInt("univId", getUnivId())
            findNavController().navigate(
                R.id.action_groupListPageFragment_to_facultyMainPageFragment,
                bundle
            )
        }

        val mainPageButton = view.findViewById<ImageButton>(R.id.mainPageButton)
        mainPageButton.setOnClickListener {
            findNavController().navigate(R.id.action_groupListPageFragment_to_adminMainPageFragment)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        groupViewModel.groupList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        groupViewModel.errorMsg.observe(viewLifecycleOwner) {
            showToastNotification(requireContext(), it)
        }
        setLoadingDialog(pDialog)
        getGroups(null, null, null)
        pDialog.dismiss()
        universityId = getUnivId()
        searchByCourseView = view.findViewById(R.id.searchByCourseView)
        searchByCourseView.inputType = InputType.TYPE_CLASS_NUMBER;
        searchByCourseView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    courseSearch = newText.toIntOrNull()
                    getGroups(courseSearch, order, groupSeacrh)
                }

                return true
            }
        })

        searchByGroupView = view.findViewById(R.id.searchByGroupView)
        searchByGroupView.inputType = InputType.TYPE_CLASS_NUMBER;
        searchByGroupView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    groupSeacrh = newText.toIntOrNull()
                    getGroups(courseSearch, order, groupSeacrh)
                }

                return true
            }
        })
    }

    override fun onEditClick(groupId: Long) {
        val bundle = Bundle()
        groupViewModel.getGroup(groupId)
        bundle.putInt("facultyId", getFacultyId())
        bundle.putInt("univId", getUnivId())
        bundle.putLong("id", groupId)
        bundle.putBoolean("editable", true)
        findNavController().navigate(
            R.id.action_groupListPageFragment_to_createGroupPageFragment,
            bundle
        )
    }

    override fun onDeleteClick(groupId: Long) {
        val builder = AlertDialog.Builder(requireContext())
        groupViewModel.getGroup(groupId).observe(viewLifecycleOwner) {
            it?.let {
                when (it.status) {
                    Status.SUCCESS -> {
                        pDialog.dismiss()
                        builder.setTitle("Удаление группы")
                            .setMessage(
                                "Вы уверены что хотите удалить группу курса: ${it.data!!.courseNumber}, " +
                                        "номер: ${it.data.groupNumber} из списка?"
                            )
                            .setCancelable(true)
                            .setPositiveButton("Удалить") { _, _ ->
                                delete(groupId)
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

    private fun delete(id: Long) {
        groupViewModel.deleteGroups(id, getFacultyId()).observe(viewLifecycleOwner) {
            it?.let {
                when (it.status) {
                    Status.SUCCESS -> {
                        pDialog.dismiss()
                        getGroups(null, null, null)
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


    private fun getUnivId(): Int {
        val id = arguments?.getInt("univId")
        var univId: Int = 0
        if (id != null) {
            univId = id
        }
        return univId
    }

    private fun getGroups(course: Int?, order: String?, groupNumber: Int?) {
        groupViewModel.getGroups(getFacultyId(), course, order, groupNumber).observe(viewLifecycleOwner) {
            it?.let {
                when(it.status) {
                    Status.SUCCESS -> {
                        if(course== null && order == null && groupNumber == null) {
                            if(it.data!!.groups.isEmpty()) {
                                showToastNotification(requireContext(), "Группы на этом факультете ещё не добавлены")
                            }
                            pDialog.dismiss()
                        }
                    }
                    Status.ERROR -> {
                        if(course== null && order == null && groupNumber == null) {
                            pDialog.dismiss()
                        }
                    }
                    Status.LOADING -> {
                        if(course== null && order == null && groupNumber == null) {
                            setLoadingDialog(pDialog)
                        }
                    }
                }
            }
        }
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
        val facId = arguments?.getInt("facultyId")
        if (facId != null) {
            val bundle = Bundle()
            bundle.putInt("facultyId", facId)
            bundle.putInt("univId", getUnivId())
            findNavController().navigate(
                R.id.action_groupListPageFragment_to_createGroupPageFragment,
                bundle
            )
        }
    }

    private fun initRV(rv: RecyclerView) {
        adapter = GroupListAdapter(
            this@GroupListPageFragment,
            this@GroupListPageFragment
        )
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter
    }


}