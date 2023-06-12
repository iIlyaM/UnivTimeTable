package vsu.cs.univtimetable.screens.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LecturerNameAdapter(private val lecturers: List<String>) : RecyclerView.Adapter<LecturerNameAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_2, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val lecturer = lecturers[position]
        holder.bind(lecturer)
    }

    override fun getItemCount(): Int {
        return lecturers.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(lecturer: String) {
            val textView = itemView.findViewById<TextView>(android.R.id.text1)
            textView.text = lecturer
        }
    }
}