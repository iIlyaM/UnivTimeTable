package vsu.cs.univtimetable.screens.admin_screens.users

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Toast
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.github.leandroborgesferreira.loadingbutton.customViews.CircularProgressButton
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vsu.cs.univtimetable.BtnLoadingProgressbar
import vsu.cs.univtimetable.R
import vsu.cs.univtimetable.SessionManager
import vsu.cs.univtimetable.TimetableClient
import vsu.cs.univtimetable.api.UserApi
import vsu.cs.univtimetable.dto.user.CreateUserResponse
import vsu.cs.univtimetable.dto.faculty.FacultyResponse
import vsu.cs.univtimetable.dto.group.GroupDto
import vsu.cs.univtimetable.dto.univ.UnivResponse
import vsu.cs.univtimetable.dto.user.UserCreateRequest
import vsu.cs.univtimetable.repository.UserRepository
import vsu.cs.univtimetable.screens.admin_screens.univ.UnivViewModelFactory


class CreateUserInfoFragment : Fragment() {

    private lateinit var userApi: UserApi
    private lateinit var createUserResponse: CreateUserResponse
    private lateinit var userViewModel: UserViewModel
    private val roleType =
        arrayOf("HEADMAN", "ADMIN", "LECTURER")

    private var univMap: MutableMap<String, UnivResponse> = mutableMapOf()
    private var groupMap: MutableMap<String, GroupDto> = mutableMapOf()
    private var facultyMap: MutableMap<String, FacultyResponse> = mutableMapOf()

    private var univList: ArrayList<UnivResponse> = arrayListOf()
    private var roleList: ArrayList<String> = arrayListOf()
    private var facultyList: ArrayList<FacultyResponse> = arrayListOf()
    private var groupList: ArrayList<GroupDto> = arrayListOf()

    private lateinit var roleTextInputLayout: AutoCompleteTextView
    private lateinit var univTextInputLayout: AutoCompleteTextView
    private lateinit var facultyTextInputLayout: AutoCompleteTextView
    private lateinit var groupTextInputLayout: AutoCompleteTextView

    private lateinit var email: String
    private lateinit var password: String
    private lateinit var login: String
    private lateinit var city: String
    private lateinit var createBtn: CircularProgressButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userApi = TimetableClient.getClient().create(UserApi::class.java)

        val tempEmail = arguments?.getString("email")
        if (tempEmail != null) {
            email = tempEmail
        }
        val tempPass = arguments?.getString("password")
        if (tempPass != null) {
            password = tempPass
        }
        val tempLogin = arguments?.getString("login")
        if (tempLogin != null) {
            login = tempLogin
        }
        val tempCity = arguments?.getString("city")
        if (tempCity != null) {
            city = tempCity
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_user_info, container, false)

        val token = SessionManager.getToken(requireContext())!!
        val userRepository = UserRepository(userApi, token)
        userViewModel =
            ViewModelProvider(
                requireActivity(),
                UnivViewModelFactory(userRepository, token)
            )[UserViewModel::class.java]

        val tilUniv = view.findViewById<TextInputLayout>(R.id.editUnivText)
        tilUniv.boxStrokeColor =
            ContextCompat.getColor(requireContext(), R.color.adminsColor)
        tilUniv.setBoxStrokeColorStateList(
            ContextCompat.getColorStateList(
                requireContext(),
                R.color.admin_selector
            )!!
        )
        tilUniv.boxStrokeWidth = resources.getDimensionPixelSize(R.dimen.new_stroke_width)

        val tilFaculty = view.findViewById<TextInputLayout>(R.id.editFacultyText)
        tilFaculty.boxStrokeColor =
            ContextCompat.getColor(requireContext(), R.color.adminsColor)
        tilFaculty.setBoxStrokeColorStateList(
            ContextCompat.getColorStateList(
                requireContext(),
                R.color.admin_selector
            )!!
        )
        tilFaculty.boxStrokeWidth = resources.getDimensionPixelSize(R.dimen.new_stroke_width)

        val tilGroupNum = view.findViewById<TextInputLayout>(R.id.editGroupNumText)
        tilGroupNum.boxStrokeColor =
            ContextCompat.getColor(requireContext(), R.color.adminsColor)
        tilGroupNum.setBoxStrokeColorStateList(
            ContextCompat.getColorStateList(
                requireContext(),
                R.color.admin_selector
            )!!
        )
        tilGroupNum.boxStrokeWidth = resources.getDimensionPixelSize(R.dimen.new_stroke_width)

