package com.ncs.mario.UI.MainScreen.Score

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ncs.mario.Domain.Models.EVENTS.ParticipatedEvent
import com.ncs.mario.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.mario.R
import com.ncs.mario.databinding.ItemPastEventBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PastEventAdapter :ListAdapter<ParticipatedEvent, PastEventAdapter.PastEventViewHolder>(ComparatorDiffUtil()) {

    class PastEventViewHolder(private val binding: ItemPastEventBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(event: ParticipatedEvent) {
            binding.eventTitle.text = event.title
            binding.venue.text = event.venue ?: "Computer Center, AB-1"

            binding.eventDate.text = formatDateString(event.time)

            binding.points.text = event.points.toString()

            binding.root.setOnClickThrottleBounceListener{}

            Glide.with(binding.root.context)
                .load(event.image)
                .placeholder(R.drawable.profile_pic_placeholder)
                .into(binding.itemIcon)
        }
        fun formatDateString(dateString: String?): String {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
            return try {
                val date: Date = inputFormat.parse(dateString) ?: return "TBD"
                outputFormat.format(date)
            } catch (e: Exception) {
                "TBD"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PastEventViewHolder {
        val binding = ItemPastEventBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PastEventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PastEventViewHolder, position: Int) {
        if (position < itemCount) {
            holder.bind(getItem(position))
        }
    }

    class ComparatorDiffUtil : DiffUtil.ItemCallback<ParticipatedEvent>() {
        override fun areItemsTheSame(oldItem: ParticipatedEvent, newItem: ParticipatedEvent): Boolean {
            return oldItem._id == newItem._id
        }
        override fun areContentsTheSame(oldItem: ParticipatedEvent, newItem: ParticipatedEvent): Boolean {
            return oldItem == newItem
        }
    }
}