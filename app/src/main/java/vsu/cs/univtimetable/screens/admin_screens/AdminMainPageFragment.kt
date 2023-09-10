package vsu.cs.univtimetable.screens.admin_screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.widget.AppCompatButton
import androidx.navigation.fragment.findNavController
import vsu.cs.univtimetable.R
import vsu.cs.univtimetable.utils.token_utils.SessionManager


class AdminMainPageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_main_page, container, false)
        val usersBtn = view.findViewById<AppCompatButton>(R.id.usersButton)
        usersBtn.setOnClickListener {
            findNavController().navigate(R.id.action_adminMainPageFragment_to_userListPageFragment)
        }

        val univsButton = view.findViewById<AppCompatButton>(R.id.univsButton)
        univsButton.setOnClickListener {
            findNavController().navigate(R.id.action_adminMainPageFragment_to_univListPageFragment)
        }

        val adminLogoutBtn = view.findViewById<ImageButton>(R.id.adminLogoutBtn)
        adminLogoutBtn.setOnClickListener {
            SessionManager.isAuth = false
            SessionManager.clearData(requireContext())
             findNavController().navigate(R.id.action_adminMainPageFragment_to_loginFragment)
        }

        return view
    }
}