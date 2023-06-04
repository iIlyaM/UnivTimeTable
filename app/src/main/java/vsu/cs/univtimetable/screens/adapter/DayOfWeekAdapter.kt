package vsu.cs.univtimetable.screens.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import vsu.cs.univtimetable.R
import vsu.cs.univtimetable.dto.ClassDto
import vsu.cs.univtimetable.dto.DateDto

class DayOfWeekAdapter(
    var context: Context,
    var day: DateDto
) : RecyclerView.Adapter<DayOfWeekAdapter.DayOfWeekAdapterViewHolder>() {
    inner class DayOfWeekAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var dateView: TextView
        var weekTypeView: TextView

        init {
            dateView = itemView.findViewById(R.id.dateView)
            weekTypeView = itemView.findViewById(R.id.weekTypeView)
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
        return 1
    }

    override fun onBindViewHolder(holder: DayOfWeekAdapterViewHolder, position: Int) {
        holder.dateView.text = day.date
        holder.weekTypeView.text = day.weekType
    }
}