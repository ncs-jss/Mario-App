package com.ncs.marioapp.UI.EventDetailsScreen.Adapters

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.ncs.marioapp.Domain.HelperClasses.PrefManager
import com.ncs.marioapp.Domain.HelperClasses.Utils.setTextWithColorFromSubstring
import com.ncs.marioapp.Domain.HelperClasses.Utils.toRoundTimeStamp
import com.ncs.marioapp.Domain.Models.Admin.Round
import com.ncs.marioapp.Domain.Models.Events.EventDetails.Submission
import com.ncs.marioapp.Domain.Models.Events.ParticipatedEvent
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.gone
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.startBlinking
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.visible
import com.ncs.marioapp.R
import com.ncs.marioapp.databinding.ItemRoundBinding

class RoundAdapter(private val rounds: List<Round>, private val callback: RoundAdapterCallback, private val userSubmission: List<Submission>) :
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

                if (round.live) {
                    roundStatus.visible()
                    roundStatus.startBlinking()
                } else {
                    roundStatus.gone()
                }

                if (round.requireSubmission) {
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
                    formButton.text="Submitted"
                } else {

                    val userType= PrefManager.getUserProfileForCache()

                    val deadline = if(userType?.admitted_to=="COLLEGE" || userType?.admitted_to=="") round.timeLine["endCollege"] else round.timeLine["endUniversity"]
                    val isEventOver: Boolean

                    Timestamp.now().let { currentTimestamp ->
                        val currentTime = currentTimestamp.seconds * 1000
                        isEventOver = currentTime >= deadline!!
                    }
                    if (isEventOver) {
                        formButton.background.setTint(itemView.context.getColor(R.color.account))
                        formButton.isClickable = false
                        formButton.isEnabled = false
                        formButton.text="Deadline Passed"
                    }
                    else{
                        formButton.background.setTint(itemView.context.getColor(R.color.button_bg))
                        formButton.isClickable = true
                        formButton.isEnabled = true
                        binding.formButton.text = round.submissionButtonText
                    }
                }


                binding.formButton.setOnClickThrottleBounceListener {
                    callback.onFormButtonClick(round)
                }
            }
        }
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

