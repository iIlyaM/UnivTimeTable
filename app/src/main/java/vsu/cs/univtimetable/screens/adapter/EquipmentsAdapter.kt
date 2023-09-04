package vsu.cs.univtimetable.screens.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import vsu.cs.univtimetable.databinding.EquipmentItemBinding
interface OnEquipmentDeleteInterface {
    fun onDeleteClick(equipment: String)
}

class EquipmentsAdapter(
    private val onDeleteClick: OnEquipmentDeleteInterface
) : ListAdapter<String, EquipmentsAdapter.ViewHolder>(
    DiffUtilCallback
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            EquipmentItemBinding.inflate(
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
        private val binding: EquipmentItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(equipment: String) {
            binding.equipmentNameView.text = equipment

            binding.deleteEquipmentIcView.setOnClickListener {
                val equipment = getItem(position)
                onDeleteClick.onDeleteClick(equipment)
            }
        }
    }

    private object DiffUtilCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
            oldItem == newItem
    }

}