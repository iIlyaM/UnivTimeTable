package vsu.cs.univtimetable.screens.lect_screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.widget.AppCompatButton
import androidx.navigation.fragment.findNavController
import vsu.cs.univtimetable.utils.token_utils.AuthToken.token
import vsu.cs.univtimetable.R
import vsu.cs.univtimetable.utils.token_utils.SessionManager


class LecturerMainPageFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_lecturer_main_page, container, false)
        val button = view.findViewById<AppCompatButton>(R.id.lookTimeTableBtn)
        button.setOnClickListener {
            findNavController().navigate(R.id.action_lecturerMainPageFragment_to_lecturerTimetablePageFragment)
        }

        val sendRequestBtn = view.findViewById<AppCompatButton>(R.id.choosingTimeRequestBtn)
        sendRequestBtn.setOnClickListener {
            findNavController().navigate(R.id.action_lecturerMainPageFragment_to_selectUnwantedTimePageFragment)
        }

        val moveClassBtn = view.findViewById<AppCompatButton>(R.id.moveClassRequestBtn)
        moveClassBtn.setOnClickListener {
            findNavController().navigate(R.id.action_lecturerMainPageFragment_to_moveClassTimePageFragment)
        }

        val lecturerLogoutBtn = view.findViewById<ImageButton>(R.id.lecturerLogoutBtn)
        lecturerLogoutBtn.setOnClickListener {
            SessionManager.isAuth = false
            SessionManager.clearData(requireContext())
            findNavController().navigate(R.id.action_lecturerMainPageFragment_to_loginFragment)
        }

        val generateTimeTableBtn = view.findViewById<AppCompatButton>(R.id.generateTimeTableBtn)

        val token: String? = SessionManager.getToken(requireContext())
        if("MAKE_TIMETABLE_AUTHORITY" !in SessionManager.decodeToken(token!!)) {
            generateTimeTableBtn.visibility = View.GONE
        }
        generateTimeTableBtn.setOnClickListener {
            findNavController().navigate(R.id.action_lecturerMainPageFragment_to_generateTimetablePageFragment)
        }
        return view
    }
}