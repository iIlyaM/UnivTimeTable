package vsu.cs.univtimetable.screens.headman_screens

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.navigation.fragment.findNavController
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
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream


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
            download()
        }

        val headmanLogoutBtn = view.findViewById<ImageButton>(R.id.headmanLogoutBtn)
        headmanLogoutBtn.setOnClickListener {
            SessionManager.clearData(requireContext())
            findNavController().navigate(R.id.action_headmanMainPageFragment_to_loginFragment)
        }
        return view
    }

    private fun download() {
        val token: String? = SessionManager.getToken(requireContext())
        Log.d("API Request failed", "${token}")
        val call = timetableApi.downloadFile("Bearer ${token}")

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val fileName = "example.xls"
                    val file = File(context!!.getExternalFilesDir(null), fileName)

                    var inputStream: InputStream? = null
                    var outputStream: OutputStream? = null

                    try {
                        val fileReader = ByteArray(4096)
                        inputStream = response.body()?.byteStream()
                        outputStream = FileOutputStream(file)

                        while (true) {
                            val read = inputStream?.read(fileReader)

                            if (read == null || read == -1) {
                                break
                            }

                            outputStream.write(fileReader, 0, read)
                        }

                        outputStream.flush()

                        Toast.makeText(context, "Файл успешно загружен", Toast.LENGTH_SHORT).show()
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(context, "Ошибка загрузки файла", Toast.LENGTH_SHORT).show()
                    } finally {
                        inputStream?.close()
                        outputStream?.close()
                    }
                } else {
                    Toast.makeText(context, "Ошибка загрузки файла", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
                Toast.makeText(context, "Ошибка загрузки файла", Toast.LENGTH_SHORT).show()
            }
        })
    }


}