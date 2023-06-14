package vsu.cs.univtimetable.screens

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
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

    private var courseSearch: Int? = null
    private var groupSeacrh: Int? = null
    private var order = "ASC"

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

        val sortBtn = view.findViewById<ImageButton>(R.id.sortByCourseBtn)
        sortBtn.setOnClickListener {
            order = if (order.equals("ASC")) {
                "DESC"
            } else {
                "ASC"
            }
            getGroups(courseSearch, order, groupSeacrh)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getGroups(null, null, null)
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

    override fun onEditClick(groupDto: GroupDto) {
        val bundle = Bundle()

        val token: String? = SessionManager.getToken(requireContext())
        val call = groupApi.getGroup("Bearer ${token}", groupDto.id ?: -1L)

        call.enqueue(object : Callback<GroupDto> {
            override fun onResponse(
                call: Call<GroupDto>,
                response: Response<GroupDto>
            ) {
                if (response.isSuccessful) {
                    Log.d("API Request successful", "Получили ${response.code()}")
                    val dataResponse = response.body()
                    println(dataResponse)
                    if (dataResponse != null) {
                        bundle.putLong("id", dataResponse.id ?: -1L)
                        bundle.putBoolean("editable", true)
                        bundle.putInt("groupNumber", dataResponse.groupNumber)
                        bundle.putInt("courseNumber", dataResponse.courseNumber)
                        bundle.putInt("studentsAmount", dataResponse.studentsAmount)
                        bundle.putInt("headmanId", dataResponse.headman?.id ?: -1)
                        bundle.putInt("facultyId", getFacultyId())

                        findNavController().navigate(
                            R.id.action_groupListPageFragment_to_createGroupPageFragment,
                            bundle
                        )
                    }
                } else {
                    println("Не успешно")
                }
            }

            override fun onFailure(call: Call<GroupDto>, t: Throwable) {
                println("Ошибка")
                println(t)
            }
        })
    }

    override fun onDeleteClick(groupDto: GroupDto) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Удаление группы")
            .setMessage(
                "Вы уверены что хотите удалить группу курса: ${groupDto.courseNumber}, " +
                        "номер: ${groupDto.groupNumber} из списка?"
            )
            .setCancelable(true)
            .setPositiveButton("Удалить") { _, _ ->
                if (groupDto.id != null) {
                    delete(groupDto.id) { code ->
                        if (code == 200) {
                            getGroups(null, null, null)
                        }
                    }
                }
            }
            .setNegativeButton(
                "Отмена"
            ) { _, _ ->
            }
        builder.create()
        builder.show()
    }

    private fun delete(id: Long, callback: (Int) -> Unit) {
        val token: String? = SessionManager.getToken(requireContext())
        Log.d("API Request failed", "${token}")
        val call = groupApi.deleteGroups(
            "Bearer ${token}", id
        )
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("API Request okay", "Удалили ${response.code()}")
                } else {
                    Log.d("API Request failed", "${response.code()}")
                }
                callback(response.code())
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Обработка ошибки
            }
        })
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