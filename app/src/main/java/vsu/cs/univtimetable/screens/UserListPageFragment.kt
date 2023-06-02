package vsu.cs.univtimetable.screens

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import vsu.cs.univtimetable.TimeTableClient
import vsu.cs.univtimetable.api.UserApi
import vsu.cs.univtimetable.dto.UserDisplayDto
import vsu.cs.univtimetable.dto.UserResponseDto
import vsu.cs.univtimetable.screens.adapter.OnUserItemClickListener
import vsu.cs.univtimetable.screens.adapter.UserListAdapter

class UserListPageFragment : Fragment(), OnUserItemClickListener {

    private lateinit var userApi: UserApi
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserListAdapter
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userApi = TimeTableClient.getClient().create(UserApi::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        return inflater.inflate(R.layout.fragment_user_list_page, container, false)
        val view = inflater.inflate(R.layout.fragment_user_list_page, container, false)
        recyclerView = view.findViewById(R.id.usersRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val addUser = view.findViewById<AppCompatButton>(R.id.addNewUserBtn)
        addUser.setOnClickListener {
            findNavController().navigate(R.id.action_userListPageFragment_to_createUserAuthFragment)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getUsers(null, null, null, null)
    }

    override fun onEditClick(user: UserDisplayDto) {
        TODO("Not yet implemented")
    }

    override fun onDeleteClick(user: UserDisplayDto) {
        TODO("Not yet implemented")
    }

    private fun getUsers(university: String?, role: String?, city: String?, name: String?) {

        val token: String? = SessionManager.getToken(requireContext())
        Log.d("API Request failed", "${token}")
        val call = userApi.getUsers("Bearer ${token}", university, role, city, name)


        call.enqueue(object : Callback<UserResponseDto> {
            override fun onResponse(
                call: Call<UserResponseDto>,
                response: Response<UserResponseDto>
            ) {
                if (response.isSuccessful) {
                    Log.d("API Request successful", "Получили ${response.code()}")
                    val dataResponse = response.body()
                    println(dataResponse)
                    if (dataResponse != null) {
                        adapter = UserListAdapter(
                            requireContext(),
                            dataResponse.usersPage.contents,
                            this@UserListPageFragment
                        )
                    }
                    recyclerView.adapter = adapter

                } else {
                    println("Не успешно")
                }
            }

            override fun onFailure(call: Call<UserResponseDto>, t: Throwable) {
                println("Ошибка")
                println(t)
            }
        })
    }
}