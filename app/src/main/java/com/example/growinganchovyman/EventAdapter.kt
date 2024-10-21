package com.example.growinganchovyman

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class EventAdapter(private val events: List<String>) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    private val colors = listOf(
        Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.MAGENTA
    )

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventName: TextView = itemView.findViewById(R.id.event_name)
        val colorBar: View = itemView.findViewById(R.id.color_bar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]

        // 이벤트 이름 설정
        holder.eventName.text = event

        // 각 이벤트마다 다른 색깔 바 설정
        holder.colorBar.setBackgroundColor(colors[position % colors.size])
    }

    override fun getItemCount(): Int {
        return events.size
    }
}