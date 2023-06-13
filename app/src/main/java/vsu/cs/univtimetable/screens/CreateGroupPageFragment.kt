package vsu.cs.univtimetable.screens

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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
import vsu.cs.univtimetable.dto.UserDisplayDto
import vsu.cs.univtimetable.dto.UserResponse


class CreateGroupPageFragment : Fragment() {

    private lateinit var groupApi: GroupApi
    private lateinit var userApi: UserApi
    private lateinit var searchView: SearchView

    private var headmans: ArrayList<String> = arrayListOf()
    private lateinit var courseMap: Map<String, Int>
    private val coursesList =
        listOf("1к. бак.", "2к. бак.", "3к. бак.", "4к. бак.", "1к. маг.", "2к. маг.")
    private val courseNums = listOf(1, 2, 3, 4, 5, 6)

    private var headmanMap: MutableMap<String, UserDisplayDto> = mutableMapOf()
    private var headmanList: List<UserDisplayDto> = listOf()

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


        val confirmBtn = view.findViewById<AppCompatButton>(R.id.confirmCreateGroupBtn)
        confirmBtn.setOnClickListener {
            addGroup(courseTypeCompleteView, headmenTypeCompleteView, groupNumField, studAmountField)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHeadmen()
        courseMap = coursesList.zip(courseNums).toMap()
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
        if (headmanItem != null) {
            headmen = headmanItem
        }

        val token: String? = SessionManager.getToken(requireContext())
        Log.d("API Request failed", "${token}")

        val call = groupApi.addGroup(
            "Bearer ${token}",
            getFacultyId(),
            GroupDto(null, groupNum.toInt(), course, amount.toInt(), headmen)
        )

        call.enqueue(object : Callback<Void> {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                if (response.isSuccessful) {
                    Log.d("API Request Successful", "${response.code()}")
                } else {
                    println("Не успешно, ошибка = ${response.code()}")
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


    private fun setHeadmen() {
        val token: String? = SessionManager.getToken(requireContext())

        Log.d("API Request failed", "${token}")

        val call = userApi.getFreeHeadmen("Bearer ${token}", getFacultyId())

        call.enqueue(object : Callback<List<UserDisplayDto>> {
            override fun onResponse(
                call: Call<List<UserDisplayDto>>,
                response: Response<List<UserDisplayDto>>
            ) {
                if (response.isSuccessful) {
                    Log.d("API Request successful", "Получили ${response.code()}")
                    val dataResponse = response.body()
                    println(dataResponse)
                    if (dataResponse != null) {
                        headmanList = dataResponse.toList()
                    }
                    for (headman in headmanList) {
                        headmanMap[headman.fullName] = headman
                        headmans.add(headman.fullName)
                    }
                } else {
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
}