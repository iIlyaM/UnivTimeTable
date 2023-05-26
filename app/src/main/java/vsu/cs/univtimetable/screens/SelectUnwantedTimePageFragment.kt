package vsu.cs.univtimetable.screens

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vsu.cs.univtimetable.Day
import vsu.cs.univtimetable.R
import vsu.cs.univtimetable.screens.adapter.DayAdapter

class SelectUnwantedTimePageFragment : Fragment(), DayAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DayAdapter

    val days = listOf(
        Day("Понедельник"),
        Day("Вторник"),
        Day("Среда"),
        Day("Четверг"),
        Day("Пятница"),
        Day("Суббота")
    )
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        return inflater.inflate(R.layout.fragment_select_unwanted_time_page, container, false)
        val view = inflater.inflate(R.layout.fragment_select_unwanted_time_page, container, false)
        recyclerView = view.findViewById(R.id.weekView)

        adapter = DayAdapter(days)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter.setOnItemClickListener(this)

        return view
    }

    override fun onItemClick(position: Int) {
        val day = days[position]
        val options = arrayOf("8:00-9:35", "9:45-11:20", "11:30-13:05", "13:25-15:00", "15:10-16:45", "16:55-18:30")
        showDialog(day.name, options)
    }

    private fun showDialog(title: String, options: Array<String>) {
        val checkedItems = BooleanArray(options.size) { false }

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)
            .setMultiChoiceItems(options, checkedItems) { _, which, isChecked ->
                checkedItems[which] = isChecked
            }
            .setPositiveButton("Ок") { dialog, _ ->
                val selectedOptions = mutableListOf<String>()
                for (i in options.indices) {
                    if (checkedItems[i]) {
                        selectedOptions.add(options[i])
                    }
                }
                // Обработка выбранных вариантов
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
        builder.create().show()
    }
}
