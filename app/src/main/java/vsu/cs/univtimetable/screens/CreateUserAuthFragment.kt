package vsu.cs.univtimetable.screens

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import vsu.cs.univtimetable.R

class CreateUserAuthFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_user_auth, container, false)
        val emailField = view.findViewById<EditText>(R.id.editTextTextEmailAddress)
        val loginField = view.findViewById<EditText>(R.id.editLoginText)
        val pwdField = view.findViewById<EditText>(R.id.editTextTextPassword2)
        val btn = view.findViewById<AppCompatButton>(R.id.confirmBtn)

        btn.setOnClickListener {
            send(emailField, pwdField, loginField)
        }
        return view
    }

    private fun send(email: EditText, password: EditText, login: EditText) {
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
        val bundle = Bundle()

        bundle.putString("email", email.text.toString())
        bundle.putString("password", email.text.toString())
        bundle.putString("login", email.text.toString())

        findNavController().navigate(
            R.id.action_createUserAuthFragment_to_createUserInfoFragment,
            bundle
        )
    }


}