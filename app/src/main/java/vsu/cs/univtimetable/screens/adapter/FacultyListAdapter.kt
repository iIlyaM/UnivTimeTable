package vsu.cs.univtimetable.screens.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import vsu.cs.univtimetable.R
import vsu.cs.univtimetable.dto.FacultyDto

interface OnFacultiesItemClickListener {
    fun onEditClick(faculty: FacultyDto)
    fun onDeleteClick(faculty: FacultyDto)
    fun onItemClick(facultyId: Int)
}

class FacultyListAdapter(
    var context: Context,
    var faculties: List<FacultyDto>,
    val listener: OnFacultiesItemClickListener
) :
    RecyclerView.Adapter<FacultyListAdapter.FacultyViewHolder>() {
    inner class FacultyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var facultyNameView: TextView
        var settingsBtn: ImageView
        var deleteBtn: ImageView


        init {
            itemView.setOnClickListener {
                listener.onItemClick(faculties[adapterPosition].id)
            }
            facultyNameView = itemView.findViewById(R.id.facultyNameView)

            settingsBtn = itemView.findViewById(R.id.editFacultyIcView)
            deleteBtn = itemView.findViewById(R.id.deleteFacultyIcView)

            settingsBtn.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onEditClick(faculties[position])
                }
            }

            deleteBtn.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onDeleteClick(faculties[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacultyViewHolder {
        return FacultyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.faculty_list_item, parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return faculties.size
    }

    override fun onBindViewHolder(holder: FacultyViewHolder, position: Int) {
        val faculty = faculties[position]
        holder.facultyNameView.text = faculty.name
    }

}