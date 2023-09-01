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
import androidx.appcompat.widget.AppCompatButton
import android.widget.SearchView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.leandroborgesferreira.loadingbutton.customViews.CircularProgressButton
import kotlinx.coroutines.delay
import vsu.cs.univtimetable.R
import vsu.cs.univtimetable.SessionManager
import vsu.cs.univtimetable.TimetableClient
import vsu.cs.univtimetable.api.GroupApi
import vsu.cs.univtimetable.repository.GroupRepository
import vsu.cs.univtimetable.screens.adapter.GroupListAdapter
import vsu.cs.univtimetable.screens.adapter.OnGroupDeleteClickInterface
import vsu.cs.univtimetable.screens.adapter.OnGroupEditClickInterface
import vsu.cs.univtimetable.screens.admin_screens.univ.UnivViewModelFactory
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

        val addGroupBtn = view.findViewById<AppCompatButton>(R.id.addNewGroupBtn)
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
                R.id.action_groupListPageFragment_to_facultyListPageFragment,
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
            showToastNotification(it)
        }
        getGroups(null, null, null)
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
                        builder.setTitle("Удаление группы")
                            .setMessage(
                                "Вы уверены что хотите удалить группу курса: ${it.data!!.courseNumber}, " +
                                        "номер: ${it.data.groupNumber} из списка?"
                            )
                            .setCancelable(true)
                            .setPositiveButton("Удалить") { _, _ ->
                                delete(groupId)
                                getGroups(null, null, null)
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
                        setLoadingDialog()
                    }
                }
            }
        }
//        var courseNumber: Int = 0
//        var groupNumber: Int = 0
//        groupViewModel.groupIMmutable.observe(viewLifecycleOwner) {
//            courseNumber = it!!.courseNumber
//            groupNumber = it.groupNumber
//        }
//        builder.setTitle("Удаление группы")
//            .setMessage(
//                "Вы уверены что хотите удалить группу курса: ${courseNumber}, " +
//                        "номер: $groupNumber из списка?"
//            )
//            .setCancelable(true)
//            .setPositiveButton("Удалить") { _, _ ->
//                delete(groupId)
//                getGroups(null, null, null)
//            }
//            .setNegativeButton(
//                "Отмена"
//            ) { _, _ ->
//            }
//        builder.create()
//        builder.show()
    }

    private fun delete(id: Long) {
        groupViewModel.deleteGroups(id, getFacultyId()).observe(viewLifecycleOwner) {
            it?.let {
                when (it.status) {
                    Status.SUCCESS -> {
                        pDialog.dismiss()
                    }
                    Status.ERROR -> {
                        pDialog.dismiss()
                    }
                    Status.LOADING -> {
                        setLoadingDialog()
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
                        pDialog.dismiss()
                    }
                    Status.ERROR -> {
                        pDialog.dismiss()
                    }
                    Status.LOADING -> {
                        setLoadingDialog()
                    }
                }
            }
        }
//        val call = groupApi.getGroups("Bearer ${token}", getFacultyId(), course, order, groupNumber)

//        call.enqueue(object : Callback<GroupResponseDto> {
//            override fun onResponse(
//                call: Call<GroupResponseDto>,
//                response: Response<GroupResponseDto>
//            ) {
//                if (response.isSuccessful) {
//                    Log.d("API Request successful", "Получили ${response.code()}")
//                    val dataResponse = response.body()
//                    println(dataResponse)
//                    if (dataResponse != null) {
//                        adapter = GroupListAdapter(
//                            requireContext(),
//                            dataResponse.groupsPage.contents,
//                            this@GroupListPageFragment
//                        )
//                    }
//                    recyclerView.adapter = adapter
//                } else {
//                    println("Не успешно")
//                }
//            }
//
//            override fun onFailure(call: Call<GroupResponseDto>, t: Throwable) {
//                println("Ошибка")
//                println(t)
//            }
//        })
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

    private fun setLoadingDialog() {
        pDialog.setMessage("Загрузка...пожалуйста подождите")
        pDialog.setCancelable(false)
        pDialog.show()
    }

    private fun showToastNotification(message: String) {
        val duration = Toast.LENGTH_LONG

        val toast = Toast.makeText(requireContext(), message, duration)
        toast.show()
        val handler = Handler()
        handler.postDelayed({ toast.cancel() }, 1500)
    }

    private fun stopAnimation(btn: CircularProgressButton) {
        btn.background = ContextCompat.getDrawable(requireContext(), R.drawable.admin_bg)
        btn.revertAnimation()
    }


}