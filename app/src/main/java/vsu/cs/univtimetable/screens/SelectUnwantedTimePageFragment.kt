package vsu.cs.univtimetable.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.navigation.fragment.findNavController
import vsu.cs.univtimetable.R

class SelectUnwantedTimePageFragment : Fragment() {

    val days = arrayOf("Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        return inflater.inflate(R.layout.fragment_select_unwanted_time_page, container, false)
        val view = inflater.inflate(R.layout.fragment_select_unwanted_time_page, container, false)
        val autoCompleteView = view.findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        val daysAdapter = ArrayAdapter(requireContext(), R.layout.week_item, days)
        autoCompleteView.setAdapter(daysAdapter)



        return view
    }
}