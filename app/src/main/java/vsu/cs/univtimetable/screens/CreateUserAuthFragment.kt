package vsu.cs.univtimetable.screens

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
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
        val cityField = view.findViewById<EditText>(R.id.editUserCity)
        val pwdField = view.findViewById<EditText>(R.id.editTextTextPassword2)
        val btn = view.findViewById<AppCompatButton>(R.id.confirmBtn)

        val prevPageButton = view.findViewById<ImageButton>(R.id.prevPageButton)
        prevPageButton.setOnClickListener {
            findNavController().navigate(R.id.action_createUserAuthFragment_to_userListPageFragment)
        }

        val mainPageButton = view.findViewById<ImageButton>(R.id.mainPageButton)
        mainPageButton.setOnClickListener {
            findNavController().navigate(R.id.action_createUserAuthFragment_to_adminMainPageFragment)
        }

        setFieldsIfEdit(emailField, pwdField, loginField, cityField)

        btn.setOnClickListener {
            send(emailField, pwdField, loginField, cityField)
        }
        return view
    }

    private fun send(email: EditText, password: EditText, login: EditText, city: EditText) {
        val editable = arguments?.getBoolean("editable");

        if (email.text.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email.text.toString())
                .matches()
        ) {
            email.error = "Почта введена некорректно"
            return
        }
        if (password.text.isEmpty() && editable == null) {
            password.error = "Вы должны ввести пароль"
            return
        }
        if (login.text.isEmpty()) {
            login.error = "Введите имя пользователя"
            return
        }
        if (city.text.isEmpty()) {
            city.error = "Введите город"
            return
        }
        val bundle = Bundle()

        if (editable != null && editable) {
            bundle.putAll(arguments)
        }

        bundle.putString("email", email.text.toString())
        bundle.putString("password", password.text.toString())
        bundle.putString("login", login.text.toString())
        bundle.putString("city", city.text.toString())

        findNavController().navigate(
            R.id.action_createUserAuthFragment_to_createUserInfoFragment,
            bundle
        )
    }

    private fun setFieldsIfEdit(
        email: EditText,
        password: EditText,
        login: EditText,
        city: EditText
    ) {
        val editable = arguments?.getBoolean("editable");
        if (editable != null && editable) {
            email.setText(arguments?.getString("email"))
            password.setText(arguments?.getString("password"))
            login.setText(arguments?.getString("login"))
            city.setText(arguments?.getString("city"))
        }
    }
}