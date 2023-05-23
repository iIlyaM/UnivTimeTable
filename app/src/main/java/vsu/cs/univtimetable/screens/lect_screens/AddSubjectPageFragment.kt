package vsu.cs.univtimetable.screens.lect_screens

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import vsu.cs.univtimetable.R


class AddSubjectPageFragment : Fragment() {

    private val groups =
        arrayOf("3к. 5гр.", "1к. 12гр.", "1к. 13гр.", "4к. 1гр.")

    private val classTypes =
        arrayOf("Лекция", "Семинар")

    var groupListView: TextView? = null
    lateinit var selectCard: MaterialCardView
    lateinit var selectedGroups: BooleanArray
    var langList = ArrayList<Int>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        return inflater.inflate(R.layout.fragment_add_subject_page, container, false)
        val view = inflater.inflate(R.layout.fragment_add_subject_page, container, false)
//        val classTypeCompleteView = view.findViewById<AutoCompleteTextView>(R.id.typeAutoCompleteText)
        val typeAdapter = ArrayAdapter(requireContext(), R.layout.subj_item, classTypes)

        selectCard = view.findViewById(R.id.selectGroupsView)
        groupListView = view.findViewById(R.id.groupList)
        selectedGroups = BooleanArray(groups.size)

//        classTypeCompleteView.setAdapter(typeAdapter)
//
//        classTypeCompleteView.setOnItemClickListener {
//                parent, view2, position, id ->
//            val selectedItem = parent.getItemAtPosition(position) as String
//            // Действия, которые нужно выполнить при выборе элемента из выпадающего меню
//            // Например, обновление текста или выполнение определенного действия
//        }

        selectCard.setOnClickListener {
//            val options = arrayOf("Option 1", "Option 2", "Option 3")
//            val selectedOptions = ArrayList<Int>()
//
//            val builder = AlertDialog.Builder(requireContext())
//            builder.setTitle("Select Options")
//            builder.setMultiChoiceItems(options, null) { _, which, isChecked ->
//                if (isChecked) {
//                    selectedOptions.add(which)
//                } else if (selectedOptions.contains(which)) {
//                    selectedOptions.remove(Integer.valueOf(which))
//                }
//            }
//            builder.setPositiveButton("OK") { _, _ ->
//                // Действия, которые нужно выполнить при выборе опций
//                for (option in selectedOptions) {
//                    // Выполнение действий для каждой выбранной опции
//                }
//            }
//            builder.setNegativeButton("Cancel") { dialog, _ ->
//                dialog.dismiss()
//            }
//            val dialog = builder.create()
//            dialog.show()

            val builder = AlertDialog.Builder(requireContext())

            // set title
            builder.setTitle("Select Groups")

            // set dialog non cancelable
            builder.setCancelable(false)

            builder.setMultiChoiceItems(groups, selectedGroups) { dialogInterface, i, b ->
                // check condition
                if (b) {
                    // when checkbox selected
                    // Add position in lang list
                    langList.add(i)
                    // Sort array list
                    langList.sort()
                } else {
                    // when checkbox unselected
                    // Remove position from langList
                    langList.remove(i)
                }
            }

            builder.setPositiveButton("OK") { dialogInterface, i ->
                // Initialize string builder
                val stringBuilder = StringBuilder()
                // use for loop
                for (j in langList.indices) {
                    // concat array value
                    stringBuilder.append(groups[langList[j]])
                    // check condition
                    if (j != langList.size - 1) {
                        // When j value not equal.
                        stringBuilder.append(", ")
                    }
                }
                groupListView?.text = stringBuilder.toString()
            }

            builder.setNegativeButton("Cancel") { dialogInterface, i ->

            }

            builder.show()
        }
        return view
    }

    private fun showDialog() {
        // Initialize alert dialog

    }
}