package vsu.cs.univtimetable.screens.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import vsu.cs.univtimetable.R
import vsu.cs.univtimetable.dto.ClassDto

class DayOfWeekAdapter(
    var context: Context,
    var days: List<ClassDto>
) : RecyclerView.Adapter<DayOfWeekAdapter.DayOfWeekAdapterViewHolder>() {
    inner class DayOfWeekAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var dateView: TextView
        var weekTypeView: TextView

        init {
            dateView = itemView.findViewById(R.id.dateView)
            weekTypeView = itemView.findViewById(R.id.weekView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayOfWeekAdapterViewHolder {
        return DayOfWeekAdapterViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.lect_day_item, parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return days.size
    }

    override fun onBindViewHolder(holder: DayOfWeekAdapterViewHolder, position: Int) {
        val day = days[position]
        holder.dateView.text = day.dayOfWeek
        holder.weekTypeView.text = day.weekType
    }
}