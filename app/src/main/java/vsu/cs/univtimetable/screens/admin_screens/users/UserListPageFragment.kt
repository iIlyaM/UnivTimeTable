package vsu.cs.univtimetable.screens.admin_screens.users

import android.app.ProgressDialog
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
import vsu.cs.univtimetable.utils.NotificationManager
import vsu.cs.univtimetable.utils.Status
import java.util.concurrent.atomic.AtomicReference

class UserListPageFragment : Fragment(), OnUserEditInterface, OnUserDeleteInterface {

    private lateinit var userApi: UserApi
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserListAdapter
    private lateinit var searchView: SearchView
    private lateinit var userViewModel: UserViewModel
    private lateinit var pDialog: ProgressDialog

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

        pDialog = ProgressDialog(context)
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

    private fun getUsers(searchParams: MutableList<String?>, name: String?) {

        userViewModel.getAllUsers(
            searchParams[0],
            searchParams[1],
            searchParams[2],
            name
        ).observe(viewLifecycleOwner) {
            it?.let {
                when (it.status) {
                    Status.SUCCESS -> {
                        clearSortBtns()
                        getSearchItems(it.data!!)
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

    private fun getUser(id: Long) {
        userViewModel.getUser(id).observe(viewLifecycleOwner) {
            it?.let {
                when (it.status) {
                    Status.SUCCESS -> {
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
        userViewModel.deleteUser(id).observe(viewLifecycleOwner) {
            it?.let {
                when (it.status) {
                    Status.SUCCESS -> {
                        getUsers(searchParams, null)
                        pDialog.dismiss()
                        NotificationManager.showToastNotification(requireContext(),"Пользователь успешно удален")
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
        clearSortBtns()
        findNavController().navigate(
            R.id.action_userListPageFragment_to_createUserInfoFragment,
            bundle
        )
    }

    override fun onDeleteClick(userId: Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Удаление пользователя")
            .setMessage("Вы уверены что хотите удалить из списка?")
            .setCancelable(true)
            .setPositiveButton("Удалить") { _, _ ->
                delete(userId)
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

    fun clearSortBtns() {
        univs.clear()
        cities.clear()
        roles.clear()
    }

}