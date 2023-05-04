package vsu.cs.univtimetable.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.navigation.fragment.findNavController
import vsu.cs.univtimetable.R


class AdminMainPageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_admin_main_page, container, false)
        val button = view.findViewById<ImageButton>(R.id.prevPageButton)
        button.setOnClickListener {
            findNavController().navigate(R.id.action_adminMainPageFragment_to_loginFragment)
        }

        return view
    }
}