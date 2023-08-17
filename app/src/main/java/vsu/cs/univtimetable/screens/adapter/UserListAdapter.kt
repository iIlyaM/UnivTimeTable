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
// binding.cocktailPicIv.setImageURI(cocktail.imagePath.toUri())
//            binding.cocktailIv.setImageResource(R.drawable.cocktail_sample)
//            binding.nameTv.text = cocktail.name
            binding.fullNameView.text = user.fullName
            binding.univView.text = user.univName
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
}


//class UserListAdapter (
//    var context: Context,
//    var users: List<UserDisplayDto>,
//    val userListener: OnUserItemClickListener
//) :
//    RecyclerView.Adapter<UserListAdapter.UserViewHolder>() {
//    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val fullName: TextView
//        val univ: TextView
//        val faculty: TextView
//        val role: TextView
//        val city: TextView
//        var settingsBtn: ImageView
//        var deleteBtn: ImageView
//
//        init {
//            fullName = itemView.findViewById(R.id.fullNameView)
//            univ = itemView.findViewById(R.id.univView)
//            faculty = itemView.findViewById(R.id.facultyView)
//            role = itemView.findViewById(R.id.roleView)
//            city = itemView.findViewById(R.id.cityView)
//            settingsBtn = itemView.findViewById(R.id.icUserEditView)
//            deleteBtn = itemView.findViewById(R.id.icUserDeleteView)
//
//            settingsBtn.setOnClickListener {
//                val position = adapterPosition
//                if (position != RecyclerView.NO_POSITION) {
//                    userListener.onEditClick(users[position])
//                }
//            }
//
//            deleteBtn.setOnClickListener {
//                val position = adapterPosition
//                if (position != RecyclerView.NO_POSITION) {
//                    userListener.onDeleteClick(users[position])
//                }
//            }
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListAdapter.UserViewHolder {
//        return UserViewHolder(
//            LayoutInflater.from(context).inflate(
//                R.layout.user_list_item, parent,
//                false
//            )
//        )
//    }
//
//    override fun onBindViewHolder(holder: UserListAdapter.UserViewHolder, position: Int) {
//        val user = users[position]
//        holder.fullName.text = user.fullName
//        holder.univ.text = user.univName
//        holder.faculty.text = user.facultyName
//        holder.role.text = user.role
//        holder.city.text  = user.city
//        holder.settingsBtn.setOnClickListener { _ ->
//            userListener.onEditClick(users[position])
//        }
//        holder.deleteBtn.setOnClickListener { _ ->
//            userListener.onDeleteClick(users[position])
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return users.size
//    }
//}