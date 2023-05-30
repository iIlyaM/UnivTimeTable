package vsu.cs.univtimetable.screens

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import androidx.core.widget.addTextChangedListener
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

        val emailEditText = view?.findViewById<EditText>(R.id.editTextTextEmail)
        val button = view?.findViewById<AppCompatButton>(R.id.login_btn)


        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

        emailEditText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
//                if (!s.toString().matches(emailPattern.toRegex())) {
//                    emailEditText.error = "Некорректный email"
//                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(Patterns.EMAIL_ADDRESS.matcher(emailEditText.text.toString()).matches()) {
                    button?.isEnabled = true
                } else {
                    button?.isEnabled = false
                    emailEditText.error = "Почта введена некорректно"
                }
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        val button = view.findViewById<AppCompatButton>(R.id.login_btn)
        val emailField = view.findViewById<EditText>(R.id.editTextTextEmail)
        val pwd = view.findViewById<EditText>(R.id.editTextTextPassword)


//        validate(emailField, pwd, button)
        button.setOnClickListener {
//            val emailField = view.findViewById<EditText>(R.id.editTextTextEmail).text.toString()
//            val pwd = view.findViewById<EditText>(R.id.editTextTextPassword).text.toString()
            login(emailField, pwd)
        }

        return view
    }

    private fun login(email: EditText, password: EditText) {
        if(email.text.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()) {
            email.error = "Почта введена некорректно"
            return
        }
        if (password.text.isEmpty()) {
            password.error = "Вы должны ввести пароль"
            return
        }

        val call = authApi.login(AuthRequestDto(email.text.toString(), password.text.toString()))
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

    private fun validate(emailField: EditText, pwdField: EditText, button: AppCompatButton) {
        if(emailField.text.isEmpty() && !(Patterns.EMAIL_ADDRESS.matcher(emailField.text.toString()).matches())) {
            emailField.error = "Почта введена некорректно"
            button.isEnabled = false
        } else {
            button.isEnabled = true
        }

        if (pwdField.text.isEmpty()) {
            pwdField.error = "Вы должны ввести пароль"
            button.isEnabled = false
        } else {
            button.isEnabled = true
        }
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