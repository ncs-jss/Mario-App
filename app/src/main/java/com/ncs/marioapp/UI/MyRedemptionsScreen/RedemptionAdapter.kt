package com.ncs.marioapp.UI.MainScreen.Score

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ncs.marioapp.Domain.Models.MyOrderData
import com.ncs.marioapp.Domain.Models.OrderStatus
import com.ncs.marioapp.R
import com.ncs.marioapp.databinding.RedemptionItemBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Locale.getDefault

class RedemptionAdapter(private val listener: OnOrderClickListener) :
    ListAdapter<MyOrderData, RedemptionAdapter.RedemptionViewHolder>(ComparatorDiffUtil()) {

    interface OnOrderClickListener {
        fun onOrderClick(order: MyOrderData)
    }

    class RedemptionViewHolder(private val binding: RedemptionItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(order: MyOrderData, listener: OnOrderClickListener) {
            binding.eventTitle.text = order.name
            binding.eventDate.text = formatDate(order.createdAt)
            binding.status.text = "${order.status.name.lowercase(getDefault()).capitalize()}"

            Glide.with(binding.root.context)
                .load(order.image)
                .placeholder(R.drawable.profile_pic_placeholder)
                .into(binding.itemIcon)

            binding.root.setOnClickListener {
                listener.onOrderClick(order)
            }
            when(order.status){
                OrderStatus.PENDING ->{
                }
                OrderStatus.FULFILLED ->{
                    binding.statusBg.setBackgroundResource(R.drawable.status_success)
                }
                OrderStatus.REFUND -> {
                    binding.statusBg.setBackgroundResource(R.drawable.status_success)
                }
                OrderStatus.CANCELLED -> {
                    binding.statusBg.setBackgroundResource(R.drawable.status_failure)
                }
            }
        }


        private fun formatDate(timestamp: Long): String {
            val date = Date(timestamp)
            val sdf = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
            return sdf.format(date)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RedemptionViewHolder {
        val binding = RedemptionItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RedemptionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RedemptionViewHolder, position: Int) {
        if (position < itemCount) {
            holder.bind(getItem(position), listener)
        }
    }

    class ComparatorDiffUtil : DiffUtil.ItemCallback<MyOrderData>() {
        override fun areItemsTheSame(oldItem: MyOrderData, newItem: MyOrderData): Boolean {
            return oldItem._id == newItem._id
        }

        override fun areContentsTheSame(oldItem: MyOrderData, newItem: MyOrderData): Boolean {
            return oldItem == newItem
        }
    }
}