        val prevPageButton = view.findViewById<ImageButton>(R.id.prevPageButton)
        prevPageButton.setOnClickListener {
//            findNavController().navigate(R.id.action_createUserInfoFragment_to_createUserAuthFragment)
            findNavController().popBackStack()
        }

        val mainPageButton = view.findViewById<ImageButton>(R.id.mainPageButton)
        mainPageButton.setOnClickListener {
            findNavController().navigate(R.id.action_createUserInfoFragment_to_adminMainPageFragment)
        }


        roleTextInputLayout = view.findViewById(R.id.editRoleAutoCompleteTextView)
        roleTextInputLayout(view)

        univTextInputLayout = view.findViewById(R.id.editUnivAutoCompleteTextView)
        univTextInputLayout.setOnItemClickListener { _, _, position, _ ->
            univTextInputLayout(position)
        }
        facultyTextInputLayout = view.findViewById(R.id.editFacultyAutoCompleteTextView)
        facultyTextInputLayout.setOnItemClickListener { _, _, position, _ ->
            facultyTextInputLayout(position, null)
        }

        groupTextInputLayout = view.findViewById(R.id.editGroupNumAutoCompleteTextView)
        groupTextInputLayout.setOnItemClickListener { _, _, _, _ ->
            groupTextInputLayout.error = null
        }

        setFieldsIfEdit(view)
        createBtn = view.findViewById<CircularProgressButton>(R.id.create_user_btn)
        createBtn.setOnClickListener {
//            val progressbar = BtnLoadingProgressbar(it)
//            progressbar.setLoading()
            createBtn.startAnimation()
            createUser(
                view.findViewById<EditText>(R.id.editTextTextEmailAddress),
                view.findViewById<EditText>(R.id.editLoginText),
                view.findViewById<EditText>(R.id.editUserCity),
                view.findViewById<EditText>(R.id.editTextTextPassword2),
                view.findViewById(R.id.editFullNameText)
            )
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getUserInfo(view)
    }

    private fun createUser(
        email: EditText,
        login: EditText,
        city: EditText,
        password: EditText,
        fullNameText: EditText
    ) {
        val editable = arguments?.getBoolean("editable");

        if (email.text.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email.text.toString())
                .matches()
        ) {
            email.error = "Почта введена некорректно"
            showToastNotification("Пожалуйста, заполните все поля")
            stopAnimation(createBtn)
            return
        }
        if (password.text.isEmpty() && editable == null) {
            password.error = "Вы должны ввести пароль"
            showToastNotification("Пожалуйста, заполните все поля")
            stopAnimation(createBtn)
            return
        }
        if (login.text.isEmpty()) {
            login.error = "Введите имя пользователя"
            showToastNotification("Пожалуйста, заполните все поля")
            stopAnimation(createBtn)
            return
        }
        if (city.text.isEmpty()) {
            city.error = "Введите город"
            showToastNotification("Пожалуйста, заполните все поля")
            stopAnimation(createBtn)
            return
        }

        val fullName = fullNameText.text.toString()
        if (fullName.isEmpty()) {
            fullNameText.error = "Введите имя пользователя"
            showToastNotification("Пожалуйста, заполните имя пользователя")
            stopAnimation(createBtn)
            return
        }
        val role = roleTextInputLayout.text.toString()
        if (role.isEmpty()) {
            roleTextInputLayout.error = "Выберите роль"
            stopAnimation(createBtn)
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
            stopAnimation(createBtn)
            return
        }

        val token: String? = SessionManager.getToken(requireContext())
        Log.d("API Request failed", "${token}")
//        val editable = arguments?.getBoolean("editable")
        val tempPass: String?
        tempPass = if (password.text.isEmpty()) {
            null
        } else {
            password.text.toString()
        }
//        val user = UserCreateRequest(
//            0,
//            role,
//            fullName,
//            login,
//            email,
//            city,
//            tempPass,
//            univId,
//            facId,
//            groupId
//        )

