package vsu.cs.univtimetable.screens.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import vsu.cs.univtimetable.databinding.UserListItemBinding
import vsu.cs.univtimetable.dto.user.UserDisplayDto


interface OnUserEditInterface {
    fun onEditClick(userId: Int)
}

interface OnUserDeleteInterface {
    fun onDeleteClick(userId: Int)
}

class UserListAdapter(
    private val onEditClick: OnUserEditInterface,
    private val onDeleteClick: OnUserDeleteInterface
) : ListAdapter<UserDisplayDto, UserListAdapter.ViewHolder>(
    DiffUtilCallback
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            UserListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: UserListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: UserDisplayDto) {
            binding.fullNameView.text = user.fullName
            if (user.univName != null) {
                binding.univView.text = abbreviateUniversityName(user.univName).toUpperCase()
            } else {
                binding.univView.text = user.univName
            }
            binding.facultyView.text = user.facultyName
            binding.roleView.text = user.role
            binding.cityView.text = user.city
            binding.icUserEditView.setOnClickListener {
                val id = getItem(position).id
                onEditClick.onEditClick(id)
            }
            binding.icUserDeleteView.setOnClickListener {
                val id = getItem(position).id
                Log.d("Error", "bind: $id, ${user.id}")
                onDeleteClick.onDeleteClick(id)
            }
        }
    }

    private object DiffUtilCallback : DiffUtil.ItemCallback<UserDisplayDto>() {
        override fun areItemsTheSame(oldItem: UserDisplayDto, newItem: UserDisplayDto): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: UserDisplayDto, newItem: UserDisplayDto): Boolean =
            oldItem == newItem
    }

    private fun abbreviateUniversityName(fullName: String): String {
        val words = fullName.split(" ")
        val abbreviation = StringBuilder()
        for (word in words) {
            if (word.isNotBlank()) {
                abbreviation.append(word[0])
            }
        }
        return abbreviation.toString()
    }
}

