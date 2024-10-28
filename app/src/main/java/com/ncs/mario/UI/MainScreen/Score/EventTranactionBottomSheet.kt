package com.ncs.mario.UI.MainScreen.Score

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ncs.mario.Domain.Models.Events.ParticipatedEvent
import com.ncs.mario.Domain.Utility.ExtensionsUtil.load
import com.ncs.mario.R
import com.ncs.mario.databinding.EventTransactionBottomSheetBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class EventTranactionBottomSheet(val event: ParticipatedEvent): BottomSheetDialogFragment() {

    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = EventTransactionBottomSheetBinding.inflate(inflater, container, false)
        populateEventDetails(binding)
        return binding.root
    }

    private fun populateEventDetails(binding: EventTransactionBottomSheetBinding) {

        binding.eventIc.load(event.image, requireContext().getDrawable(R.drawable.placeholder_image)!!)

        binding.eventName.text=event.title
        binding.eventDesc.text=event.description

        if (event.time.isNullOrEmpty()){
            binding.time.text="TBA"
            binding.date.text="TBA"
        }
        else{
            val (formattedDate, formattedTime) = formatTimestamp(event.time.toLong())
            binding.time.text=formattedTime
            binding.date.text=formattedDate
        }

        binding.venue.text=event.venue


    }

    fun formatTimestamp(timestamp: Long): Pair<String, String> {
        val date = Date(timestamp)
        val dateFormat = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val formattedDate = dateFormat.format(date)
        val formattedTime = timeFormat.format(date)
        return Pair(formattedDate, formattedTime)
    }


}