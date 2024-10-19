package com.ncs.mario.UI.MainScreen.Home.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ncs.mario.R
import com.ncs.mario.databinding.ItemBannerBinding

class BannerAdapter(private val banners: List<Int>) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    inner class BannerViewHolder(private val binding: ItemBannerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(bannerImg: Int) {
            Glide.with(binding.root.context).load(bannerImg).placeholder(R.drawable.placeholder_image).into(binding.bannerImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val binding = ItemBannerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BannerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        val actualPosition = position % banners.size // Circular logic
        holder.bind(banners[actualPosition])
    }

    override fun getItemCount(): Int {
        // Large enough to simulate infinite scrolling
        return Int.MAX_VALUE
    }
}