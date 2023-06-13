package vsu.cs.univtimetable.screens

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SearchView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vsu.cs.univtimetable.R
import vsu.cs.univtimetable.SessionManager
import vsu.cs.univtimetable.TimetableClient
import vsu.cs.univtimetable.api.GroupApi
import vsu.cs.univtimetable.dto.GroupDto
import vsu.cs.univtimetable.dto.GroupResponseDto
import vsu.cs.univtimetable.screens.adapter.GroupListAdapter
import vsu.cs.univtimetable.screens.adapter.OnGroupItemClickListener

class GroupListPageFragment : Fragment(), OnGroupItemClickListener {


    private lateinit var groupApi: GroupApi
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GroupListAdapter
    private lateinit var searchByCourseView: SearchView
    private lateinit var searchByGroupView: SearchView

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
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val addGroupBtn = view.findViewById<AppCompatButton>(R.id.addNewGroupBtn)
        addGroupBtn.setOnClickListener {
            sendId()
        }

        val prevPageButton = view.findViewById<ImageButton>(R.id.prevPageButton)
        prevPageButton.setOnClickListener {
            findNavController().navigate(R.id.action_groupListPageFragment_to_facultyListPageFragment)
        }

        val mainPageButton = view.findViewById<ImageButton>(R.id.mainPageButton)
        mainPageButton.setOnClickListener {
            findNavController().navigate(R.id.action_groupListPageFragment_to_adminMainPageFragment)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getGroups(null, null, null)
        searchByCourseView = view.findViewById(R.id.searchByCourseView)

        searchByCourseView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                }

                return true
            }
        })

        searchByGroupView = view.findViewById(R.id.searchByGroupView)

        searchByGroupView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                }

                return true
            }
        })
    }

    override fun onEditClick(groupDto: GroupDto) {
        TODO("Not yet implemented")
    }

    override fun onDeleteClick(groupDto: GroupDto) {
        TODO("Not yet implemented")
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

        Log.d("API Request failed", "${token}")
        val call = groupApi.getGroups("Bearer ${token}", getFacultyId(), course, order, groupNumber)

        call.enqueue(object : Callback<GroupResponseDto> {
            override fun onResponse(
                call: Call<GroupResponseDto>,
                response: Response<GroupResponseDto>
            ) {
                if (response.isSuccessful) {
                    Log.d("API Request successful", "Получили ${response.code()}")
                    val dataResponse = response.body()
                    println(dataResponse)
                    if (dataResponse != null) {
                        adapter = GroupListAdapter(
                            requireContext(),
                            dataResponse.groupsPage.contents,
                            this@GroupListPageFragment
                        )
                    }
                    recyclerView.adapter = adapter
                } else {
                    println("Не успешно")
                }
            }

            override fun onFailure(call: Call<GroupResponseDto>, t: Throwable) {
                println("Ошибка")
                println(t)
            }
        })
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


}