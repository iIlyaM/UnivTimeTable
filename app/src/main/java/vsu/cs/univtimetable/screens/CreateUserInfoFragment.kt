package vsu.cs.univtimetable.screens

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vsu.cs.univtimetable.R
import vsu.cs.univtimetable.SessionManager
import vsu.cs.univtimetable.TimeTableClient
import vsu.cs.univtimetable.api.UserApi
import vsu.cs.univtimetable.dto.CreateUserResponse
import vsu.cs.univtimetable.dto.FacultyResponse
import vsu.cs.univtimetable.dto.GroupDto
import vsu.cs.univtimetable.dto.UnivResponse
import vsu.cs.univtimetable.dto.UserCreateRequest


class CreateUserInfoFragment : Fragment() {

    private lateinit var userApi: UserApi
    private lateinit var createUserResponse: CreateUserResponse
    private val roleType =
        arrayOf("HEADMAN", "ADMIN", "LECTURER")

    private var univMap: MutableMap<String, UnivResponse> = mutableMapOf()
    private var groupMap: MutableMap<String, GroupDto> = mutableMapOf()
    private var facultyMap: MutableMap<String, FacultyResponse> = mutableMapOf()

    private var univList: ArrayList<UnivResponse> = arrayListOf()
    private var roleList: ArrayList<String> = arrayListOf()
    private var facultyList: ArrayList<FacultyResponse> = arrayListOf()
    private var groupList: ArrayList<GroupDto> = arrayListOf()

    private lateinit var roleTextInputLayout: AutoCompleteTextView;
    private lateinit var univTextInputLayout: AutoCompleteTextView;
    private lateinit var facultyTextInputLayout: AutoCompleteTextView;
    private lateinit var groupTextInputLayout: AutoCompleteTextView;

    private lateinit var email: String;
    private lateinit var password: String;
    private lateinit var login: String;
    private lateinit var city: String;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userApi = TimeTableClient.getClient().create(UserApi::class.java)

        val tempEmail = arguments?.getString("email");
        if (tempEmail != null) {
            email = tempEmail
        }
        val tempPass = arguments?.getString("password");
        if (tempPass != null) {
            password = tempPass
        }
        val tempLogin = arguments?.getString("login");
        if (tempLogin != null) {
            login = tempLogin
        }
        val tempCity = arguments?.getString("city");
        if (tempCity != null) {
            city = tempCity
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_create_user_info, container, false)
        val view = inflater.inflate(R.layout.fragment_create_user_info, container, false)

        roleTextInputLayout = view.findViewById(R.id.editRoleAutoCompleteTextView)
        roleTextInputLayout(view)

        univTextInputLayout = view.findViewById(R.id.editUnivAutoCompleteTextView)
        univTextInputLayout.setOnItemClickListener { _, _, position, _ ->
            univTextInputLayout(position, false)
        }
        facultyTextInputLayout = view.findViewById(R.id.editFacultyAutoCompleteTextView)
        facultyTextInputLayout.setOnItemClickListener { _, _, position, _ ->
            facultyTextInputLayout(position, null)
        }

        groupTextInputLayout = view.findViewById(R.id.editGroupNumAutoCompleteTextView)
        groupTextInputLayout.setOnItemClickListener { _, _, _, _ ->
            groupTextInputLayout.error = null
        }

        view.findViewById<Button>(R.id.confUserInfoBtn).setOnClickListener {
            createUser(view.findViewById(R.id.editFullNameText))
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getUserInfo(view)
    }

