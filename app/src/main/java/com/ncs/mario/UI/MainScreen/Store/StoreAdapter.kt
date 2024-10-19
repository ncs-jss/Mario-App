package com.ncs.mario.UI.MainScreen.Store

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ncs.mario.Domain.HelperClasses.PrefManager
import com.ncs.mario.Domain.Models.Merch
import com.ncs.mario.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.mario.R
import com.ncs.mario.databinding.MerchItemBinding

class StoreAdapter(private var items: List<Merch>, private val onRedeemClick: (Merch) -> Unit) :
    RecyclerView.Adapter<StoreAdapter.StoreViewHolder>() {

    class StoreViewHolder(val binding: MerchItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder {
        val binding = MerchItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StoreViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        val item = items[position]

        with(holder.binding) {
            Glide.with(root).load(item.image).error(R.drawable.placeholder_image).into(contentIcon)
            itemName.text = item.name
            points.text = item.cost.toString()

            if (PrefManager.getUserProfile()!!.points >= item.cost) {
                lockLayout.setBackgroundResource(R.drawable.filled_primary_box)
                lockText.text = "Redeem"
                lockImage.visibility = View.GONE

                root.setOnClickThrottleBounceListener {
                    onRedeemClick(item)
                }
            } else {
                root.setOnClickThrottleBounceListener {
                    Toast.makeText(root.context, "You don't have enough points!!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<Merch>) {
        this.items = newItems
        notifyDataSetChanged()
    }
}
