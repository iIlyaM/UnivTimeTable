package vsu.cs.univtimetable.screens.admin_screens.users

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
import androidx.lifecycle.ViewModelProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vsu.cs.univtimetable.R
import vsu.cs.univtimetable.SessionManager
import vsu.cs.univtimetable.TimetableClient
import vsu.cs.univtimetable.api.UserApi
import vsu.cs.univtimetable.dto.user.UserCreateRequest
import vsu.cs.univtimetable.dto.user.UserDisplayDto
import vsu.cs.univtimetable.repository.UserRepository
import vsu.cs.univtimetable.screens.adapter.OnUserDeleteInterface
import vsu.cs.univtimetable.screens.adapter.OnUserEditInterface
import vsu.cs.univtimetable.screens.adapter.UserListAdapter
import vsu.cs.univtimetable.screens.admin_screens.univ.UnivViewModelFactory
import java.util.concurrent.atomic.AtomicReference

class UserListPageFragment : Fragment(), OnUserEditInterface, OnUserDeleteInterface {

    private lateinit var userApi: UserApi
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserListAdapter
    private lateinit var searchView: SearchView
    private lateinit var userViewModel: UserViewModel

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
    private var searchParams = mutableListOf<String?>(null, null, null)


//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//    }
    //Продумать обработку в случае если token закончится

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_list_page, container, false)
        userApi = TimetableClient.getClient().create(UserApi::class.java)
        val token = SessionManager.getToken(requireContext())!!
        val userRepository = UserRepository(userApi, token)
        userViewModel =
            ViewModelProvider(
                requireActivity(),
                UnivViewModelFactory(userRepository, token)
            )[UserViewModel::class.java]

        recyclerView = view.findViewById(R.id.usersRecyclerView)
        initRV(recyclerView)
        val addUser = view.findViewById<AppCompatButton>(R.id.addNewUserBtn)
        val refreshFilterBtn = view.findViewById<AppCompatButton>(R.id.refreshFilterBtn)
        val prevPageButton = view.findViewById<ImageButton>(R.id.prevPageButton)
        prevPageButton.setOnClickListener {
            findNavController().navigate(R.id.action_userListPageFragment_to_adminMainPageFragment)
        }

        refreshFilterBtn.setOnClickListener {
            searchParams = mutableListOf<String?>(null, null, null)
            getUsers(searchParams, null)
        }


        univBtn = view.findViewById(R.id.sortUnivBtn)
        univBtn.setOnClickListener {
            showRadioButtonDialog("Выберите ВУЗ", univs.toList()) { selectedValue ->
                searchParams[0] = selectedValue
                getUsers(searchParams, null)
            }
        }
        roleBtn = view.findViewById(R.id.sortRoleBtn)
        roleBtn.setOnClickListener {
            showRadioButtonDialog("Выберите роль", roles.toList()) { selectedValue ->
                searchParams[1] = selectedValue
                getUsers(searchParams, null)
            }
        }
        cityBtn = view.findViewById(R.id.sortByCityBtn)
        cityBtn.setOnClickListener {
            showRadioButtonDialog("Выберите город", cities.toList()) { selectedValue ->
                searchParams[2] = selectedValue
                getUsers(searchParams, null)
            }
        }

        searchView = view.findViewById(R.id.userSearch)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                getUsers(searchParams, newText)
                return false
            }
        })

        addUser.setOnClickListener {
            findNavController().navigate(R.id.action_userListPageFragment_to_createUserInfoFragment)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel.userList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            getSearchItems(it)
        }
        userViewModel.errorMsg.observe(viewLifecycleOwner) {
        }
        getUsers(searchParams, null)

    }


    //    mutableListOf<String?