    private fun createUser(
        fullNameText: EditText
    ) {
        val fullName = fullNameText.text.toString()
        if (fullName.isEmpty()) {
            fullNameText.error = "Введите имя пользователя"
            return
        }
        val role = roleTextInputLayout.text.toString()
        if (role.isEmpty()) {
            roleTextInputLayout.error = "Выберите роль"
            return
        }
        val univId = if (univMap[univTextInputLayout.text.toString()] != null) {
            univMap[univTextInputLayout.text.toString()]?.id
        } else {
            null
        }
        val facId = if (facultyMap[facultyTextInputLayout.text.toString()] != null) {
            facultyMap[facultyTextInputLayout.text.toString()]?.id
        } else {
            null
        }
        val groupId = if (groupMap[groupTextInputLayout.text.toString()] != null) {
            groupMap[groupTextInputLayout.text.toString()]?.id
        } else {
            null
        }
        if (!validUserInfo(role, univId, facId)) {
            return
        }

        val token: String? = SessionManager.getToken(requireContext())
        Log.d("API Request failed", "${token}")
        val editable = arguments?.getBoolean("editable")
        var tempPass: String?;
        tempPass = if (password.isEmpty()) {
            null
        } else {
            password
        }
        var user = UserCreateRequest(
            0,
            role,
            fullName,
            login,
            email,
            city,
            tempPass,
            univId,
            facId,
            groupId
        );
        var call: Call<Void>;
        if (editable != null && editable) {
            val id = arguments?.getInt("id") ?: -1
            user.id = arguments?.getInt("id") ?: -1
            call = userApi.editUser(
                "Bearer ${token}",
                id,
                user
            )
        } else {
            call = userApi.addUser(
                "Bearer ${token}",
                user
            )
        }
        call.enqueue(object : Callback<Void> {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                if (response.isSuccessful) {
                    Log.d("API Request successful", "Получили ${response.code()}")
                } else {
                    println("Не успешно")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                println("Ошибка")
                println(t)
            }
        })

        findNavController().navigate(R.id.action_createUserInfoFragment_to_userListPageFragment)
    }

