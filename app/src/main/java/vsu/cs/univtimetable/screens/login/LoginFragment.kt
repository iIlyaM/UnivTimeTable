package vsu.cs.univtimetable.screens.login

import android.os.Bundle
import android.os.Handler
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vsu.cs.univtimetable.BtnLoadingProgressbar
import vsu.cs.univtimetable.R
import vsu.cs.univtimetable.utils.token_utils.SessionManager
import vsu.cs.univtimetable.TimetableClient
import vsu.cs.univtimetable.api.UserAuthApi
import vsu.cs.univtimetable.dto.auth.AuthRequestDto
import vsu.cs.univtimetable.dto.auth.AuthResponseDto
import vsu.cs.univtimetable.dto.univ.CreateFacultyDto
import vsu.cs.univtimetable.repository.AuthRepository
import vsu.cs.univtimetable.screens.admin_screens.univ.UnivViewModelFactory
import vsu.cs.univtimetable.utils.NavigationManager
import vsu.cs.univtimetable.utils.NotificationManager
import vsu.cs.univtimetable.utils.Status


class LoginFragment : Fragment() {

    private lateinit var authApi: UserAuthApi
    private lateinit var loginViewModel: LoginViewModel
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

        val authRepository = AuthRepository(authApi)

        loginViewModel =
            ViewModelProvider(
                requireActivity(),
                UnivViewModelFactory(authRepository, "")
            )[LoginViewModel::class.java]

        button.setOnClickListener {
            val progressbar = BtnLoadingProgressbar(it)
            progressbar.setLoading()
            login(emailField, pwd, navController, progressbar)
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
            startError(progressbar)
            return
        }
        if (password.text.isEmpty()) {
            password.error = "Вы должны ввести пароль"
            startError(progressbar)
            return
        }

        loginViewModel.login(AuthRequestDto(email.text.toString(), password.text.toString())).observe(viewLifecycleOwner) {
            it?.let {
                when (it.status) {
                    Status.SUCCESS -> {
                        val token = it.data?.token
                        SessionManager.saveAuthToken(requireContext(), token!!)
                        val decodedToken = SessionManager.decodeToken(token)
                        SessionManager.isAuth = true
                        progressbar.setState(true){
                            NavigationManager.navigateTo(decodedToken, navController)
                        }
                    }

                    Status.ERROR -> {
                        startError(progressbar)
                        NotificationManager.showToastNotification(
                            requireContext(),
                            it.message.toString()
                        )
                    }
                    Status.LOADING -> {
                    }
                }
            }
        }
    }

    private fun startError(progressbar: BtnLoadingProgressbar) {
        progressbar.reset()
        handler.postDelayed({
            progressbar.setLoading()
            handler.postDelayed({
                progressbar.setState(false) {
                    handler.postDelayed({
                        progressbar.reset()
                    }, 1500)
                }
            }, 2000)
        }, 600)
    }
}
