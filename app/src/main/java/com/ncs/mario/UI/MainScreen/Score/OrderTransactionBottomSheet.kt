package com.ncs.mario.UI.MainScreen.Score

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ncs.mario.Domain.Models.MyOrderData
import com.ncs.mario.Domain.Models.OrderStatus
import com.ncs.mario.Domain.Utility.ExtensionsUtil.gone
import com.ncs.mario.Domain.Utility.ExtensionsUtil.isNull
import com.ncs.mario.Domain.Utility.ExtensionsUtil.visible
import com.ncs.mario.R
import com.ncs.mario.databinding.BottomsheetRedemptionBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class OrderTransactionBottomSheet(val id:String):BottomSheetDialogFragment() {

    private val viewModel: ScoreViewModel by activityViewModels()
    private lateinit var binding: BottomsheetRedemptionBinding

    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomsheetRedemptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.orderDetailsShimmerLayout.apply {
            startShimmer()
            visibility = View.VISIBLE
        }
        binding.orderDetailsView.gone()
        observeViewModel()
    }
    private fun observeViewModel(){
        viewModel.merchResponse.observe(viewLifecycleOwner) {
            if (!it.isNull){
                Log.d("merch", id)
                Log.d("merch", it.toString())
                val merch=it.orders!!.find { it._id==id }
                if (merch!=null){
                    populateOrderDetails(merch)
                }
            }
            else{
                viewModel.getMyMerch()
            }
        }
    }

    private fun populateOrderDetails(order: MyOrderData) {
        binding.orderDetailsShimmerLayout.apply {
            stopShimmer()
            visibility = View.GONE
        }
        binding.orderDetailsView.visible()

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
    fun formatLongToDate(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
        val date = Date(timestamp)
        return dateFormat.format(date)
    }


}
