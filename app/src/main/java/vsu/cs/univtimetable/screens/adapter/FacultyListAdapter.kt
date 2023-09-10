package vsu.cs.univtimetable.screens.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import vsu.cs.univtimetable.databinding.FacultyListItemBinding
import vsu.cs.univtimetable.dto.faculty.FacultyDto
import vsu.cs.univtimetable.dto.univ.UnivDto

interface OnFacultiesItemClickInterface {
    fun onItemClick(facultyId: Int)
}

interface OnFacultyDeleteInterface {
    fun onDeleteClick(id: Int)
}

interface OnFacultyEditInterface {
    fun onEditClick(id: Int)
}


class FacultyListAdapter(
    private val onEditClick: OnFacultyEditInterface,
    private val onDeleteClick: OnFacultyDeleteInterface,
    private val onItemClick: OnFacultiesItemClickInterface,
) :
    ListAdapter<FacultyDto, FacultyListAdapter.ViewHolder>(
        DiffUtilCallback
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacultyListAdapter.ViewHolder {
        return ViewHolder(
            FacultyListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FacultyListAdapter.ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private object DiffUtilCallback : DiffUtil.ItemCallback<FacultyDto>() {
        override fun areItemsTheSame(oldItem: FacultyDto, newItem: FacultyDto): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: FacultyDto, newItem: FacultyDto): Boolean =
            oldItem == newItem
    }

    inner class ViewHolder(
        private val binding: FacultyListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(faculty: FacultyDto) {
            binding.facultyNameView.text = faculty.name

            binding.editFacultyIcView.setOnClickListener {
                val id = getItem(position).id
                onEditClick.onEditClick(id.toInt())
            }

            binding.deleteFacultyIcView.setOnClickListener {
                val id = getItem(position).id
                Log.d("Error", "bind: $id, ${faculty.id}")
                onDeleteClick.onDeleteClick(id.toInt())
            }

            itemView.setOnClickListener {
                val id = getItem(position).id
                onItemClick.onItemClick(id.toInt())
            }
        }

    }
//            inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//                var facultyNameView: TextView
//                var settingsBtn: ImageView
//                var deleteBtn: ImageView
//
//
//                init {
//                    itemView.setOnClickListener {
//                        listener.onItemClick(faculties[adapterPosition].id)
//                    }
//                    facultyNameView = itemView.findViewById(R.id.facultyNameView)
//
//                    settingsBtn = itemView.findViewById(R.id.editFacultyIcView)
//                    deleteBtn = itemView.findViewById(R.id.deleteFacultyIcView)
//
//                    settingsBtn.setOnClickListener {
//                        val position = adapterPosition
//                        if (position != RecyclerView.NO_POSITION) {
//                            listener.onEditClick(faculties[position])
//                        }
//                    }
//
//                    deleteBtn.setOnClickListener {
//                        val position = adapterPosition
//                        if (position != RecyclerView.NO_POSITION) {
//                            listener.onDeleteClick(faculties[position])
//                        }
//                    }
//                }
//            }
//
//            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacultyViewHolder {
//                return FacultyViewHolder(
//                    LayoutInflater.from(context).inflate(
//                        R.layout.faculty_list_item, parent,
//                        false
//                    )
//                )
//            }
//
//            override fun getItemCount(): Int {
//                return faculties.size
//            }
//
//            override fun onBindViewHolder(holder: FacultyViewHolder, position: Int) {
//                val faculty = faculties[position]
//                holder.facultyNameView.text = faculty.name
//            }
//
//        }
}