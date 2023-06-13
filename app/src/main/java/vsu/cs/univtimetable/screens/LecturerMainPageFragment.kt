package vsu.cs.univtimetable.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.yandex.metrica.YandexMetrica
import vsu.cs.univtimetable.R


class LecturerMainPageFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
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

        YandexMetrica.reportEvent("Главная страница пользователя")

        return view
    }
}