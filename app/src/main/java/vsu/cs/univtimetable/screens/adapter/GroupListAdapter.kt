package vsu.cs.univtimetable.screens.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vsu.cs.univtimetable.dto.GroupDto
import vsu.cs.univtimetable.dto.UnivDto

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

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        TODO("Not yet implemented")
    }
}