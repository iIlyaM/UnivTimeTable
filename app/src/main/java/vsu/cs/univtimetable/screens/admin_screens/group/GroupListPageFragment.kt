package vsu.cs.univtimetable.screens.admin_screens.group

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.widget.AppCompatButton
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vsu.cs.univtimetable.R
import vsu.cs.univtimetable.SessionManager
import vsu.cs.univtimetable.TimetableClient
import vsu.cs.univtimetable.api.GroupApi
import vsu.cs.univtimetable.repository.GroupRepository
import vsu.cs.univtimetable.screens.adapter.GroupListAdapter
import vsu.cs.univtimetable.screens.adapter.OnGroupDeleteClickInterface
import vsu.cs.univtimetable.screens.adapter.OnGroupEditClickInterface
import vsu.cs.univtimetable.screens.admin_screens.univ.UnivViewModelFactory

class GroupListPageFragment : Fragment(), OnGroupEditClickInterface, OnGroupDeleteClickInterface {


    private lateinit var groupApi: GroupApi
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GroupListAdapter
    private lateinit var searchByCourseView: SearchView
    private lateinit var searchByGroupView: SearchView
    private lateinit var groupViewModel: GroupViewModel


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
            bundle.putInt("univId", universityId)
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
        groupViewModel.getGroup(groupId)
        sendGroupData()
    }

    private fun sendGroupData() {
        val bundle = Bundle()
        groupViewModel.group.observe(viewLifecycleOwner) {
            bundle.putLong("id", it!!.id ?: -1L)
            bundle.putBoolean("editable", true)
            bundle.putInt("groupNumber", it.groupNumber)
            bundle.putInt("courseNumber", it.courseNumber)
            bundle.putInt("studentsAmount", it.studentsAmount)
            bundle.putInt("headmanId", it.headman?.id ?: -1)
            bundle.putInt("facultyId", getFacultyId())

            findNavController().navigate(
                R.id.action_groupListPageFragment_to_createGroupPageFragment,
                bundle
            )
        }
    }

//    fun <T> LiveData<T>.observeOnce(observer: (T) -> Unit) {
//        observeForever(object: Observer<T> {
//            override fun onChanged(value: T) {
//                removeObserver(this)
//                observer(value)
//            }
//        })
//    }
//
//    private fun <T> LiveData<T>.observeOnce(owner: LifecycleOwner, observer: (T) -> Unit) {
//        observe(owner, object: Observer<T> {
//            override fun onChanged(value: T) {
//                removeObserver(this)
//                observer(value)
//            }
//        })
//    }

    override fun onDeleteClick(groupId: Long) {
        val builder = AlertDialog.Builder(requireContext())
        groupViewModel.getGroup(groupId)
        var courseNumber: Int = 0
        var groupNumber: Int = 0
        groupViewModel.group.observe(viewLifecycleOwner) {
            courseNumber = it!!.courseNumber
            groupNumber = it.groupNumber
        }
        builder.setTitle("Удаление группы")
            .setMessage(
                "Вы уверены что хотите удалить группу курса: ${courseNumber}, " +
                        "номер: $groupNumber из списка?"
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

    private fun delete(id: Long) {

        groupViewModel.deleteGroups(id, getFacultyId())
//        val call = groupApi.deleteGroups(
//            "Bearer ${token}", id
//        )
//        call.enqueue(object : Callback<Void> {
//            override fun onResponse(call: Call<Void>, response: Response<Void>) {
//                if (response.isSuccessful) {
//                    Log.d("API Request okay", "Удалили ${response.code()}")
//                } else {
//                    Log.d("API Request failed", "${response.code()}")
//                }
//                callback(response.code())
//            }
//
//            override fun onFailure(call: Call<Void>, t: Throwable) {
//                // Обработка ошибки
//            }
//        })
    }


    private fun getUnivId(): Int {
        val id = arguments?.getInt("univId")
        var univId: Int = 0
        if (id != null) {
            univId = id
        }
        return univId
    }
//    @Query("course") course: Int?,
//    @Query("order") order: String?,
//    @Query("groupNumber") groupNumber: Int?,

    private fun getGroups(course: Int?, order: String?, groupNumber: Int?) {
        val token: String? = SessionManager.getToken(requireContext())

        groupViewModel.getGroups(getFacultyId(), course, order, groupNumber)
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


}