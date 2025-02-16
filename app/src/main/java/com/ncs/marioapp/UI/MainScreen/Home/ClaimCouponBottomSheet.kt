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
import com.ncs.marioapp.databinding.ClaimCouponBottomSheetBinding
import com.ncs.marioapp.databinding.EventActionBottomSheetBinding
import com.ncs.marioapp.databinding.ReviewRequestBottomSheetBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class ClaimCouponBottomSheet(val couponCode:String, val callback: Callback): BottomSheetDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = ClaimCouponBottomSheetBinding.inflate(inflater, container, false)
        populateViews(binding)
        return binding.root
    }

    private fun populateViews(binding: ClaimCouponBottomSheetBinding) {
        binding.btnLetGo.setOnClickThrottleBounceListener {
            dismiss()
        }
        binding.btnClaim.setOnClickThrottleBounceListener {
            callback.onClaimCLick(couponCode)
            dismiss()
        }
    }

    interface Callback {
        fun onClaimCLick(couponCode: String)
    }

}