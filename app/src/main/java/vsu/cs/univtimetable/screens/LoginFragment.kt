package vsu.cs.univtimetable.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import androidx.navigation.fragment.findNavController
import com.auth0.android.jwt.JWT
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vsu.cs.univtimetable.R
import vsu.cs.univtimetable.SessionManager
import vsu.cs.univtimetable.TimeTableClient
import vsu.cs.univtimetable.UserAuthApi
import vsu.cs.univtimetable.dto.AuthRequestDto
import vsu.cs.univtimetable.dto.AuthResponseDto


class LoginFragment : Fragment() {

    private lateinit var authApi: UserAuthApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authApi = TimeTableClient.getClient().create(UserAuthApi::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        val button = view.findViewById<AppCompatButton>(R.id.login_btn)


        button.setOnClickListener {
            val emailField = view.findViewById<EditText>(R.id.editTextTextEmail).text.toString()
            val pwd = view.findViewById<EditText>(R.id.editTextTextPassword).text.toString()
            login(emailField, pwd)
        }

        return view
    }

    private fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            // Обработка ошибки ввода данных
            return
        }

        val call = authApi.login(AuthRequestDto(email, password))
        call.enqueue(object : Callback<AuthResponseDto> {
            override fun onResponse(
                call: Call<AuthResponseDto>,
                response: Response<AuthResponseDto>
            ) {
                if (response.isSuccessful) {
                    val token = response.body()?.token
                    SessionManager.saveAuthToken(requireContext(), token!!)
                    val decodedToken = SessionManager.decodeToken(token)
                    navigateTo(decodedToken)
                    // Переход на другой экран при успешной авторизации
                } else {
                    // Обработка ошибки авторизации
                }
            }

            override fun onFailure(call: Call<AuthResponseDto>, t: Throwable) {
                // Обработка ошибки сети
            }
        })
    }

    private fun navigateTo(decodedToken: String) {
        if(decodedToken.contains("CREATE_USER", ignoreCase = true)) {
            findNavController().navigate(R.id.action_loginFragment_to_adminMainPageFragment2)
            return
        }
        if(decodedToken.contains("MOVE_CLASS", ignoreCase = true)) {
            findNavController().navigate(R.id.action_loginFragment_to_lecturerMainPageFragment2)
            return
        }
        if(decodedToken.contains("", ignoreCase = true)) {
            findNavController().navigate(R.id.action_loginFragment_to_headmanMainPageFragment2)
            return
        }
    }

}

