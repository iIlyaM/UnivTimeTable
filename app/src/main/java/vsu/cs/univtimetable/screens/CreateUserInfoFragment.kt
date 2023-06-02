package vsu.cs.univtimetable.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import vsu.cs.univtimetable.R

class CreateUserInfoFragment : Fragment() {
    private val roleType =
        arrayOf("HEADMAN", "ADMIN", "LECTURER")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_create_user_info, container, false)
        val view = inflater.inflate(R.layout.fragment_create_user_info, container, false)



        return view
    }

}