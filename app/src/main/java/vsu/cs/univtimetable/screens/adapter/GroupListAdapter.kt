package vsu.cs.univtimetable.screens.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import vsu.cs.univtimetable.R
import vsu.cs.univtimetable.dto.GroupDto

interface OnGroupItemClickListener {
    fun onEditClick(groupDto: GroupDto)
    fun onDeleteClick(groupDto: GroupDto)

}

class GroupListAdapter(
    var context: Context,
    var groups: List<GroupDto>,
    val listener: OnGroupItemClickListener
):
    RecyclerView.Adapter<GroupListAdapter.GroupViewHolder>() {
    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var courseNumView: TextView
        var groupNumView: TextView
        var headmanNameView: TextView
        var studentsCountView: TextView
        var editGroupView: ImageView
        var deleteGroupView: ImageView

        init {
            courseNumView = itemView.findViewById(R.id.courseNumView)
            groupNumView = itemView.findViewById(R.id.groupNumView)
            headmanNameView = itemView.findViewById(R.id.headmanNameView)
            studentsCountView = itemView.findViewById(R.id.studentsCountView)
            editGroupView = itemView.findViewById(R.id.editGroupView)
            deleteGroupView = itemView.findViewById(R.id.deleteGroupView)

            editGroupView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onEditClick(groups[position])
                }
            }

            deleteGroupView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onDeleteClick(groups[position])
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        return GroupViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.group_list_item, parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return groups.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val group = groups[position]
        holder.courseNumView.text = "Курс: ${group.courseNumber}"
        holder.groupNumView.text = "Группа: ${group.groupNumber}"
        if(group.headman == null) {
            holder.headmanNameView.text = "Староста: не назначен"

        } else {
            holder.headmanNameView.text = "Староста: \n ${getHeadmanShortName(group.headman.fullName)}"
        }
        holder.studentsCountView.text = "Студентов:${group.studentsAmount}"
    }

    private fun getHeadmanShortName(fullName: String): String {
        val parts = fullName.split(" ")
        val shortenedParts = parts.mapIndexed { index, part ->
            if (index == 0) {
                part
            } else {
                "${part.first()}."
            }
        }
         return shortenedParts.joinToString(" ")
    }
}