package com.ncs.mario.UI.MyRedemptionsScreen

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ncs.mario.Domain.Models.MyOrderData
import com.ncs.mario.Domain.Models.OrderStatus
import com.ncs.mario.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.mario.R
import com.ncs.mario.UI.SurveyScreen.BottomSheet
import com.ncs.mario.databinding.BottomsheetRedemptionBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderDetailsBottomSheet:BottomSheetDialogFragment() {
    private lateinit var order:MyOrderData
    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = BottomsheetRedemptionBinding.inflate(inflater, container, false)
        populateOrderDetails(binding)
        return binding.root
    }

    private fun populateOrderDetails(binding: BottomsheetRedemptionBinding) {
        binding.contactWA.setOnClickThrottleBounceListener{
            openWhatsAppChat("917017305615", message = "My order id: ${order._id} \n\n")
        }
        binding.productName.text = order.name
        binding.points.text = order.cost.toString()
        binding.orderId.text = "Order ID: ${order._id}"
        binding.date.text = "Placed on: ${formatLongToDate(order.createdAt)}"
        Glide.with(binding.root).load(order.image).placeholder(R.drawable.profile_pic_placeholder).into(binding.itemIcon)
        when(order.status){
            OrderStatus.PENDING -> {}
            OrderStatus.FULFILLED -> {
                binding.completeTV.text = "FULFILLED"
                binding.progressView.setImageResource(R.drawable.outline_check_circle_outline_24)
                binding.completeView.setImageResource(R.drawable.outline_check_circle_outline_24)
            }
            OrderStatus.REFUND -> {
                binding.completeTV.text = "REFUNDED"
                binding.progressView.setImageResource(R.drawable.outline_check_circle_outline_24)
                binding.completeView.setImageResource(R.drawable.outline_check_circle_outline_24)
            }
            OrderStatus.CANCELLED -> {
                binding.completeTV.text = "CANCELLED"
                binding.progressView.setImageResource(R.drawable.outline_check_circle_outline_24)
                binding.completeView.setImageResource(R.drawable.outline_check_circle_outline_24)
            }
        }
    }

    fun openWhatsAppChat(phoneNumber: String, message: String) {
        val formattedNumber = "https://wa.me/$phoneNumber?text=${Uri.encode(message)}"

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


    fun formatLongToDate(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
        val date = Date(timestamp)
        return dateFormat.format(date)
    }

    companion object {
        fun newInstance(order: MyOrderData): OrderDetailsBottomSheet {
            val fragment = OrderDetailsBottomSheet()
            fragment.order = order
            return fragment
        }
    }
}
