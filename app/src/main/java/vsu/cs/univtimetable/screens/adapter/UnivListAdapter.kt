package vsu.cs.univtimetable.screens.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import vsu.cs.univtimetable.R
import vsu.cs.univtimetable.dto.UnivDto

class UnivListAdapter(var context: Context, var univs: List<UnivDto>):
        RecyclerView.Adapter<UnivListAdapter.UnivViewHolder>() {
    inner class UnivViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var univNameView: TextView
        var cityNameView: TextView

        init {
            univNameView = itemView.findViewById(R.id.univNameView)
            cityNameView = itemView.findViewById(R.id.cityNameView)
        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnivViewHolder {
        return UnivViewHolder(LayoutInflater.from(context).inflate(R.layout.univs_list_item, parent,
            false))
    }

    override fun onBindViewHolder(holder: UnivViewHolder, position: Int) {
        val obj = univs[position]
        holder.univNameView.text = obj.universityName
        holder.cityNameView.text = obj.city
    }


    override fun getItemCount(): Int {
        return univs.size
    }
}