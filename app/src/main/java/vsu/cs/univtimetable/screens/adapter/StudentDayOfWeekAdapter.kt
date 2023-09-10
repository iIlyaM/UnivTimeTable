package vsu.cs.univtimetable.screens.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import vsu.cs.univtimetable.R
import vsu.cs.univtimetable.dto.datetime.DateDto

class StudentDayOfWeekAdapter(
    var context: Context,
    var day: DateDto
) : RecyclerView.Adapter<StudentDayOfWeekAdapter.StudentDayOfWeekAdapterViewHolder>() {
    inner class StudentDayOfWeekAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var weekDateView: TextView
        var studWeekType: TextView

        init {
            weekDateView = itemView.findViewById(R.id.weekDateView)
            studWeekType = itemView.findViewById(R.id.studWeekType)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentDayOfWeekAdapterViewHolder {
        return StudentDayOfWeekAdapterViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.stud_day_item, parent,
                false
            )
        )
    }


    override fun getItemCount(): Int {
        return 1
    }

    override fun onBindViewHolder(holder: StudentDayOfWeekAdapterViewHolder, position: Int) {
        holder.weekDateView.text = day.date
        holder.studWeekType.text = day.weekType
    }
}