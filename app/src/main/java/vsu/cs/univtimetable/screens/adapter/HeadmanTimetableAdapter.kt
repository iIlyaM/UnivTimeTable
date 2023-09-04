package vsu.cs.univtimetable.screens.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import vsu.cs.univtimetable.R
import vsu.cs.univtimetable.dto.classes.ClassDto

class HeadmanTimetableAdapter(
    var context: Context,
    var timetableItems: List<ClassDto>,
) : RecyclerView.Adapter<HeadmanTimetableAdapter.HeadmanTimetableViewHolder>() {
    inner class HeadmanTimetableViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var lectTimeView: TextView
        var subjNameView: TextView
        var audienceNumView: TextView
        var classTypeView: TextView

        init {
            lectTimeView = itemView.findViewById(R.id.studClassTimeView)
            subjNameView = itemView.findViewById(R.id.studSubjNameView)
            audienceNumView = itemView.findViewById(R.id.subjAudNumView)
            classTypeView = itemView.findViewById(R.id.studClassTypeView)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HeadmanTimetableAdapter.HeadmanTimetableViewHolder {
        return HeadmanTimetableViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.stud_timetable_item, parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: HeadmanTimetableAdapter.HeadmanTimetableViewHolder,
        position: Int
    ) {
        val timetableItem = timetableItems[position]
        holder.lectTimeView.text = "${timetableItem.startTime.dropLast(3)} - ${timetableItem.endTime.dropLast(3)}"
        holder.subjNameView.text = timetableItem.subjectName
        holder.audienceNumView.text = "ауд.№: ${timetableItem.audience}"
        holder.classTypeView.text = timetableItem.typeOfClass
    }

    override fun getItemCount(): Int {
        return timetableItems.size
    }
}