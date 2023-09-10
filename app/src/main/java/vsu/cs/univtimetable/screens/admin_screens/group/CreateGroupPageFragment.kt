package vsu.cs.univtimetable.screens.admin_screens.group

import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.github.leandroborgesferreira.loadingbutton.customViews.CircularProgressButton
import com.google.android.material.textfield.TextInputLayout
import vsu.cs.univtimetable.R
import vsu.cs.univtimetable.utils.token_utils.SessionManager
import vsu.cs.univtimetable.TimetableClient
import vsu.cs.univtimetable.api.GroupApi
import vsu.cs.univtimetable.api.UserApi
import vsu.cs.univtimetable.dto.group.GroupDto
import vsu.cs.univtimetable.dto.user.UserCreateRequest
import vsu.cs.univtimetable.dto.user.UserDisplayDto
import vsu.cs.univtimetable.repository.GroupRepository
import vsu.cs.univtimetable.repository.UserRepository
import vsu.cs.univtimetable.screens.admin_screens.univ.UnivViewModelFactory
import vsu.cs.univtimetable.screens.admin_screens.users.UserViewModel
import vsu.cs.univtimetable.utils.Status


class CreateGroupPageFragment : Fragment() {

    private lateinit var groupApi: GroupApi
    private lateinit var userApi: UserApi
    private lateinit var searchView: SearchView
    private lateinit var groupViewModel: GroupViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var confirmBtn: CircularProgressButton
    private lateinit var pDialog: ProgressDialog

    private var headmans: MutableSet<String> = mutableSetOf()
    private val coursesList =
        listOf("1к. бак.", "2к. бак.", "3к. бак.", "4к. бак.", "1к. маг.", "2к. маг.")
    private val courseNums = listOf(1, 2, 3, 4, 5, 6)

    private val courseMap: Map<String, Int> = coursesList.zip(courseNums).toMap()

    private var headmanMap: MutableMap<String, UserDisplayDto> = mutableMapOf()
    private var headmanList: ArrayList<UserDisplayDto> = arrayListOf()

    private lateinit var possibleHeadman: UserCreateRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        groupApi = TimetableClient.getClient().create(GroupApi::class.java)
        userApi = TimetableClient.getClient().create(UserApi::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_group_page, container, false)

        val token = SessionManager.getToken(requireContext())!!
        val groupRepository = GroupRepository(groupApi, token)
        val userRepository = UserRepository(userApi, token)

        groupViewModel =
            ViewModelProvider(
                requireActivity(),
                UnivViewModelFactory(groupRepository, token)
            )[GroupViewModel::class.java]

        userViewModel =
            ViewModelProvider(
                requireActivity(),
                UnivViewModelFactory(userRepository, token)
            )[UserViewModel::class.java]

        val courseTextInputLayout =
            view.findViewById<TextInputLayout>(R.id.editCourseText)
        courseTextInputLayout.boxStrokeColor =
            ContextCompat.getColor(requireContext(), R.color.adminsColor)
        courseTextInputLayout.setBoxStrokeColorStateList(
            ContextCompat.getColorStateList(
                requireContext(),
                R.color.admin_selector
            )!!
        )
        courseTextInputLayout.boxStrokeWidth =
            resources.getDimensionPixelSize(R.dimen.new_stroke_width)


