package vsu.cs.univtimetable.screens.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import vsu.cs.univtimetable.R
import vsu.cs.univtimetable.dto.UserDisplayDto


interface OnUserItemClickListener {
    fun onEditClick(user: UserDisplayDto)
    fun onDeleteClick(user: UserDisplayDto)
}


class UserListAdapter (
    var context: Context,
    var users: List<UserDisplayDto>,
    val userListener: OnUserItemClickListener
) :
    RecyclerView.Adapter<UserListAdapter.UserViewHolder>() {
    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fullName: TextView
        val univ: TextView
        val faculty: TextView
        val role: TextView
        val city: TextView
        var settingsBtn: ImageView
        var deleteBtn: ImageView

        init {
            fullName = itemView.findViewById(R.id.fullNameView)
            univ = itemView.findViewById(R.id.univView)
            faculty = itemView.findViewById(R.id.facultyView)
            role = itemView.findViewById(R.id.roleView)
            city = itemView.findViewById(R.id.cityView)
            settingsBtn = itemView.findViewById(R.id.icUserEditView)
            deleteBtn = itemView.findViewById(R.id.icUserDeleteView)

            settingsBtn.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    userListener.onEditClick(users[position])
                }
            }

            deleteBtn.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    userListener.onDeleteClick(users[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListAdapter.UserViewHolder {
        return UserViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.user_list_item, parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: UserListAdapter.UserViewHolder, position: Int) {
        val user = users[position]
        holder.fullName.text = user.fullName
        holder.univ.text = user.university
        holder.faculty.text = user.faculty
        holder.role.text = user.role
        holder.city.text  = user.city
        holder.settingsBtn.setOnClickListener { view ->
            userListener.onEditClick(users[position])
        }
        holder.deleteBtn.setOnClickListener { view ->
            userListener.onDeleteClick(users[position])
        }
    }

    override fun getItemCount(): Int {
        return users.size
    }

}