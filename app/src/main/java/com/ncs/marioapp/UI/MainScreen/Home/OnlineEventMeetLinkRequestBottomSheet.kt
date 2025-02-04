package com.ncs.marioapp.UI.MainScreen.Home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ncorti.slidetoact.SlideToActView
import com.ncs.marioapp.Domain.HelperClasses.PrefManager
import com.ncs.marioapp.Domain.Models.Events.Event
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.gone
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.load
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.visible
import com.ncs.marioapp.R
import com.ncs.marioapp.databinding.EventActionBottomSheetBinding
import com.ncs.marioapp.databinding.OnlineEventMeetLinkRequestBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class OnlineEventMeetLinkRequestBottomSheet(): BottomSheetDialogFragment() {
    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }

    private val viewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = OnlineEventMeetLinkRequestBinding.inflate(inflater, container, false)
        populateEventDetails(binding)
        return binding.root
    }

    private fun populateEventDetails(binding: OnlineEventMeetLinkRequestBinding) {

        binding.email.text=PrefManager.getUserSignUpEmail()
        binding.closeBtn.setOnClickThrottleBounceListener {
            dismiss()
        }
        binding.contactWA.setOnClickThrottleBounceListener {
            openWhatsAppChat("917017305615")
        }

    }


    fun openWhatsAppChat(phoneNumber: String) {
        val formattedNumber = "https://wa.me/$phoneNumber"

        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(formattedNumber)
                setPackage("com.whatsapp")
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "WhatsApp is not installed", Toast.LENGTH_SHORT).show()
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