package com.ncs.marioapp.UI.EventDetailsScreen.Adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ncs.marioapp.Domain.HelperClasses.Utils.setTextWithColorFromSubstring
import com.ncs.marioapp.Domain.HelperClasses.Utils.toRoundTimeStamp
import com.ncs.marioapp.Domain.Models.Admin.Round
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.gone
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.visible
import com.ncs.marioapp.databinding.ItemRoundBinding

class RoundAdapter(private val rounds: List<Round>) :
    RecyclerView.Adapter<RoundAdapter.RoundViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoundViewHolder {
        val binding = ItemRoundBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RoundViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoundViewHolder, position: Int) {
        holder.bind(rounds[position], position)
    }

    override fun getItemCount(): Int = rounds.size

    inner class RoundViewHolder(private val binding: ItemRoundBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(round: Round, position: Int) {
            with(binding) {

                if (round.startTime == "TBA") {
                    dateBadge.text = round.startTime.toString()
                    startTime.text = "Start: To Be Announced"
                    endTime.text = "End: To Be Announced"
                } else {
                    dateBadge.text = round.startTime!!.toRoundTimeStamp()
                    startTime.text = "Start: ${round.startTime}"
                    endTime.text = "End: ${round.endTime}"
                }

                if (round.isLive) {
                    roundStatus.visible()
                } else {
                    roundStatus.gone()
                }

                roundTitle.text = round.roundTitle
                roundDescription.text = round.description
                binding.venueDetail.text = "Venue: ${round.venue}"
                binding.formButton.text = round.submissionButtonText

                startTime.setTextWithColorFromSubstring(":", Color.WHITE)
                endTime.setTextWithColorFromSubstring(":", Color.WHITE)
                venueDetail.setTextWithColorFromSubstring(":", Color.WHITE)

                // Handle edge cases for timeline indicators
                binding.timelineIndicatorAbove.visibility =
                    if (position == 0) View.INVISIBLE else View.VISIBLE
                binding.timelineIndicatorBelow.visibility =
                    if (position == rounds.size - 1) View.INVISIBLE else View.VISIBLE
            }
        }
    }
}