    private fun getUserInfo(view: View) {
        val token: String? = SessionManager.getToken(requireContext())
        Log.d("API Request failed", "${token}")
        val call = userApi.createUserInfo("Bearer ${token}")

        call.enqueue(object : Callback<CreateUserResponse> {
            override fun onResponse(
                call: Call<CreateUserResponse>,
                response: Response<CreateUserResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("API Request successful", "Получили ${response.code()}")
                    val dataResponse = response.body()
                    println(dataResponse)
                    if (dataResponse != null) {
                        roleList = ArrayList(dataResponse.roles)
                        univList = ArrayList(dataResponse.universityResponses)
                        for (univ in univList) {
                            univMap[univ.universityName] = univ
                        }
                        roleTextInputLayout.setAdapter(
                            ArrayAdapter(
                                requireContext(),
                                R.layout.subj_item,
                                dataResponse.roles
                            )
                        )
                        univTextInputLayout.setAdapter(
                            ArrayAdapter(
                                requireContext(),
                                R.layout.subj_item,
                                univMap.keys.toTypedArray()
                            )
                        )
                        val editable = arguments?.getBoolean("editable")
                        if (editable != null && editable)
                            setFieldsIfEdit(view)
                    }
                } else {
                    println("Не успешно")
                }
            }

            override fun onFailure(call: Call<CreateUserResponse>, t: Throwable) {
                println("Ошибка")
                println(t)
            }
        })
    }

    private fun validUserInfo(
        role: String,
        univId: Long?,
        facId: Long?
    ): Boolean {
        var isValid = true;
        if (role == "HEADMAN") {
            if (univId == null) {
                univTextInputLayout.error = "Выберите университет"
                isValid = false;
            }
            if (facId == null) {
                facultyTextInputLayout.error = "Выберите факультет"
                isValid = false;
            }
        } else if (role == "LECTURER") {
            if (univId == null) {
                univTextInputLayout.error = "Выберите университет"
                isValid = false;
            }
            if (facId == null) {
                facultyTextInputLayout.error = "Выберите факультет"
                isValid = false;
            }
        }
        return isValid;
    }

    private fun setFieldsIfEdit(view: View) {
        val role = arguments?.getString("role")
        roleTextInputLayout.setText(role)
        if (role != null)
            roleInputValid(view, role)
        val fullName = arguments?.getString("fullName")
        if (fullName != null)
            view.findViewById<EditText>(R.id.editFullNameText).setText(fullName)
        val univId = arguments?.getLong("univId")
        if (univId != null && univId != -1L) {
            val univ = univList.firstOrNull { it.id == univId }
            val position = univList.indexOf(univ)
            univTextInputLayout(position, true)
            if (univ != null) {
                univTextInputLayout.setText(univ.universityName, false)
            }
        }
        val facultyId = arguments?.getLong("facultyId")
        if (facultyId != null && facultyId != -1L) {
            val faculty = facultyList.firstOrNull { it.id == facultyId };
            val position = facultyList.indexOf(faculty)
            val groupId = arguments?.getLong("group")
            facultyTextInputLayout(position, groupId)
            if (faculty != null) {
                facultyTextInputLayout.setText(faculty.name, false)
            }
        }
        val groupId = arguments?.getLong("group")
        if (groupId != null && groupId != -1L) {
            val group = groupList.firstOrNull { it.id == groupId };
            for ((key, mapValue) in groupMap) {
                if (mapValue.id == group?.id) {
                    groupTextInputLayout.setText(key, false)
                }
            }
        }
    }

    private fun roleTextInputLayout(view: View) {
        roleTextInputLayout.setOnItemClickListener { _, _, position, _ ->
            roleInputValid(view, roleList[position])
            roleTextInputLayout.error = null
        }
    }

    private fun univTextInputLayout(position: Int, editable: Boolean?) {
        facultyMap.clear()
        facultyList = ArrayList(univMap[univList[position].universityName]?.facultyDtos!!)
        for (fac in facultyList) {
            facultyMap[fac.name] = fac
        }
        facultyTextInputLayout.setAdapter(
            ArrayAdapter(
                requireContext(),
                R.layout.subj_item,
                facultyList.map { it.name }.toList()
            )
        )
        univTextInputLayout.error = null
    }

    private fun facultyTextInputLayout(position: Int, headmanGroupId: Long?) {
        groupMap.clear()
        groupList = ArrayList(facultyMap[facultyList[position].name]?.groups!!)
        for (group in groupList) {
            if (group.headman == null || headmanGroupId == group.id) {
                groupMap["Курс: " + group.courseNumber + ", группа: " + group.groupNumber] = group
            }
        }
        groupTextInputLayout.setAdapter(
            ArrayAdapter(
                requireContext(),
                R.layout.subj_item,
                groupMap.keys.toTypedArray()
            )
        )
        groupTextInputLayout.text = null
        facultyTextInputLayout.error = null
    }

    private fun roleInputValid(view: View, role: String) {
        when (role) {
            "LECTURER" -> {
                view.findViewById<TextInputLayout>(R.id.editUnivText).visibility = View.VISIBLE
                view.findViewById<TextInputLayout>(R.id.editFacultyText).visibility = View.VISIBLE
                view.findViewById<TextInputLayout>(R.id.editGroupNumText).visibility =
                    View.INVISIBLE
                groupTextInputLayout.text = null
            }

            "ADMIN" -> {
                view.findViewById<TextInputLayout>(R.id.editUnivText).visibility = View.INVISIBLE
                univTextInputLayout.text = null
                view.findViewById<TextInputLayout>(R.id.editFacultyText).visibility = View.INVISIBLE
                facultyTextInputLayout.text = null
                view.findViewById<TextInputLayout>(R.id.editGroupNumText).visibility =
                    View.INVISIBLE
                groupTextInputLayout.text = null
            }

            else -> {
                view.findViewById<TextInputLayout>(R.id.editUnivText).visibility = View.VISIBLE
                view.findViewById<TextInputLayout>(R.id.editFacultyText).visibility = View.VISIBLE
                view.findViewById<TextInputLayout>(R.id.editGroupNumText).visibility = View.VISIBLE
            }
        }
    }

}