        if (editable != null && editable) {
            val id = arguments?.getInt("id") ?: -1
            userViewModel.editUser(
                id,
                role,
                fullName,
                login.text.toString(),
                email.text.toString(),
                city.text.toString(),
                tempPass,
                univId,
                facId,
                groupId
            )
        } else {
            userViewModel.addUser(
                0,
                role,
                fullName,
                login.text.toString(),
                email.text.toString(),
                city.text.toString(),
                tempPass,
                univId,
                facId,
                groupId
            )
        }


//        val call: Call<Void>;
//        if (editable != null && editable) {
//            val id = arguments?.getInt("id") ?: -1
//            user.id = arguments?.getInt("id") ?: -1
//            call = userApi.editUser(
//                "Bearer ${token}",
//                id,
//                user
//            )
//        } else {
//            call = userApi.addUser(
//                "Bearer ${token}",
//                user
//            )
//        }
//        call.enqueue(object : Callback<Void> {
//            override fun onResponse(
//                call: Call<Void>,
//                response: Response<Void>
//            ) {
//                if (response.isSuccessful) {
//                    Log.d("API Request successful", "Получили ${response.code()}")
//                } else {
//                    println("Не успешно")
//                }
//            }
//
//            override fun onFailure(call: Call<Void>, t: Throwable) {
//                println("Ошибка")
//                println(t)
//            }
//        })

