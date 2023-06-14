package vsu.cs.univtimetable.screens

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vsu.cs.univtimetable.R
import vsu.cs.univtimetable.SessionManager
import vsu.cs.univtimetable.TimetableClient
import vsu.cs.univtimetable.api.GroupApi
import vsu.cs.univtimetable.api.UserApi
import vsu.cs.univtimetable.dto.GroupDto
import vsu.cs.univtimetable.dto.UserCreateRequest
import vsu.cs.univtimetable.dto.UserDisplayDto
import vsu.cs.univtimetable.dto.UserResponse


class CreateGroupPageFragment : Fragment() {

    private lateinit var groupApi: GroupApi
    private lateinit var userApi: UserApi
    private lateinit var searchView: SearchView

    private var headmans: ArrayList<String> = arrayListOf()
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
        val courseTextInputLayout =
            view.findViewById<TextInputLayout>(R.id.editCourseText)
        courseTextInputLayout.boxStrokeColor =
            ContextCompat.getColor(requireContext(), R.color.adminsColor)

        val prevPageButton = view.findViewById<ImageButton>(R.id.prevPageButton)
        prevPageButton.setOnClickListener {
            findNavController().navigate(R.id.action_createGroupPageFragment_to_groupListPageFragment)
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

        val headmenTypeCompleteView =
            view.findViewById<AutoCompleteTextView>(R.id.editHeadmanAutoCompleteText)

        val headmenAdapter = ArrayAdapter(requireContext(), R.layout.subj_item, headmans)
        headmenTypeCompleteView.setAdapter(headmenAdapter)


        val groupNumField = view.findViewById<EditText>(R.id.editGroupText)
        val studAmountField = view.findViewById<EditText>(R.id.editAmountText)

        setFieldsIfEdit(
            courseTypeCompleteView,
            groupNumField,
            headmenTypeCompleteView,
            studAmountField
        )

        val confirmBtn = view.findViewById<AppCompatButton>(R.id.confirmCreateGroupBtn)
        confirmBtn.setOnClickListener {
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
        setHeadmen(view.findViewById<AutoCompleteTextView>(R.id.editHeadmanAutoCompleteText))
    }

    private fun addGroup(
        courses: AutoCompleteTextView,
        headmanView: AutoCompleteTextView,
        groupNumField: EditText,
        amountField: EditText
    ) {
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

        val token: String? = SessionManager.getToken(requireContext())

        val editable = arguments?.getBoolean("editable");
        val call: Call<Void>;
        if (editable != null && editable) {
            val id = arguments?.getLong("id") ?: -1
            call = groupApi.editGroup(
                "Bearer ${token}",
                id,
                GroupDto(id, groupNum.toInt(), course, amount.toInt(), headmen)
            )
        } else {
            call = groupApi.addGroup(
                "Bearer ${token}",
                getFacultyId(),
                GroupDto(null, groupNum.toInt(), course, amount.toInt(), headmen)
            )
        }

        call.enqueue(object : Callback<Void> {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                if (response.isSuccessful) {
                    Log.d("API Request Successful", "${response.code()}")

                    findNavController().navigate(R.id.action_createGroupPageFragment_to_groupListPageFragment)
                    showToastNotification("Группа успешно создана")
                } else {
                    println("Не успешно, ошибка = ${response.code()}")
                    if (response.code() == 400) {
                        showToastNotification("Такая группа на этом факультете уже существует")
                    }
                    if (response.code() == 403) {
                        showToastNotification("Недостаточно прав доступа для выполнения")
                    }
                    if (response.code() == 404) {
                        showToastNotification("Cтароста для группы по переданному id не был найден")
                    }
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                println("Ошибка")
                println(t)
            }
        })
        groupNumField.text.clear()
        amountField.text.clear()
        courses.text.clear()
        headmanView.text.clear()
    }


    private fun setHeadmen(headmanView: AutoCompleteTextView) {
        val token: String? = SessionManager.getToken(requireContext())

        Log.d("API Request failed", "${token}")

        val call = userApi.getFreeHeadmen("Bearer ${token}", getFacultyId())

        call.enqueue(object : Callback<List<UserDisplayDto>> {
            override fun onResponse(
                call: Call<List<UserDisplayDto>>,
                response: Response<List<UserDisplayDto>>
            ) {
                //TODO: showToastNotification
                if (response.isSuccessful) {
                    Log.d("API Request successful", "Получили ${response.code()}")
                    val dataResponse = response.body()
                    println(dataResponse)
                    if (dataResponse != null) {
                        headmanList.addAll(dataResponse)
                    }
                    for (headman in headmanList) {
                        headmanMap[headman.fullName] = headman
                        headmans.add(headman.fullName)
                    }
                    val headmenAdapter =
                        ArrayAdapter(requireContext(), R.layout.subj_item, headmans)
                    headmanView.setAdapter(headmenAdapter)
                } else {
                    showToastNotification("Не получилось назначить старосту группы")
                    println("Не успешно")
                }

            }

            override fun onFailure(call: Call<List<UserDisplayDto>>, t: Throwable) {
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

    private fun setFieldsIfEdit(
        course: AutoCompleteTextView,
        group: EditText,
        headman: AutoCompleteTextView,
        studentsAmount: EditText
    ) {
        val editable = arguments?.getBoolean("editable");
        if (editable != null && editable) {
            val courseNumber = arguments?.getInt("courseNumber")
            for (entry in courseMap) {
                if (entry.value == courseNumber) {
                    course.setText(entry.key)
                    val courseAdapter =
                        ArrayAdapter(requireContext(), R.layout.subj_item, coursesList)
                    course.setAdapter(courseAdapter)
                    break;
                }
            }
            group.setText(arguments?.getInt("groupNumber").toString())
            studentsAmount.setText(arguments?.getInt("studentsAmount").toString())
            val headmanId = arguments?.getInt("headmanId")
            if (headmanId != null && headmanId != -1) {
                getUser(headmanId, headman)
            }
        }
    }

    private fun getUser(headmanId: Int, headman: AutoCompleteTextView) {
        val token: String? = SessionManager.getToken(requireContext())
        val call = userApi.getUser("Bearer ${token}", headmanId.toLong())
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
                        possibleHeadman = dataResponse
                        headmanList.add(
                            UserDisplayDto(
                                dataResponse.id,
                                dataResponse.role,
                                dataResponse.fullName,
                                dataResponse.city,
                                "",
                                "",
                                dataResponse.groupId?.toInt() ?: -1
                            )
                        )
                        headman.setText(dataResponse.fullName)
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

    private fun showToastNotification(message: String) {
        val duration = Toast.LENGTH_LONG

        val toast = Toast.makeText(requireContext(), message, duration)
        toast.show()
        val handler = Handler()
        handler.postDelayed({ toast.cancel() }, 1500)
    }
}