package vsu.cs.univtimetable.screens.admin_screens

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.app.AlertDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vsu.cs.univtimetable.R
import vsu.cs.univtimetable.SessionManager
import vsu.cs.univtimetable.TimetableClient
import vsu.cs.univtimetable.api.UserApi
import vsu.cs.univtimetable.dto.UserCreateRequest
import vsu.cs.univtimetable.dto.UserDisplayDto
import vsu.cs.univtimetable.dto.UserResponseDto
import vsu.cs.univtimetable.screens.adapter.OnUserItemClickListener
import vsu.cs.univtimetable.screens.adapter.UserListAdapter
import java.util.concurrent.atomic.AtomicReference

class UserListPageFragment : Fragment(), OnUserItemClickListener {

    private lateinit var userApi: UserApi
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserListAdapter
    private lateinit var searchView: SearchView

    private lateinit var roleBtn: Button
    private lateinit var univBtn: Button
    private lateinit var cityBtn: Button

    private lateinit var userForEdit: UserCreateRequest

    private var searchCity: String? = null
    private var searchRole: String? = null
    private var searchUniv: String? = null

    private var univs = mutableSetOf<String>()
    private var roles = mutableSetOf<String>()
    private var cities = mutableSetOf<String>()
    private var searchItem: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userApi = TimetableClient.getClient().create(UserApi::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_list_page, container, false)
        recyclerView = view.findViewById(R.id.usersRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val addUser = view.findViewById<AppCompatButton>(R.id.addNewUserBtn)
        val refreshFilterBtn = view.findViewById<AppCompatButton>(R.id.refreshFilterBtn)
        val prevPageButton = view.findViewById<ImageButton>(R.id.prevPageButton)
        prevPageButton.setOnClickListener {
            findNavController().navigate(R.id.action_userListPageFragment_to_adminMainPageFragment)
        }

        refreshFilterBtn.setOnClickListener {
            getUsers(null, null, null, null)
        }


        univBtn = view.findViewById(R.id.sortUnivBtn)
        univBtn.setOnClickListener {
            showRadioButtonDialog("Выберите ВУЗ", univs.toList()) { selectedValue ->
                getUsers(selectedValue, null, null, null)
            }
        }
        roleBtn = view.findViewById(R.id.sortRoleBtn)
        roleBtn.setOnClickListener {
            showRadioButtonDialog("Выберите роль", roles.toList()) { selectedValue ->
                getUsers(null, selectedValue, null, null)
            }
        }
        cityBtn = view.findViewById(R.id.sortByCityBtn)
        cityBtn.setOnClickListener {
            showRadioButtonDialog("Выберите город", cities.toList()) { selectedValue ->
                getUsers(null, null, selectedValue, null)
            }
        }

        searchView = view.findViewById(R.id.userSearch)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                getUsers(searchUniv, searchRole, searchCity, newText)
                return false
            }
        })

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
        val bundle = Bundle()

        val token: String? = SessionManager.getToken(requireContext())
        val call = userApi.getUser("Bearer ${token}", user.id.toLong())

        call.enqueue(object : Callback<UserCreateRequest> {
            override fun onResponse(
                call: Call<UserCreateRequest>,
                response: Response<UserCreateRequest>
            ) {
                if (response.isSuccessful) {
                    Log.d("API Request successful", "Получили ${response.code()}")
                    val dataResponse = response.body()
                    println(dataResponse)
                    if (dataResponse != null) {
                        bundle.putInt("id", dataResponse.id)
                        bundle.putBoolean("editable", true)
                        bundle.putString("role", dataResponse.role)
                        bundle.putString("fullName", dataResponse.fullName)
                        bundle.putString("login", dataResponse.username)
                        bundle.putString("email", dataResponse.email)
                        bundle.putString("city", dataResponse.city)
                        bundle.putString("password", dataResponse.password)
                        bundle.putLong("univId", dataResponse.universityId ?: -1L)
                        bundle.putLong("facultyId", dataResponse.facultyId ?: -1L)
                        bundle.putLong("group", dataResponse.groupId ?: -1L)

                        findNavController().navigate(
                            R.id.action_userListPageFragment_to_createUserAuthFragment,
                            bundle
                        )
                    }
                } else {
                    println("Не успешно")
                }
            }

            override fun onFailure(call: Call<UserCreateRequest>, t: Throwable) {
                println("Ошибка")
                println(t)
            }
        })
    }

    override fun onDeleteClick(user: UserDisplayDto) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Удаление пользователя")
            .setMessage("Вы уверены что хотите удалить ${user.fullName} из списка?")
            .setCancelable(true)
            .setPositiveButton("Удалить") { _, _ ->
                delete(user.id) { code ->
                    if (code == 200) {
                        getUsers(null, null, null, null)
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
                        getSearchItems(dataResponse.usersPage.contents)
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

    private fun getUser(id: Long) {
        val token: String? = SessionManager.getToken(requireContext())
        Log.d("API Request failed", "${token}")
        val call = userApi.getUser("Bearer ${token}", id.toLong())

        call.enqueue(object : Callback<UserCreateRequest> {
            override fun onResponse(
                call: Call<UserCreateRequest>,
                response: Response<UserCreateRequest>
            ) {
                if (response.isSuccessful) {
                    Log.d("API Request successful", "Получили ${response.code()}")
                    val dataResponse = response.body()
                    println(dataResponse)
                    if (dataResponse != null) {
                        userForEdit = dataResponse
                    }
                } else {
                    println("Не успешно")
                }
            }

            override fun onFailure(call: Call<UserCreateRequest>, t: Throwable) {
                println("Ошибка")
                println(t)
            }
        })
    }

    private fun delete(id: Int, callback: (Int) -> Unit) {
        val token: String? = SessionManager.getToken(requireContext())
        Log.d("API Request failed", "${token}")
        val call = userApi.deleteUser(
            "Bearer ${token}", id
        )
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("API Request okay", "Удалили ${response.code()}")
                    showToastNotification("Пользователь успешно удален")
                } else {
                    if (response.code() == 403) {
                        showToastNotification("Недостаточно прав доступа для выполнения")
                    }
                    if (response.code() == 404) {
                        showToastNotification("Пользователь по переданному id не был найден")
                    }
                    Log.d("API Request failed", "${response.code()}")
                }
                callback(response.code())
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Обработка ошибки
            }
        })
    }

    private fun showRadioButtonDialog(
        title: String,
        items: List<String>,
        onItemSelected: (String) -> Unit
    ) {
        val selectedValue = AtomicReference<String>()

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)
        builder.setSingleChoiceItems(
            items.toTypedArray(),
            -1
        ) { _, which ->
            selectedValue.set(items[which])
        }
        builder.setPositiveButton("Выбрать") { _, _ ->
            val value = selectedValue.get()
            if (!value.isNullOrEmpty()) {
                onItemSelected(value)
            }
        }
        builder.setNegativeButton("Отмена", null)

        val dialog = builder.create()
        dialog.show()
    }

//    private fun showChosenItem(msg: String) {
//        Snackbar.make(requireView(), msg, Snackbar.LENGTH_SHORT).show()
//    }

    private fun showToastNotification (message: String) {
        val duration = Toast.LENGTH_LONG

        val toast = Toast.makeText(requireContext(), message, duration)
        toast.show()
        val handler = Handler()
        handler.postDelayed({ toast.cancel() }, 1500)
    }

    private fun getSearchItems(userDtoList: List<UserDisplayDto>) {
        for (userDto in userDtoList) {
            if (userDto.univName != null) {
                univs.add(userDto.univName)
            }
            roles.add(userDto.role)
            cities.add(userDto.city)
        }
    }
}