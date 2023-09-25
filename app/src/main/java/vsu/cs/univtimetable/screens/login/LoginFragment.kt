package vsu.cs.univtimetable.screens.login

import android.content.Context
import android.content.SharedPreferences
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
import vsu.cs.univtimetable.BtnLoadingProgressbar
import vsu.cs.univtimetable.R
import vsu.cs.univtimetable.utils.token_utils.SessionManager
import vsu.cs.univtimetable.TimetableClient
import vsu.cs.univtimetable.api.UserAuthApi
import vsu.cs.univtimetable.dto.auth.AuthRequestDto
import vsu.cs.univtimetable.repository.AuthRepository
import vsu.cs.univtimetable.screens.admin_screens.univ.UnivViewModelFactory
import vsu.cs.univtimetable.utils.NavigationManager
import vsu.cs.univtimetable.utils.NotificationManager
import vsu.cs.univtimetable.utils.Status


class LoginFragment : Fragment() {

    private lateinit var authApi: UserAuthApi
    private lateinit var loginViewModel: LoginViewModel
    private val handler = Handler()
    private lateinit var sharedPreferencesEmail: SharedPreferences
    private lateinit var sharedPreferencesPassword: SharedPreferences
    private var flag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferencesEmail =
            requireActivity().getSharedPreferences("email", Context.MODE_PRIVATE)
        sharedPreferencesPassword =
            requireActivity().getSharedPreferences("password", Context.MODE_PRIVATE)
        authApi = TimetableClient.getClient().create(UserAuthApi::class.java)
        if (!sharedPreferencesEmail.equals("")) {
            flag = true
        }
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

        setIfSharedPreferencesNotEmpty(emailField, pwd)

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

        loginViewModel.login(AuthRequestDto(email.text.toString(), password.text.toString()))
            .observe(viewLifecycleOwner) {
                it?.let {
                    when (it.status) {
                        Status.SUCCESS -> {
                            val token = it.data?.token
                            SessionManager.saveAuthToken(requireContext(), token!!)
                            val decodedToken = SessionManager.decodeToken(token)
                            setEmailToSharedPreferences(email.text.toString())
                            setPasswordToSharedPreferences(password.text.toString())
                            SessionManager.isAuth = true
                            progressbar.setState(true) {
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

    private fun setEmailToSharedPreferences(value: String) {
        val editor = sharedPreferencesEmail.edit()
        editor.putString("email", value)
        editor.apply()
    }

    private fun setPasswordToSharedPreferences(value: String){
        val editor = sharedPreferencesPassword.edit()
        editor.putString("password", value)
        editor.apply()
    }

    private fun getEmailFromSharedPreferences(): String {
        return sharedPreferencesEmail.getString("email", "") ?: ""
    }

    private fun getPasswordFromSharedPreferences(): String {
        return sharedPreferencesPassword.getString("password", "") ?: ""
    }

    private fun setIfSharedPreferencesNotEmpty(
        email: EditText,
        password: EditText,
    ) {
        if(flag) {
            email.setText(getEmailFromSharedPreferences())
            password.setText(getPasswordFromSharedPreferences())
        }
    }

}
