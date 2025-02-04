package com.ncs.marioapp.UI.MainScreen.Score

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ncs.marioapp.Domain.Models.Events.ParticipatedEvent
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.gone
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.visible
import com.ncs.marioapp.R
import com.ncs.marioapp.databinding.ItemPastEventBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PastEventAdapter(private val callback: Callback) :ListAdapter<ParticipatedEvent, PastEventAdapter.PastEventViewHolder>(ComparatorDiffUtil()) {

    class PastEventViewHolder(private val binding: ItemPastEventBinding, private val callback: Callback) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(event: ParticipatedEvent) {
            binding.eventTitle.text = event.title
            binding.venue.text = event.venue ?: "Computer Center, AB-1"

            binding.eventDate.text = formatDateString(event.time)

            Log.d("checkDate", event.time.toString())

            binding.points.text = "+ ${event.points.toString()}"

            binding.coins.text = "+ ${((event.points!!)/5).toInt().toString()}"

            binding.root.setOnClickThrottleBounceListener{
                callback.onClickListener(event)
            }

            Glide.with(binding.root.context)
                .load(event.image)
                .placeholder(R.drawable.profile_pic_placeholder)
                .into(binding.itemIcon)

            if (event.attended){
                binding.coinsView.visible()
            }
            else{
                binding.coinsView.gone()
            }



        }


        fun formatDateString(dateString: String?): String {
            if (dateString == "TBA") {
                return "TBA"
            }

            return try {
                val timestamp = dateString?.toLongOrNull()
                if (timestamp != null) {
                    val outputFormat = SimpleDateFormat("dd MMM, yy | hh:mm a", Locale.getDefault())
                    val date = Date(timestamp)
                    return outputFormat.format(date)
                }

                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date: Date = inputFormat.parse(dateString) ?: return "TBA"
                val outputFormat = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
                outputFormat.format(date)
            } catch (e: Exception) {
                "TBA"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PastEventViewHolder {
        val binding = ItemPastEventBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PastEventViewHolder(binding, callback)
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

    interface Callback{
        fun onClickListener(event: ParticipatedEvent)
    }
}