        val prevPageButton = view.findViewById<ImageButton>(R.id.prevPageButton)
        prevPageButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("facultyId", getFacultyId())
            bundle.putInt("univId", arguments?.getInt("univId")!!)
            findNavController().navigate(
                R.id.action_createGroupPageFragment_to_groupListPageFragment,
                bundle
            )
        }

        val mainPageButton = view.findViewById<ImageButton>(R.id.mainPageButton)
        mainPageButton.setOnClickListener {
            findNavController().navigate(R.id.action_createAudiencePageFragment_to_adminMainPageFragment)
        }

        val courseTypeCompleteView =
            view.findViewById<AutoCompleteTextView>(R.id.editCourseAutoCompleteText)
        val courseAdapter = ArrayAdapter(requireContext(), R.layout.subj_item, coursesList)
        courseTypeCompleteView.setAdapter(courseAdapter)

        val headmanTextInputLayout =
            view.findViewById<TextInputLayout>(R.id.editHeadmanText)
        headmanTextInputLayout.boxStrokeColor =
            ContextCompat.getColor(requireContext(), R.color.adminsColor)
        headmanTextInputLayout.setBoxStrokeColorStateList(
            ContextCompat.getColorStateList(
                requireContext(),
                R.color.admin_selector
            )!!
        )
        headmanTextInputLayout.boxStrokeWidth =
            resources.getDimensionPixelSize(R.dimen.new_stroke_width)

        pDialog = ProgressDialog(context)

        val headmenTypeCompleteView =
            view.findViewById<AutoCompleteTextView>(R.id.editHeadmanAutoCompleteText)

        val headmenAdapter =
            ArrayAdapter(requireContext(), R.layout.subj_item, headmans.toTypedArray())
        headmenTypeCompleteView.setAdapter(headmenAdapter)


        val groupNumField = view.findViewById<EditText>(R.id.editGroupText)
        val studAmountField = view.findViewById<EditText>(R.id.editAmountText)

        setFieldsIfEdit(
            courseTypeCompleteView,
            groupNumField,
            headmenTypeCompleteView,
            studAmountField
        )

        confirmBtn = view.findViewById(R.id.confirmCreateGroupBtn)
        confirmBtn.setOnClickListener {
            confirmBtn.startAnimation()
            addGroup(
                courseTypeCompleteView,
                headmenTypeCompleteView,
                groupNumField,
                studAmountField
            )
        }
        headmanList.add(
            UserDisplayDto(
                -1,
                "",
                "Нет старосты",
                "",
                "",
                "",
                -1
            )
        )

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHeadmen(view.findViewById(R.id.editHeadmanAutoCompleteText))
        groupViewModel.errorMsg.observe(viewLifecycleOwner) {
        }
    }

    private fun addGroup(
        courses: AutoCompleteTextView,
        headmanView: AutoCompleteTextView,
        groupNumField: EditText,
        amountField: EditText
    ) {
        if (courses.text.isEmpty()) {
            courses.error = "Выберите номер курса"
            stopAnimation(confirmBtn)
            return
        }
        if (headmanView.text.isEmpty()) {
            headmanView.error = "Выберите старосту"
            stopAnimation(confirmBtn)
            return
        }
        if (groupNumField.text.isEmpty() || groupNumField.text.toString()
                .filter { it.isDigit() }.length > 1 || groupNumField.text.toString()
                .filter { it.isDigit() }
                .toLong() < 0
        ) {
            groupNumField.error = "Введите корректный номер группы"
            stopAnimation(confirmBtn)
            return
        }
        if (groupNumField.text.toString().filter { it.isDigit() }.toLong() >= 10) {
            groupNumField.error = "Номер группы должен быть меньше 10"
            stopAnimation(confirmBtn)
            return
        }
        if (amountField.text.isEmpty() || amountField.text.toString()
                .filter { it.isDigit() }.length > 2 ||amountField.text.toString().filter { it.isDigit() }
                .toLong().toInt() < 0 ||
            amountField.text.toString().toInt() > 100
        ) {
            amountField.error = "Введите корректную численность группы"
            stopAnimation(confirmBtn)
            return
        }


        val groupNum: String = groupNumField.text.toString()
        val amount: String = amountField.text.toString()
        var course = 0
        var courseItem = courseMap[courses.text.toString()]
        if (courseItem != null) {
            course = courseItem
        }
        var headmanItem = headmanMap[headmanView.text.toString()]
        var headmen: UserDisplayDto? = null
        if (headmanItem != null && headmanItem.fullName != "Нет старосты") {
            headmen = headmanItem
        }


        val editable = arguments?.getBoolean("editable");
        if (editable != null && editable) {
            val id = arguments?.getLong("id") ?: -1
            groupViewModel.editGroup(
                id,
                getFacultyId(),
                GroupDto(id, groupNum.toInt(), course, amount.toInt(), headmen)
            ).observe(viewLifecycleOwner) {
                it?.let {
                    when (it.status) {
                        Status.SUCCESS -> {
                            stopAnimation(confirmBtn)
                            showToastNotification("Информация о группе изменена")
                        }

                        Status.ERROR -> {
                            showToastNotification(it.message.toString())
                            stopAnimation(confirmBtn)
                        }

                        Status.LOADING -> {}
                    }
                }
            }
        } else {
            groupViewModel.addGroup(
                getFacultyId(),
                GroupDto(null, groupNum.toInt(), course, amount.toInt(), headmen)
            ).observe(viewLifecycleOwner) {
                it?.let {
                    when (it.status) {
                        Status.SUCCESS -> {
                            groupNumField.text.clear()
                            amountField.text.clear()
                            courses.text.clear()
                            headmanView.text.clear()
                            val bundle = Bundle()
                            bundle.putInt("facultyId", getFacultyId())
                            showToastNotification("Группа № ${groupNum.toInt()} курс ${course} добавлена")
                            findNavController().navigate(
                                R.id.action_createGroupPageFragment_to_groupListPageFragment,
                                bundle
                            )
                            stopAnimation(confirmBtn)
                        }

                        Status.ERROR -> {
                            stopAnimation(confirmBtn)
                        }

                        Status.LOADING -> {

                        }
                    }
                }
            }
            stopAnimation(confirmBtn)
        }
    }


    private fun setHeadmen(headmanView: AutoCompleteTextView) {
        userViewModel.getFreeHeadmen(getFacultyId()).observe(viewLifecycleOwner) {
            it?.let {
                when (it.status) {
                    Status.SUCCESS -> {
                        if (it.data != null) {
                            headmanList.addAll(it.data)
                        }
                        for (headman in headmanList) {
                            headmanMap[headman.fullName] = headman
                            headmans.add(headman.fullName)
                        }
                        val headmenAdapter =
                            ArrayAdapter(
                                requireContext(),
                                R.layout.subj_item,
                                headmans.toTypedArray()
                            )
                        headmanView.setAdapter(headmenAdapter)
                    }

                    Status.ERROR -> {
                        showToastNotification(it.message.toString())
                    }

                    Status.LOADING -> {}
                }
            }
        }
    }

    private fun getFacultyId(): Int {
        val id = arguments?.getInt("facultyId")
        var facultyId: Int = 0
        if (id != null) {
            facultyId = id
        }
        return facultyId
    }

    private fun setFieldsIfEdit(
        course: AutoCompleteTextView,
        group: EditText,
        headman: AutoCompleteTextView,
        studentsAmount: EditText
    ) {
        val editable = arguments?.getBoolean("editable")
        if (editable != null && editable) {
            groupViewModel.getGroup(arguments?.getLong("id")!!).observe(viewLifecycleOwner) {
                it?.let {
                    when (it.status) {
                        Status.SUCCESS -> {
                            pDialog.dismiss()
                            for (entry in courseMap) {
                                if (entry.value == it.data!!.courseNumber) {
                                    course.setText(entry.key)
                                    val courseAdapter =
                                        ArrayAdapter(
                                            requireContext(),
                                            R.layout.subj_item,
                                            coursesList
                                        )
                                    course.setAdapter(courseAdapter)
                                    break
                                }
                            }
                            group.setText(it.data!!.groupNumber.toString())
                            studentsAmount.setText(it.data.studentsAmount.toString())
                            setUser(it.data.headman?.id?.toLong() ?: -1L, headman)
                        }

                        Status.ERROR -> {
                            pDialog.dismiss()
                            showToastNotification(it.message.toString())
                        }

                        Status.LOADING -> {
                            setLoadingDialog()
                        }
                    }
                }
            }
        }
    }

    private fun setUser(headmanId: Long, headman: AutoCompleteTextView) {
        userViewModel.getUser(headmanId).observe(viewLifecycleOwner) {
            it?.let {
                when (it.status) {
                    Status.SUCCESS -> {
                        pDialog.dismiss()
                        headman.setText(it.data!!.fullName)
                        headmanList.add(
                            UserDisplayDto(
                                it.data.id,
                                it.data.role,
                                it.data.fullName,
                                it.data.city,
                                "",
                                "",
                                id.toInt() ?: -1
                            )
                        )
                        for (head in headmanList) {
                            headmanMap[head.fullName] = head
                            headmans.add(head.fullName)
                        }
                        val headmenAdapter =
                            ArrayAdapter(
                                requireContext(),
                                R.layout.subj_item,
                                headmans.toTypedArray()
                            )
                        headman.setAdapter(headmenAdapter)
                    }

                    Status.ERROR -> {
                        pDialog.dismiss()
                        if (it.message.toString() != "Пользователь по переданному id не был найден") {
                            showToastNotification(it.message.toString())
                        } else {
                            showToastNotification("Староста не установлен")
                        }
                    }

                    Status.LOADING -> {
                        setLoadingDialog()
                    }
                }
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

    private fun setLoadingDialog() {
        pDialog.setMessage("Загрузка...пожалуйста подождите")
        pDialog.setCancelable(false)
        pDialog.show()
    }
}