        findNavController().popBackStack()
    }

    private fun getUserInfo(view: View) {
        val token: String? = SessionManager.getToken(requireContext())
        userViewModel.getUserInfo()



        userViewModel.userInfo.observe(viewLifecycleOwner) {
            roleList = ArrayList(it.roles)
            univList = ArrayList(it.universityResponses)
            for (univ in univList) {
                univMap[univ.universityName] = univ
            }
            roleTextInputLayout.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.subj_item,
                    it.roles
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
//        Log.d("API Request failed", "${token}")
//        val call = userApi.createUserInfo("Bearer ${token}")
//
//        call.enqueue(object : Callback<CreateUserResponse> {
//            override fun onResponse(
//                call: Call<CreateUserResponse>,
//                response: Response<CreateUserResponse>
//            ) {
//                if (response.isSuccessful) {
//                    Log.d("API Request successful", "Получили ${response.code()}")
//                    val dataResponse = response.body()
//                    println(dataResponse)
//                    if (dataResponse != null) {
//                        roleList = ArrayList(dataResponse.roles)
//                        univList = ArrayList(dataResponse.universityResponses)
//                        for (univ in univList) {
//                            univMap[univ.universityName] = univ
//                        }
//                        roleTextInputLayout.setAdapter(
//                            ArrayAdapter(
//                                requireContext(),
//                                R.layout.subj_item,
//                                dataResponse.roles
//                            )
//                        )
//                        univTextInputLayout.setAdapter(
//                            ArrayAdapter(
//                                requireContext(),
//                                R.layout.subj_item,
//                                univMap.keys.toTypedArray()
//                            )
//                        )
//                        val editable = arguments?.getBoolean("editable")
//                        if (editable != null && editable)
//                            setFieldsIfEdit(view)
//                    }
//                } else {
//                    println("Не успешно")
//                }
//            }
//
//            override fun onFailure(call: Call<CreateUserResponse>, t: Throwable) {
//                println("Ошибка")
//                println(t)
//            }
//        })
    }

    private fun validUserInfo(
        role: String,
        univId: Long?,
        facId: Long?
    ): Boolean {
        var isValid = true;
        if (role == "HEADMAN" || role == "LECTURER") {
            if (univId == null) {
                univTextInputLayout.error = "Выберите университет"
                showToastNotification("Пожалуйста, заполните все поля")
                isValid = false;
            }
            if (facId == null) {
                facultyTextInputLayout.error = "Выберите факультет"
                showToastNotification("Пожалуйста, заполните все поля")
                isValid = false
            }
        }
        return isValid
    }

    private fun setFieldsIfEdit(view: View) {
//        val role = arguments?.getString("role")

        val editable = arguments?.getBoolean("editable");
        if (editable != null && editable) {
            userViewModel.getUser(arguments?.getInt("id")!!.toLong())
            userViewModel.user.observe(viewLifecycleOwner) {
                val editableUser = it
                view.findViewById<EditText>(R.id.editTextTextEmailAddress)
                    .setText(editableUser.email)
                view.findViewById<EditText>(R.id.editLoginText).setText(editableUser.username)
                view.findViewById<EditText>(R.id.editUserCity).setText(editableUser.city)
                view.findViewById<EditText>(R.id.editTextTextPassword2)
                    .setText(editableUser.password)
                roleInputValid(view, editableUser.role)
                roleTextInputLayout.setText(editableUser.role)
                view.findViewById<EditText>(R.id.editFullNameText).setText(it.fullName)
                if (it.universityId != null && editableUser.universityId != -1L) {
                    val univ = univList.firstOrNull { it.id == editableUser.universityId }
                    val position = univList.indexOf(univ)
                    univTextInputLayout(position)
                    if (univ != null) {
                        univTextInputLayout.setText(univ.universityName, false)
                    }
                }
                if (editableUser.facultyId != null && editableUser.facultyId != -1L) {
                    val faculty = facultyList.firstOrNull { it.id == editableUser.facultyId };
                    val position = facultyList.indexOf(faculty)
                    val groupId = editableUser.groupId
                    facultyTextInputLayout(position, groupId)
                    if (faculty != null) {
                        facultyTextInputLayout.setText(faculty.name, false)
                    }
                }

                if (editableUser.groupId != null && editableUser.groupId != -1L) {
                    val group = groupList.firstOrNull { it.id == editableUser.groupId }
                    for ((key, mapValue) in groupMap) {
                        if (mapValue.id == group?.id) {
                            groupTextInputLayout.setText(key, false)
                        }
                    }
                }
            }
        }


//        val fullName = arguments?.getString("fullName")
//        if (fullName != null)
//            view.findViewById<EditText>(R.id.editFullNameText).setText(fullName)
//        val univId = arguments?.getLong("univId")
//        if (univId != null && univId != -1L) {
//            val univ = univList.firstOrNull { it.id == univId }
//            val position = univList.indexOf(univ)
//            univTextInputLayout(position)
//            if (univ != null) {
//                univTextInputLayout.setText(univ.universityName, false)
//            }
//        }
//        val facultyId = arguments?.getLong("facultyId")
//        if (facultyId != null && facultyId != -1L) {
//            val faculty = facultyList.firstOrNull { it.id == facultyId };
//            val position = facultyList.indexOf(faculty)
//            val groupId = arguments?.getLong("group")
//            facultyTextInputLayout(position, groupId)
//            if (faculty != null) {
//                facultyTextInputLayout.setText(faculty.name, false)
//            }
//        }
//        val groupId = arguments?.getLong("group")
//        if (groupId != null && groupId != -1L) {
//            val group = groupList.firstOrNull { it.id == groupId }
//            for ((key, mapValue) in groupMap) {
//                if (mapValue.id == group?.id) {
//                    groupTextInputLayout.setText(key, false)
//                }
//            }
//        }
    }

    private fun roleTextInputLayout(view: View) {
        roleTextInputLayout.setOnItemClickListener { _, _, position, _ ->
            roleInputValid(view, roleList[position])
            roleTextInputLayout.error = null
        }
    }

    private fun univTextInputLayout(position: Int) {
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
        facultyTextInputLayout.text = null
        groupTextInputLayout.text = null
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
            "Преподаватель" -> {
                view.findViewById<TextInputLayout>(R.id.editUnivText).visibility = View.VISIBLE
                view.findViewById<TextInputLayout>(R.id.editFacultyText).visibility = View.VISIBLE
                view.findViewById<TextInputLayout>(R.id.editGroupNumText).visibility =
                    View.GONE
                groupTextInputLayout.text = null
            }

            "Администратор" -> {
                view.findViewById<TextInputLayout>(R.id.editUnivText).visibility = View.GONE
                univTextInputLayout.text = null
                view.findViewById<TextInputLayout>(R.id.editFacultyText).visibility = View.GONE
                facultyTextInputLayout.text = null
                view.findViewById<TextInputLayout>(R.id.editGroupNumText).visibility =
                    View.GONE
                groupTextInputLayout.text = null
            }

            else -> {
                view.findViewById<TextInputLayout>(R.id.editUnivText).visibility = View.VISIBLE
                view.findViewById<TextInputLayout>(R.id.editFacultyText).visibility = View.VISIBLE
                view.findViewById<TextInputLayout>(R.id.editGroupNumText).visibility = View.VISIBLE
            }
        }
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