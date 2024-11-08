package com.ncs.marioapp.UI.MainScreen.Home.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ncs.marioapp.Domain.Models.Banner
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.load
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.marioapp.R
import com.ncs.marioapp.databinding.ItemBannerBinding

class BannerAdapter(private val banners: List<Banner>, private val callback:BannerAdapter.Callback) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    inner class BannerViewHolder(private val binding: ItemBannerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(bannerImg: Banner) {
            binding.bannerImage.load(bannerImg.image,binding.root.context.getDrawable(R.drawable.placeholder_image)!!)
            binding.root.setOnClickThrottleBounceListener{
                callback.onBannerClick(bannerImg)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val binding = ItemBannerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BannerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        val actualPosition = position % banners.size
        holder.bind(banners[actualPosition])
    }

    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }

    interface Callback{
        fun onBannerClick(banner: Banner)
    }
}