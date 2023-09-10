package vsu.cs.univtimetable.screens.adapter


import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import vsu.cs.univtimetable.databinding.GroupListItemBinding
import vsu.cs.univtimetable.dto.group.GroupDto
import vsu.cs.univtimetable.dto.user.UserDisplayDto

interface OnGroupEditClickInterface {
    fun onEditClick(groupId: Long)
}

interface OnGroupDeleteClickInterface {
    fun onDeleteClick(groupId: Long)
}

class GroupListAdapter(
    private val onEditClick: OnGroupEditClickInterface,
    private val onDeleteClick: OnGroupDeleteClickInterface
) :
    ListAdapter<GroupDto, GroupListAdapter.ViewHolder>(
        DiffUtilCallback
    ) {
    inner class ViewHolder(
        private val binding: GroupListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(group: GroupDto) {
            binding.courseNumView.text ="Курс: ${group.courseNumber}"
            binding.groupNumView.text = "Группа: ${group.groupNumber}"
            binding.headmanNameView.text = checkHeadman(group.headman)
            binding.studentsCountView.text = "Студентов:${group.studentsAmount}"
            binding.editGroupView.setOnClickListener {
                val id = getItem(position).id!!
                onEditClick.onEditClick(id)
            }
            binding.deleteGroupView.setOnClickListener {
                val id = getItem(position).id!!
                Log.d("Error", "bind: $id, ${group.id}")
                onDeleteClick.onDeleteClick(id)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupListAdapter.ViewHolder {
        return ViewHolder(
            GroupListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    private object DiffUtilCallback : DiffUtil.ItemCallback<GroupDto>() {
        override fun areItemsTheSame(oldItem: GroupDto, newItem: GroupDto): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: GroupDto, newItem: GroupDto): Boolean =
            oldItem == newItem
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

    private fun checkHeadman(headman: UserDisplayDto?): String {
        return if(headman == null) {
            "Староста: не назначен"
        } else {
            val checkedName = headman.fullName.trimEnd().trimStart()
            getHeadmanShortName(checkedName)
        }
    }
}