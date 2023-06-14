package vsu.cs.univtimetable.screens.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import vsu.cs.univtimetable.R
import vsu.cs.univtimetable.dto.UnivDto


interface OnUnivItemClickListener {
    fun onEditClick(univ: UnivDto)
    fun onDeleteClick(univ: UnivDto)

    fun onItemClick(id: Int)
}

class UnivListAdapter(
    var context: Context,
    var univs: List<UnivDto>,
    val listener: OnUnivItemClickListener
) :
    RecyclerView.Adapter<UnivListAdapter.UnivViewHolder>() {
    inner class UnivViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var univNameView: TextView
        var cityNameView: TextView
        var settingsBtn: ImageView
        var deleteBtn: ImageView

        init {
            itemView.setOnClickListener {
                listener.onItemClick(univs[adapterPosition].id.toInt())
            }

            univNameView = itemView.findViewById(R.id.univNameView)
            cityNameView = itemView.findViewById(R.id.cityNameView)
            settingsBtn = itemView.findViewById(R.id.icSettingsView)
            deleteBtn = itemView.findViewById(R.id.icDeleteView)

            settingsBtn.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onEditClick(univs[position])
                }
            }

            deleteBtn.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onDeleteClick(univs[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnivViewHolder {
        return UnivViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.univs_list_item, parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: UnivViewHolder, position: Int) {
        val obj = univs[position]
        holder.univNameView.text = abbreviateUniversityName(obj.universityName).toUpperCase()
        holder.cityNameView.text = obj.city
        holder.settingsBtn.setOnClickListener { view ->
            listener.onEditClick(univs[position])
        }
        holder.deleteBtn.setOnClickListener { view ->
            listener.onDeleteClick(univs[position])
        }
    }


    override fun getItemCount(): Int {
        return univs.size
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