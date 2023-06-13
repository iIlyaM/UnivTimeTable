package vsu.cs.univtimetable.screens

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.widget.AppCompatButton
import androidx.navigation.fragment.findNavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vsu.cs.univtimetable.R
import vsu.cs.univtimetable.SessionManager
import vsu.cs.univtimetable.TimetableClient
import vsu.cs.univtimetable.api.FacultyApi
import vsu.cs.univtimetable.dto.CreateFacultyDto

class CreateFacultyPageFragment : Fragment() {

    private lateinit var facultyApi: FacultyApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        facultyApi = TimetableClient.getClient().create(FacultyApi::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        return inflater.inflate(R.layout.fragment_create_faculty_page, container, false)
        val view = inflater.inflate(R.layout.fragment_create_faculty_page, container, false)
        val facultyNameField = view.findViewById<EditText>(R.id.editFacultyNameText)
        val confirmBtn = view.findViewById<AppCompatButton>(R.id.confirmFacultyCreateBtn)

        val prevPageButton = view.findViewById<ImageButton>(R.id.prevPageButton)
        prevPageButton.setOnClickListener {
            findNavController().navigate(R.id.action_facultyListPageFragment_to_univListPageFragment)
        }

        val mainPageButton = view.findViewById<ImageButton>(R.id.mainPageButton)
        mainPageButton.setOnClickListener {
            findNavController().navigate(R.id.action_facultyListPageFragment_to_adminMainPageFragment)
        }

        confirmBtn.setOnClickListener {
            addFaculty(facultyNameField)
        }
        return view
    }

    private fun addFaculty(facultyField: EditText) {
        val name: String = facultyField.text.toString()

        val token: String? = SessionManager.getToken(requireContext())
        Log.d("API Request failed", "${token}")

        val id = arguments?.getInt("univId")

        if (id != null) {
            val call = facultyApi.addFaculty(
                "Bearer ${token}",
                id,
                CreateFacultyDto(name)
            )

            call.enqueue(object : Callback<Void> {
                override fun onResponse(
                    call: Call<Void>,
                    response: Response<Void>
                ) {
                    if (response.isSuccessful) {
                        Log.d("API Request Successful", "${response.code()}")
                        showDialog(name, response.code())
                    } else {
                        println("Не успешно")
                        showDialog(name, response.code())
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    println("Ошибка")
                    println(t)
                }
            })
            facultyField.text.clear()
        }


    }

    private fun showDialog(facultyName: String, code: Int) {
        val builder = AlertDialog.Builder(requireContext())
        if (code == 201) {
            builder.setMessage("${facultyName} добавлен")
            val alert = builder.create()
            alert.show()
            alert.window?.setGravity(Gravity.BOTTOM)

            Handler().postDelayed({
                alert.dismiss()
            }, 2000)
        }
        if (code == 400) {
            builder.setMessage("${facultyName} уже есть в списке")
            val alert = builder.create()
            alert.show()
            alert.window?.setGravity(Gravity.BOTTOM)

            Handler().postDelayed({
                alert.dismiss()
            }, 2000)
        }
    }


}