package com.ncs.mario.UI.MainScreen.Home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ncs.mario.Domain.Models.EVENTS.Event
import com.ncs.mario.databinding.EventsItemViewBinding


class EventsAdapter(private var events: List<Int>, private val callback: Callback) :
    RecyclerView.Adapter<EventsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = EventsItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event=events[position]



    }

    inner class ViewHolder(val binding: EventsItemViewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemCount() = events.size

    interface Callback{
        fun onClick(event: Event)
    }
}
