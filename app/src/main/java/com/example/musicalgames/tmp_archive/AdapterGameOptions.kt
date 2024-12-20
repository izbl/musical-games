package com.example.musicalgames.tmp_archive

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.musicalgames.R
import com.example.musicalgames.games.GamePackage

class AdapterGameOptions(
    private val options: List<GamePackage>,
    private val onItemClick: (GamePackage) -> Unit
) : RecyclerView.Adapter<AdapterGameOptions.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_option, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val option = options[position]
        holder.bind(option.name)
        holder.itemView.setOnClickListener { onItemClick(option) }
    }

    override fun getItemCount(): Int = options.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val optionTextView: TextView = itemView.findViewById(R.id.optionTextView)

        fun bind(option: String) {
            optionTextView.text = option
        }
    }
}
