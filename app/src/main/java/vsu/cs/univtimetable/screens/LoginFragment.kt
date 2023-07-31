package vsu.cs.univtimetable.screens

import android.os.Bundle
import android.os.Handler
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vsu.cs.univtimetable.BtnLoadingProgressbar
import vsu.cs.univtimetable.R
import vsu.cs.univtimetable.SessionManager
import vsu.cs.univtimetable.TimetableClient
import vsu.cs.univtimetable.api.UserAuthApi
import vsu.cs.univtimetable.dto.AuthRequestDto
import vsu.cs.univtimetable.dto.AuthResponseDto
import vsu.cs.univtimetable.utils.NavigationManager


class LoginFragment : Fragment() {

    private lateinit var authApi: UserAuthApi
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authApi = TimetableClient.getClient().create(UserAuthApi::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        val button = view.findViewById<LinearLayout>(R.id.login_btn)
        val emailField = view.findViewById<EditText>(R.id.editTextTextEmail)
        val pwd = view.findViewById<EditText>(R.id.editTextTextPassword)
        val navController = findNavController()

        button.setOnClickListener {
            val progressbar = BtnLoadingProgressbar(it)
            progressbar.setLoading()
            login(emailField, pwd, navController, progressbar)
//            val progressbar = BtnLoadingProgressbar(it) // `it` is view of button
//            progressbar.setLoading()
//            handler.postDelayed({
//                progressbar.setState(true) { // executed after animation end
//                    handler.postDelayed({
//                        startError(progressbar)
//                    }, 1500)
//                }
//            }, 2000)
        }
        return view
    }

    private fun login(
        email: EditText,
        password: EditText,
        navController: NavController,
        progressbar: BtnLoadingProgressbar
    ) {
        if (email.text.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email.text.toString())
                .matches()
        ) {
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
                    progressbar.setState(true){ // executed after animation end
                        NavigationManager.navigateTo(decodedToken, navController)
                    }

                } else {
                    println(response.code())
                    startError(progressbar)
                    email.error = "Доступ запрещён"
                    password.error = "Доступ запрещён"
                }
            }

            override fun onFailure(call: Call<AuthResponseDto>, t: Throwable) {
                call.cancel()
            }
        })
    }

    private fun startError(progressbar: BtnLoadingProgressbar) {
        progressbar.reset()
        handler.postDelayed({
            progressbar.setLoading()
            handler.postDelayed({
                progressbar.setState(false) { // executed after animation end
                    handler.postDelayed({
                        progressbar.reset()
                    }, 1500)
                }
            }, 2000)
        }, 600)
    }
}
