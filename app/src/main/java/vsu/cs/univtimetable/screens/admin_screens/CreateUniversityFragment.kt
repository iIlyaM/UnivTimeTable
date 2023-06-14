package vsu.cs.univtimetable.screens.admin_screens

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.navigation.fragment.findNavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vsu.cs.univtimetable.R
import vsu.cs.univtimetable.SessionManager
import vsu.cs.univtimetable.TimetableClient
import vsu.cs.univtimetable.api.UnivApi
import vsu.cs.univtimetable.dto.CreateUnivDto

class CreateUniversityFragment : Fragment() {

    private lateinit var univApi: UnivApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        univApi = TimetableClient.getClient().create(UnivApi::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_create_university, container, false)
        val view = inflater.inflate(R.layout.fragment_create_university, container, false)
        val confirmBtn = view.findViewById<AppCompatButton>(R.id.confirmAddUnivBtn)
        val univField = view.findViewById<EditText>(R.id.editUnivNameText)
        val city = view.findViewById<EditText>(R.id.editCityText)

        val prevPageButton = view.findViewById<ImageButton>(R.id.prevPageButton)
        prevPageButton.setOnClickListener {
            findNavController().navigate(R.id.action_createUniversityFragment_to_univListPageFragment)
        }

        val mainPageButton = view.findViewById<ImageButton>(R.id.mainPageButton)
        mainPageButton.setOnClickListener {
            findNavController().navigate(R.id.action_createUniversityFragment_to_adminMainPageFragment)
        }

        confirmBtn.setOnClickListener {
            addUniversity(univField, city)
        }
        return view
    }

    private fun addUniversity(univField: EditText, city: EditText) {
        val univName: String = univField.text.toString()
        val cityName:String = city.text.toString()

        val token: String? = SessionManager.getToken(requireContext())
        Log.d("API Request failed", "${token}")

        val call = univApi.addUniversity(
            "Bearer ${token}",
            CreateUnivDto(univName, cityName)
        )

        call.enqueue(object : Callback<Void> {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                if (response.isSuccessful) {
                    Log.d("API Request Successful", "${response.code()}")
                    showToastNotification("Университет успешно добавлен")
                } else {
                    if (response.code() == 400) {
                        showToastNotification("Такой университет уже существует")
                    }
                    if (response.code() == 403) {
                        showToastNotification("Недостаточно прав доступа для выполнения")
                    }
                    println("Не успешно")
//                    showDialog(univName, response.code())
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                println("Ошибка")
                println(t)
            }
        })
        univField.text.clear()
        city.text.clear()
    }

//    private fun showDialog(univName: String, code: Int) {
//        val builder = AlertDialog.Builder(requireContext())
//        if (code == 201) {
//            builder.setMessage("${univName} добавлен")
//            val alert = builder.create()
//            alert.show()
//            alert.window?.setGravity(Gravity.BOTTOM)
//
//            Handler().postDelayed({
//                alert.dismiss()
//            }, 2000)
//        }
//        if(code == 400) {
//            builder.setMessage("${univName} уже есть в списке")
//            val alert = builder.create()
//            alert.show()
//            alert.window?.setGravity(Gravity.BOTTOM)
//
//            Handler().postDelayed({
//                alert.dismiss()
//            }, 2000)
//        }
//    }

    private fun showToastNotification (message: String) {
        val duration = Toast.LENGTH_LONG

        val toast = Toast.makeText(requireContext(), message, duration)
        toast.show()
        val handler = Handler()
        handler.postDelayed({ toast.cancel() }, 1500)
    }

}