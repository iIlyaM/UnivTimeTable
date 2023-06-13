package vsu.cs.univtimetable.screens

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.yandex.metrica.YandexMetrica
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vsu.cs.univtimetable.R
import vsu.cs.univtimetable.SessionManager
import vsu.cs.univtimetable.TimetableClient
import vsu.cs.univtimetable.api.TimetableApi
import java.io.File
import java.io.FileOutputStream


class HeadmanMainPageFragment : Fragment() {

    private lateinit var timetableApi: TimetableApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        timetableApi = TimetableClient.getClient().create(TimetableApi::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_headman_main_page, container, false)
        val button = view.findViewById<AppCompatButton>(R.id.lookTimeTableBtn)
        button.setOnClickListener {
            findNavController().navigate(R.id.action_headmanMainPageFragment_to_studentTimeTablePageFragment)
        }

        val saveFilebtn = view.findViewById<AppCompatButton>(R.id.saveTimeTableBtn)
        saveFilebtn.setOnClickListener {
            downloadTimetable()
        }

        YandexMetrica.reportEvent("Главная страница пользователя")
        return view
    }

    private fun downloadTimetable() {
        val token: String? = SessionManager.getToken(requireContext())
        Log.d("API Request failed", "${token}")
        val call = timetableApi.downloadFile("Bearer ${token}")

        call.enqueue(object : Callback<ResponseBody> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    val file = File(context!!.getExternalFilesDir(null), "example.xlsx")
                    val inputStream = response.body()?.byteStream()
                    val outputStream = FileOutputStream(file)


                    inputStream?.use { input ->
                        outputStream.use { output ->
                            input.copyTo(output)
                        }
                    }

                    YandexMetrica.reportEvent("Скачивание расписания с главной страницы")

                    showToastNotification()
                } else {
                    if (response.code() == 400) {
                        showDialog()
                    }
                    Log.d("ошибка", "Получили ошибку - ${response.code()}")
                    Log.d("ошибка", "с ошибкой пришло - ${response.body()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                println("Ошибка")
                println(t)
            }
        })
    }

    private fun showDialog() {
        val builder = AlertDialog.Builder(requireContext())

        builder.setMessage("Расписание ещё не сформировано")
        val alert = builder.create()
        alert.show()
        alert.window?.setGravity(Gravity.BOTTOM)

        Handler().postDelayed({
            alert.dismiss()
        }, 2000)
        findNavController().navigate(R.id.action_lecturerTimetablePageFragment_to_lecturerMainPageFragment)

    }

    private fun showToastNotification() {
        val duration = Toast.LENGTH_LONG

        val toast = Toast.makeText(requireContext(), "Файл сохранён", duration)
        toast.show()
        val handler = Handler()
        handler.postDelayed({ toast.cancel() }, 500)
    }

}