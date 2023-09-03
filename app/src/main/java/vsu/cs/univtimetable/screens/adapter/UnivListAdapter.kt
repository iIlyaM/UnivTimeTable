package vsu.cs.univtimetable.screens.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import vsu.cs.univtimetable.databinding.UnivsListItemBinding
import vsu.cs.univtimetable.dto.univ.UnivDto


interface OnUnivEditInterface {
    fun onEditClick(id: Int)
}

interface OnUnivDeleteInterface {
    fun onDeleteClick(id: Int)
}


interface OnUnivClickInterface {
    fun onItemClick(id: Int)
}


class UnivListAdapter(
    private val onEditClick: OnUnivEditInterface,
    private val onDeleteClick: OnUnivDeleteInterface,
    private val onItemClick: OnUnivClickInterface,
) :
    ListAdapter<UnivDto, UnivListAdapter.ViewHolder>(
        DiffUtilCallback
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnivListAdapter.ViewHolder {
        return ViewHolder(
            UnivsListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: UnivListAdapter.ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private object DiffUtilCallback : DiffUtil.ItemCallback<UnivDto>() {
        override fun areItemsTheSame(oldItem: UnivDto, newItem: UnivDto): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: UnivDto, newItem: UnivDto): Boolean =
            oldItem == newItem
    }


    inner class ViewHolder(
        private val binding: UnivsListItemBinding
    ) : RecyclerView.ViewHolder(binding.root){

        fun bind(univ: UnivDto) {
            binding.univNameView.text = univ.universityName
            binding.shortUnivNameView.text = abbreviateUniversityName(univ.universityName).toUpperCase()
            binding.cityNameView.text = univ.city

            binding.icSettingsView.setOnClickListener {
                val id = getItem(position).id
                onEditClick.onEditClick(id.toInt())
            }

            binding.icDeleteView.setOnClickListener {
                val id = getItem(position).id
                Log.d("Error", "bind: $id, ${univ.id}")
                onDeleteClick.onDeleteClick(id.toInt())
            }

            itemView.setOnClickListener {
                val id = getItem(position).id
                onItemClick.onItemClick(id.toInt())
            }
        }

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
