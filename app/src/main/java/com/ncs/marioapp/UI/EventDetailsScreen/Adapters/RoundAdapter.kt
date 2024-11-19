package com.ncs.marioapp.UI.EventDetailsScreen.Adapters

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ncs.marioapp.Domain.HelperClasses.Utils
import com.ncs.marioapp.Domain.HelperClasses.Utils.setTextWithColorFromSubstring
import com.ncs.marioapp.Domain.HelperClasses.Utils.toRoundTimeStamp
import com.ncs.marioapp.Domain.Models.Admin.Round
import com.ncs.marioapp.Domain.Models.Events.EventDetails.Submission
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.gone
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.startBlinking
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.visible
import com.ncs.marioapp.R
import com.ncs.marioapp.databinding.ItemRoundBinding

class RoundAdapter(
    private val rounds: List<Round>,
    private val callback: RoundAdapterCallback,
    private val userSubmission: List<Submission>,
    private var isUserEnrolled: Int = -1,
    private var currentTime: String?
) :
    RecyclerView.Adapter<RoundAdapter.RoundViewHolder>() {

    private var hasRoundFailure = false

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

                if (round.live) {
                    roundStatus.visible()
                    roundStatus.startBlinking()
                } else {
                    roundStatus.gone()
                }

                if (round.requireSubmission && isUserEnrolled != 0) {
                    formButton.visible()
                } else {
                    formButton.gone()
                }

                roundTitle.text = round.roundTitle
                roundDescription.text = round.description
                binding.venueDetail.text = "Venue: ${round.venue}"
                binding.formButton.text = round.submissionButtonText

                startTime.setTextWithColorFromSubstring(":", Color.WHITE)
                endTime.setTextWithColorFromSubstring(":", Color.WHITE)
                venueDetail.setTextWithColorFromSubstring(":", Color.WHITE)

                binding.timelineIndicatorAbove.visibility =
                    if (position == 0) View.INVISIBLE else View.VISIBLE
                binding.timelineIndicatorBelow.visibility =
                    if (position == rounds.size - 1) View.INVISIBLE else View.VISIBLE

                Log.d("RoundAdapter", "User Submissions: $userSubmission")

                val isSubmitted = userSubmission.any { it.roundID == round.roundID }

                if (isSubmitted) {
                    formButton.background.setTint(itemView.context.getColor(R.color.account))
                    formButton.isClickable = false
                    formButton.isEnabled = false
                    formButton.text = "Submitted"
                } else {
                    // Not Submitted

                    val deadline = round.endTime
                    val isRoundOver: Boolean

                    currentTime?.let { currentTimestamp ->
                        isRoundOver =
                            Utils.convertFullFormatTimeToTimestamp(currentTimestamp) >= Utils.convertFullFormatTimeToTimestamp(
                                deadline!!
                            )

                        if (isRoundOver) {

                            // Round Deadline over
                            formButton.background.setTint(itemView.context.getColor(R.color.account))
                            formButton.isClickable = false
                            formButton.isEnabled = false
                            formButton.text = "Deadline Passed"

                            if (!round.isOptional) {
                                hasRoundFailure = true
                            }

                        } else {

                            // Round is not over yet

                            if (!hasRoundFailure) {
                                formButton.background.setTint(itemView.context.getColor(R.color.button_bg))
                                formButton.isClickable = true
                                formButton.isEnabled = true
                                binding.formButton.text = round.submissionButtonText
                            } else {
                                formButton.background.setTint(itemView.context.getColor(R.color.account))
                                formButton.isClickable = false
                                formButton.isEnabled = false
                                binding.formButton.text = "Round Locked"
                            }

                        }

                    }

                }


                binding.formButton.setOnClickThrottleBounceListener {
                    callback.onFormButtonClick(round)
                }
            }
        }
    }

    fun setIsUserEnrolled(isEnrolled: Int) {
        this.isUserEnrolled = isEnrolled
        val positions = rounds.filter { it.requireSubmission }.map { item -> rounds.indexOf(item) }
        positions.forEach {
            notifyItemChanged(it)
        }
    }

    fun setUpdatedTime(time: String) {
        hasRoundFailure = false
        this.currentTime = time
        notifyDataSetChanged()
    }

//    fun userSubmissions(submissions: List<Submission>) {
//        Log.d("RoundAdapter", "User Submissions: $submissions")
//        userSubmission.clear()
//        userSubmission.addAll(submissions)
//        notifyDataSetChanged()
//    }

    interface RoundAdapterCallback {
        fun onFormButtonClick(round: Round)
    }
}

