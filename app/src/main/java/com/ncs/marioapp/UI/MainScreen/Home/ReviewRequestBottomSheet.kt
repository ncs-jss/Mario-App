package com.ncs.marioapp.UI.MainScreen.Home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ncorti.slidetoact.SlideToActView
import com.ncs.marioapp.Domain.HelperClasses.ReviewPreferenceManager
import com.ncs.marioapp.Domain.Models.Events.Event
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.gone
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.load
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.visible
import com.ncs.marioapp.R
import com.ncs.marioapp.databinding.EventActionBottomSheetBinding
import com.ncs.marioapp.databinding.ReviewRequestBottomSheetBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class ReviewRequestBottomSheet(): BottomSheetDialogFragment() {
    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = ReviewRequestBottomSheetBinding.inflate(inflater, container, false)
        populateViews(binding)
        return binding.root
    }

    private fun populateViews(binding: ReviewRequestBottomSheetBinding) {
        val reviewPrefManager = ReviewPreferenceManager(requireContext())
        binding.btnReview.setOnClickThrottleBounceListener {
            reviewPrefManager.setUserReviewed()
            openUrl("https://play.google.com/store/apps/details?id=com.ncs.marioapp")
            dismiss()
        }
        binding.btnAskMeLater.setOnClickThrottleBounceListener {
            reviewPrefManager.setLastReviewPrompt()
            dismiss()
        }
    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}