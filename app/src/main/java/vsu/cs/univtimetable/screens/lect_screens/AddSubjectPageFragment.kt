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
        arrayListOf("3к. 5гр.", "1к. 12гр.", "1к. 13гр.", "4к. 1гр.")

    private val equipment =
        arrayListOf("Проектор", "Компьютеры", "Миркофон", "Микроскоп")

    private val classTypes =
        arrayOf("Лекция", "Семинар")

    var groupListView: TextView? = null
    var equipListView: TextView? = null
    lateinit var selectCard: MaterialCardView
    lateinit var selectEquipCard: MaterialCardView
    lateinit var selectedGroups: BooleanArray
    lateinit var selectedEquipment: BooleanArray
    var groupList = ArrayList<Int>()
    var equipList = ArrayList<Int>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        return inflater.inflate(R.layout.fragment_add_subject_page, container, false)
        val view = inflater.inflate(R.layout.fragment_add_subject_page, container, false)
        val classTypeCompleteView = view.findViewById<AutoCompleteTextView>(R.id.typeAutoCompleteText)
        val typeAdapter = ArrayAdapter(requireContext(), R.layout.subj_item, classTypes)

        selectCard = view.findViewById(R.id.selectGroupsView)
        selectEquipCard = view.findViewById(R.id.selectEquipmentView)

        groupListView = view.findViewById(R.id.groupList)
        equipListView = view.findViewById(R.id.equipmentList)

        selectedGroups = BooleanArray(groups.size)
        selectedEquipment = BooleanArray(equipment.size)

        classTypeCompleteView.setAdapter(typeAdapter)

        classTypeCompleteView.setOnItemClickListener {
                parent, view2, position, id ->
            val selectedItem = parent.getItemAtPosition(position) as String
            // Действия, которые нужно выполнить при выборе элемента из выпадающего меню
            // Например, обновление текста или выполнение определенного действия
        }

        selectCard.setOnClickListener {
            val itemsArray = groups.toArray(arrayOf<CharSequence>(groups.size.toString()))
            showDialog(itemsArray, groupList, selectedGroups, groupListView)
        }
        selectEquipCard.setOnClickListener {
            val equipArray = equipment.toArray(arrayOf<CharSequence>(equipment.size.toString()))
            showDialog(equipArray, equipList, selectedEquipment, equipListView)
        }
        return view
    }

    private fun showDialog(
        items: Array<CharSequence>,
        list: ArrayList<Int>,
        selectedItems: BooleanArray,
        listView: TextView?
    ) {
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("Выберите группы")

        builder.setCancelable(false)

        builder.setMultiChoiceItems(items, selectedItems) { dialogInterface, i, b ->
            if (b) {
                // when checkbox selected
                // Add position in lang list
                list.add(i)
                // Sort array list
                list.sort()
            } else {
                // when checkbox unselected
                // Remove position from langList
                list.remove(i)
            }
        }

        builder.setPositiveButton("OK") { dialogInterface, i ->
            // Initialize string builder
            val stringBuilder = StringBuilder()
            // use for loop
            for (j in list.indices) {
                // concat array value
                stringBuilder.append(items[list[j]])
                // check condition
                if (j != items.size - 1) {
                    // When j value not equal.
                    stringBuilder.append(", ")
                }
            }
            listView?.text = stringBuilder.toString()
        }

        builder.setNegativeButton("Cancel") { dialogInterface, i ->

        }

        builder.show()
    }

}