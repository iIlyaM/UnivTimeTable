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
            binding.univNameView.text = abbreviateUniversityName(univ.universityName).toUpperCase()
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


//class UnivListAdapter(
//    var context: Context,
//    var univs: List<UnivDto>,
//    val listener: OnUnivItemClickListener
//) :
//    RecyclerView.Adapter<UnivListAdapter.UnivViewHolder>() {
//    inner class UnivViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        var univNameView: TextView
//        var cityNameView: TextView
//        var settingsBtn: ImageView
//        var deleteBtn: ImageView
//
//        init {
//            itemView.setOnClickListener {
//                listener.onItemClick(univs[adapterPosition].id.toInt())
//            }
//
//            univNameView = itemView.findViewById(R.id.univNameView)
//            cityNameView = itemView.findViewById(R.id.cityNameView)
//            settingsBtn = itemView.findViewById(R.id.icSettingsView)
//            deleteBtn = itemView.findViewById(R.id.icDeleteView)
//
//            settingsBtn.setOnClickListener {
//                val position = adapterPosition
//                if (position != RecyclerView.NO_POSITION) {
//                    listener.onEditClick(univs[position])
//                }
//            }
//
//            deleteBtn.setOnClickListener {
//                val position = adapterPosition
//                if (position != RecyclerView.NO_POSITION) {
//                    listener.onDeleteClick(univs[position])
//                }
//            }
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnivViewHolder {
//        return UnivViewHolder(
//            LayoutInflater.from(context).inflate(
//                R.layout.univs_list_item, parent,
//                false
//            )
//        )
//    }
//
//    override fun onBindViewHolder(holder: UnivViewHolder, position: Int) {
//        val obj = univs[position]
//        holder.univNameView.text = abbreviateUniversityName(obj.universityName).toUpperCase()
//        holder.cityNameView.text = obj.city
//        holder.settingsBtn.setOnClickListener { view ->
//            listener.onEditClick(univs[position])
//        }
//        holder.deleteBtn.setOnClickListener { view ->
//            listener.onDeleteClick(univs[position])
//        }
//    }
//
//
//    override fun getItemCount(): Int {
//        return univs.size
//    }
//
//    private fun abbreviateUniversityName(fullName: String): String {
//        val words = fullName.split(" ")
//        val abbreviation = StringBuilder()
//        for (word in words) {
//            if (word.isNotBlank()) {
//                abbreviation.append(word[0])
//            }
//        }
//        return abbreviation.toString()
//    }
//}