//university: String?, role: String?, city: String?, name: String?
    private fun getUsers(searchParams: MutableList<String?>, name: String?) {
        val token: String? = SessionManager.getToken(requireContext())
//        Log.d("API Request failed", "${token}")
//        val call = userApi.getUsers(
//            "Bearer ${token}",
//            searchParams[0],
//            searchParams[1],
//            searchParams[2],
//            name
//        )

        userViewModel.getAllUsers(
            searchParams[0],
            searchParams[1],
            searchParams[2],
            name
        )
//        getSearchItems(list)

//        call.enqueue(object : Callback<UserResponseDto> {
//            override fun onResponse(
//                call: Call<UserResponseDto>,
//                response: Response<UserResponseDto>
//            ) {
//                if (response.isSuccessful) {
//                    Log.d("API Request successful", "Получили ${response.code()}")
//                    val dataResponse = response.body()
//                    println(dataResponse)
//                    if (dataResponse != null) {
//                        userViewModel.getAllUsers(dataResponse.usersPage.contents)
//                        getSearchItems(dataResponse.usersPage.contents)
//                    }
//
//                } else {
//                    println("Не успешно")
//                }
//            }
//
//            override fun onFailure(call: Call<UserResponseDto>, t: Throwable) {
//                println("Ошибка")
//                println(t)
//            }
//        })
    }

    private fun getUser(id: Long) {
        val token: String? = SessionManager.getToken(requireContext())
        userViewModel.getUser(id)
//        Log.d("API Request failed", "${token}")
//        val call = userApi.getUser("Bearer ${token}", id.toLong())
//
//        call.enqueue(object : Callback<UserCreateRequest> {
//            override fun onResponse(
//                call: Call<UserCreateRequest>,
//                response: Response<UserCreateRequest>
//            ) {
//                if (response.isSuccessful) {
//                    Log.d("API Request successful", "Получили ${response.code()}")
//                    val dataResponse = response.body()
//                    println(dataResponse)
//                    if (dataResponse != null) {
//                        userForEdit = dataResponse
//                    }
//                } else {
//                    println("Не успешно")
//                }
//            }
//
//            override fun onFailure(call: Call<UserCreateRequest>, t: Throwable) {
//                println("Ошибка")
//                println(t)
//            }
//        })
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
                    Log.d("Delete", "Удалили ${id}")
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

    private fun showToastNotification(message: String) {
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

    override fun onEditClick(userId: Int) {
        val bundle = Bundle()
        bundle.putInt("id", userId)
        bundle.putBoolean("editable", true)
        findNavController().navigate(
            R.id.action_userListPageFragment_to_createUserInfoFragment,
            bundle
        )



//        val call = userApi.getUser("Bearer ${token}", userId.toLong())

//        call.enqueue(object : Callback<UserCreateRequest> {
//            override fun onResponse(
//                call: Call<UserCreateRequest>,
//                response: Response<UserCreateRequest>
//            ) {
//                if (response.isSuccessful) {
//                    Log.d("API Request successful", "Получили ${response.code()}")
//                    val dataResponse = response.body()
//                    println(dataResponse)
//                    if (dataResponse != null) {
//                        bundle.putInt("id", dataResponse.id)
//                        bundle.putBoolean("editable", true)
//                        bundle.putString("role", dataResponse.role)
//                        bundle.putString("fullName", dataResponse.fullName)
//                        bundle.putString("login", dataResponse.username)
//                        bundle.putString("email", dataResponse.email)
//                        bundle.putString("city", dataResponse.city)
//                        bundle.putString("password", dataResponse.password)
//                        bundle.putLong("univId", dataResponse.universityId ?: -1L)
//                        bundle.putLong("facultyId", dataResponse.facultyId ?: -1L)
//                        bundle.putLong("group", dataResponse.groupId ?: -1L)
//
//                        findNavController().navigate(
//                            R.id.action_userListPageFragment_to_createUserInfoFragment,
//                            bundle
//                        )
//                    }
//                } else {
//                    println("Не успешно")
//                }
//            }
//
//            override fun onFailure(call: Call<UserCreateRequest>, t: Throwable) {
//                println("Ошибка")
//                println(t)
//            }
//        })
    }

    override fun onDeleteClick(userId: Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Удаление пользователя")
            .setMessage("Вы уверены что хотите удалить из списка?")
            .setCancelable(true)
            .setPositiveButton("Удалить") { _, _ ->
                delete(userId) { code ->
                    if (code == 200) {
                        getUsers(searchParams, null)
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

    private fun initRV(rv: RecyclerView) {
        adapter = UserListAdapter(
            this@UserListPageFragment,
            this@UserListPageFragment
        )
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter
    }

}