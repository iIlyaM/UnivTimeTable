package vsu.cs.univtimetable.screens.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import vsu.cs.univtimetable.databinding.AudienceListItemBinding
import vsu.cs.univtimetable.databinding.UnivsListItemBinding
import vsu.cs.univtimetable.dto.audience.AudienceResponse
import vsu.cs.univtimetable.dto.audience.AudienceResponseDto
import vsu.cs.univtimetable.dto.univ.UnivDto

interface OnAudienceEditInterface {
    fun onEditClick(id: Int)
}

interface OnAudienceDeleteInterface {
    fun onDeleteClick(id: Int)
}

class AudienceListAdapter(
    private val onEditClick: OnAudienceEditInterface,
    private val onDeleteClick: OnAudienceDeleteInterface
) : ListAdapter<AudienceResponseDto, AudienceListAdapter.ViewHolder>(
    AudienceListAdapter.DiffUtilCallback
) {
    inner class ViewHolder(
        private val binding: AudienceListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(audience: AudienceResponseDto) {
            binding.audienceNumberView.text = "№ ${audience.audienceNumber}"
            binding.capacityView.text = "Мест: ${audience.capacity}"
            binding.facultyView.text = audience.faculty
            binding.universityView.text =
                abbreviateUniversityName(audience.university).toUpperCase()
            binding.equipmentsView.text =
                (audience.equipments.toString()).replace("[", "").replace("]", "")

            binding.icAudienceEditView.setOnClickListener {
                val id = getItem(position).id
                onEditClick.onEditClick(id.toInt())
            }

            binding.icAudienceDeleteView.setOnClickListener {
                val id = getItem(position).id
                Log.d("Error", "bind: $id, ${audience.id}")
                onDeleteClick.onDeleteClick(id.toInt())
            }
        }

    }

    override fun onBindViewHolder(holder: AudienceListAdapter.ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private object DiffUtilCallback : DiffUtil.ItemCallback<AudienceResponseDto>() {
        override fun areItemsTheSame(
            oldItem: AudienceResponseDto,
            newItem: AudienceResponseDto
        ): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(
            oldItem: AudienceResponseDto,
            newItem: AudienceResponseDto
        ): Boolean =
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

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AudienceListAdapter.ViewHolder {
        return ViewHolder(
            AudienceListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

}