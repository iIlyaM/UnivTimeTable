package vsu.cs.univtimetable.screens.lect_screens

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.leandroborgesferreira.loadingbutton.customViews.CircularProgressButton
import vsu.cs.univtimetable.utils.date_utils.DateManager
import vsu.cs.univtimetable.dto.datetime.Day
import vsu.cs.univtimetable.R
import vsu.cs.univtimetable.dto.datetime.ImpossibleTimeDto
import vsu.cs.univtimetable.screens.adapter.DayAdapter
import vsu.cs.univtimetable.utils.date_utils.DateManager.Companion.clearChoices

class SelectUnwantedTimePageFragment : Fragment(), DayAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DayAdapter
    private lateinit var confirmTimeBtn: CircularProgressButton
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
        confirmTimeBtn = view.findViewById(R.id.confirmTimeBtn)

        adapter = DayAdapter(days)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter.setOnItemClickListener(this)

        val bundle = Bundle()
        confirmTimeBtn.setOnClickListener {
            confirmTimeBtn.startAnimation()
            bundle.putSerializable("map", ImpossibleTimeDto(dayWeekTimeMap))
            findNavController().navigate(
                R.id.action_selectUnwantedTimePageFragment_to_addSubjectPageFragment,
                bundle
            )
            stopAnimation(confirmTimeBtn)
        }

        val prevPageButton = view.findViewById<ImageButton>(R.id.prevPageButton)
        prevPageButton.setOnClickListener {
            clearChoices(requireContext())
            findNavController().navigate(R.id.action_selectUnwantedTimePageFragment_to_lecturerMainPageFragment)
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
        val sharedPreferences = requireContext().getSharedPreferences("dayTimeChoices", Context.MODE_PRIVATE)
        val checkedItems = BooleanArray(options.size) { false }

        // Загрузить предыдущие выбранные значения из SharedPreferences
        val savedSelections = sharedPreferences.getStringSet(title, emptySet())
        savedSelections?.forEach { selectedOption ->
            val index = options.indexOfFirst { it.startsWith(selectedOption) }
            if (index != -1) {
                checkedItems[index] = true
            }
        }

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

                // Сохранить выбранные значения в SharedPreferences
                val editor = sharedPreferences.edit()
                editor.putStringSet(title, selectedOptions.toSet())
                editor.apply()

                dayWeekTimeMap[title] = selectedOptions
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
        builder.create().show()
    }

    private fun stopAnimation(btn: CircularProgressButton) {
        btn.background = ContextCompat.getDrawable(requireContext(), R.drawable.lecturer_bg)
        btn.revertAnimation()
    }
}
