package vsu.cs.univtimetable.screens.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import vsu.cs.univtimetable.R
import vsu.cs.univtimetable.dto.ClassDto

class LecturerTimetableAdapter(
    var context: Context,
    var timetableItems: List<ClassDto>,
) :
    RecyclerView.Adapter<LecturerTimetableAdapter.LecturerTimeTableViewHolder>() {
    inner class LecturerTimeTableViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var lectTimeView: TextView
        var subjNameView: TextView
        var courseNumberView: TextView
        var audienceNumView: TextView
        var classTypeView: TextView
        var lectGroupNumberView: TextView

        init {
            lectTimeView = itemView.findViewById(R.id.lectTimeView)
            subjNameView = itemView.findViewById(R.id.subjNameView)
            courseNumberView = itemView.findViewById(R.id.courseNumberView)
            audienceNumView = itemView.findViewById(R.id.audienceNumView)
            classTypeView = itemView.findViewById(R.id.classTypeView)
            lectGroupNumberView = itemView.findViewById(R.id.lectGroupNumberView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LecturerTimetableAdapter.LecturerTimeTableViewHolder {
        return LecturerTimeTableViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.lect_timetable_item, parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return timetableItems.size
    }

    override fun onBindViewHolder(holder: LecturerTimetableAdapter.LecturerTimeTableViewHolder, position: Int) {
        val timetableItem = timetableItems[position]
        holder.lectTimeView.text = timetableItem.startTime.toString()
        holder.subjNameView.text = timetableItem.subjectName
        holder.courseNumberView.text = timetableItem.courseNumber.toString()
        holder.audienceNumView.text = timetableItem.audience.toString()
        holder.classTypeView.text = timetableItem.typeOfClass
        holder.lectGroupNumberView.text = timetableItem.groupsNumber.toString()
    }
}