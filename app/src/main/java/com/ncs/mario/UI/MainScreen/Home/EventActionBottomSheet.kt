package com.ncs.mario.UI.MainScreen.Home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ncorti.slidetoact.SlideToActView
import com.ncs.mario.Domain.Models.Events.Event
import com.ncs.mario.Domain.Utility.ExtensionsUtil.gone
import com.ncs.mario.Domain.Utility.ExtensionsUtil.load
import com.ncs.mario.Domain.Utility.ExtensionsUtil.visible
import com.ncs.mario.R
import com.ncs.mario.databinding.EventActionBottomSheetBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class EventActionBottomSheet(val event: Event, val type:String): BottomSheetDialogFragment() {


    private val viewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = EventActionBottomSheetBinding.inflate(inflater, container, false)
        populateEventDetails(binding)
        return binding.root
    }

    private fun populateEventDetails(binding: EventActionBottomSheetBinding) {

        if (type=="Enroll"){
            binding.title.text="Enroll"
            binding.confirmButtonEnroll.visible()
            binding.confirmButtonUnenroll.gone()
        }
        else{
            binding.title.text="Unenroll"
            binding.confirmButtonUnenroll.visible()
            binding.confirmButtonEnroll.gone()
        }

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

        binding.confirmButtonEnroll.onSlideCompleteListener = object : SlideToActView.OnSlideCompleteListener {
            override fun onSlideComplete(view: SlideToActView) {
                viewModel.enrollUser(event._id)
                dismiss()
            }
        }

        binding.confirmButtonUnenroll.onSlideCompleteListener = object : SlideToActView.OnSlideCompleteListener {
            override fun onSlideComplete(view: SlideToActView) {
                viewModel.unenrollUser(event._id)
                dismiss()
            }
        }

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