package com.ncs.marioapp.UI.MainScreen.Home.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.ncs.marioapp.Domain.Models.NCSApps
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.marioapp.R
import com.ncs.marioapp.databinding.NcsAppsItemBinding

class NCSAppsAdapter(private var ncsApps: List<NCSApps>, private val callback: Callback) :
    RecyclerView.Adapter<NCSAppsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = NcsAppsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ncsApp = ncsApps[position]
        holder.bind(ncsApp)
    }

    override fun getItemCount() = ncsApps.size

    inner class ViewHolder(private val binding: NcsAppsItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private var isExpanded = false

        fun bind(ncsApp: NCSApps) {
            binding.appTitle.text = ncsApp.appName
            binding.appDesc.text = ncsApp.appDescription
            binding.appIc.setImageDrawable(ncsApp.appIcon)

            binding.ratingCount.text=ncsApp.appRating.toString()
            setStars(ncsApp.appRating)

            binding.root.setOnClickListener {
                isExpanded = !isExpanded
                animateArrow(isExpanded)
                binding.hiddenLayout.visibility = if (isExpanded) View.VISIBLE else View.GONE
            }

            binding.btnDownload.setOnClickThrottleBounceListener{
                callback.onClick(ncsApp)
            }
        }

        private fun setStars(rating: Double) {
            val fullStars = rating.toInt()
            val hasHalfStar = rating % 1 != 0.0

            val stars = listOf(
                binding.star1, binding.star2, binding.star3,
                binding.star4, binding.star5
            )

            for (i in stars.indices) {
                when {
                    i < fullStars -> stars[i].setImageResource(R.drawable.baseline_star_24)
                    i == fullStars && hasHalfStar -> stars[i].setImageResource(R.drawable.baseline_star_half_24)
                    else -> stars[i].setImageResource(R.drawable.baseline_star_outline_24)
                }
                stars[i].setColorFilter(binding.root.context.getColor(R.color.green))
            }
        }


        private fun animateArrow(expand: Boolean) {
            val fromRotation = if (expand) 0f else 180f
            val toRotation = if (expand) 180f else 0f
            val rotateAnim = RotateAnimation(
                fromRotation, toRotation,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
            )
            rotateAnim.duration = 300
            rotateAnim.fillAfter = true
            binding.arrow.startAnimation(rotateAnim)
        }
    }

    interface Callback {
        fun onClick(ncsApps: NCSApps)
    }
}
