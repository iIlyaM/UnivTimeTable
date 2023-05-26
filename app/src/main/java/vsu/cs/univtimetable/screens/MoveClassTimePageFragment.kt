package vsu.cs.univtimetable.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import vsu.cs.univtimetable.R

class MoveClassTimePageFragment : Fragment() {

    private val subjects =
        arrayListOf("Физика", "ЯП Java", "ТИПИС", "Квант. теория", "Физика", "ЯП Java", "ТИПИС", "Квант. теория", "Физика", "ЯП Java", "ТИПИС", "Квант. теория")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_move_class_time_page, container, false)
        val view = inflater.inflate(R.layout.fragment_move_class_time_page, container, false)

        val subjectCompleteView =
            view.findViewById<AutoCompleteTextView>(R.id.subjAutoCompleteTextView)
        val dayAutoCompleteTextView =
            view.findViewById<AutoCompleteTextView>(R.id.dayAutoCompleteTextView)
        val classTimeAutoCompleteTextView =
            view.findViewById<AutoCompleteTextView>(R.id.classTimeAutoCompleteTextView)
        val audienceAutoCompleteTextView =
            view.findViewById<AutoCompleteTextView>(R.id.audienceAutoCompleteTextView)
        val dayTimeAutoCompleteTextView =
            view.findViewById<AutoCompleteTextView>(R.id.dayTimeAutoCompleteTextView)


        setListener(subjectCompleteView, subjects)
        setListener(dayAutoCompleteTextView, subjects)
        setListener(classTimeAutoCompleteTextView, subjects)
        setListener(audienceAutoCompleteTextView, subjects)
        setListener(dayTimeAutoCompleteTextView, subjects)


        return view
    }

    private fun setListener(textView: AutoCompleteTextView, list: ArrayList<String>) {
        val adapter = ArrayAdapter(requireContext(), R.layout.move_class_item, list)
        textView.setAdapter(adapter)
        textView.setOnItemClickListener {
                parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position) as String
        }
    }
}