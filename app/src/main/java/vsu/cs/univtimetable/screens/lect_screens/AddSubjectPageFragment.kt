package vsu.cs.univtimetable.screens.lect_screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import vsu.cs.univtimetable.R

class AddSubjectPageFragment : Fragment() {

    private val groups =
        arrayOf("3к. 5гр.", "1к. 12гр.", "1к. 13гр.", "4к. 1гр.", "1к. 2гр.", "6к. 5гр.")

    private val classTypes =
        arrayOf("Лекция", "Семинар")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        return inflater.inflate(R.layout.fragment_add_subject_page, container, false)
        val view = inflater.inflate(R.layout.fragment_add_subject_page, container, false)
        val classTypeCompleteView = view.findViewById<AutoCompleteTextView>(R.id.typeAutoCompleteText)
        val typeAdapter = ArrayAdapter(requireContext(), R.layout.subj_item, classTypes)
        classTypeCompleteView.setAdapter(typeAdapter)

        classTypeCompleteView.setOnItemClickListener {
                parent, view2, position, id ->
            val selectedItem = parent.getItemAtPosition(position) as String
            // Действия, которые нужно выполнить при выборе элемента из выпадающего меню
            // Например, обновление текста или выполнение определенного действия
        }


        return view
    }
}