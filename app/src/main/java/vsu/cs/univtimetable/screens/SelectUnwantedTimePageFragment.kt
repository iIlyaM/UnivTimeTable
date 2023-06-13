package vsu.cs.univtimetable.screens

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vsu.cs.univtimetable.DateManager
import vsu.cs.univtimetable.Day
import vsu.cs.univtimetable.R
import vsu.cs.univtimetable.dto.ImpossibleTimeDto
import vsu.cs.univtimetable.screens.adapter.DayAdapter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SelectUnwantedTimePageFragment : Fragment(), DayAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DayAdapter
    private var bundle = Bundle()
    private var dayWeekTimeMap: MutableMap<String, ArrayList<String>> = mutableMapOf()

    val days = listOf(
        Day("Понедельник"),
        Day("Вторник"),
        Day("Среда"),
        Day("Четверг"),
        Day("Пятница"),
        Day("Суббота"),
        Day("Воскресенье")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_select_unwanted_time_page, container, false)
        recyclerView = view.findViewById(R.id.weekView)
        val confirmTimeBtn = view.findViewById<AppCompatButton>(R.id.confirmTimeBtn)

        adapter = DayAdapter(days)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter.setOnItemClickListener(this)

        val bundle = Bundle()
        confirmTimeBtn.setOnClickListener {
            bundle.putSerializable("map", ImpossibleTimeDto(dayWeekTimeMap))
            findNavController().navigate(
                R.id.action_selectUnwantedTimePageFragment_to_addSubjectPageFragment,
                bundle
            )
        }
        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onItemClick(position: Int) {
        val day = days[position]

        showDialog(day.name, DateManager.TIME)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showDialog(title: String, options: Array<String>) {
        val checkedItems = BooleanArray(options.size) { false }

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)
            .setMultiChoiceItems(options, checkedItems) { _, which, isChecked ->
                checkedItems[which] = isChecked
            }
            .setPositiveButton("Ок") { dialog, _ ->
                val selectedOptions = ArrayList<String>()
                for (i in options.indices) {
                    if (checkedItems[i]) {
                        selectedOptions.add(options[i].split('-')[0])
                    }
                }
                dayWeekTimeMap[title] = selectedOptions
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
        builder.create().show()
    }

}
