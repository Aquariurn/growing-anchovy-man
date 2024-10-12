package com.example.growinganchovyman

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PresetAdapter(private val presets: List<Preset>) :
    RecyclerView.Adapter<PresetAdapter.PresetViewHolder>(){
        class PresetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
            val genreTextView: TextView = itemView.findViewById(R.id.titleTextView)
            val runtimeTextView: TextView = itemView.findViewById(R.id.runtimeTextView)
            val actImageView: ImageView = itemView.findViewById(R.id.actImageView)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PresetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.preset_recycler, parent, false)
        return PresetViewHolder(view)
    }

    override fun getItemCount(): Int {
        return presets.size
    }

    override fun onBindViewHolder(holder: PresetViewHolder, position: Int) {
        val preset = presets[position]
        holder.titleTextView.text = preset.title
        holder.genreTextView.text = preset.genre
        holder.runtimeTextView.text = preset.runTime
        holder.actImageView.setImageResource(preset.presetID)
    }
}