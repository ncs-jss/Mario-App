package com.ncs.marioapp.UI.EventDetailsScreen.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ncs.marioapp.Domain.Models.Events.EventDetails.Mentor
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.load
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.marioapp.R
import com.ncs.marioapp.databinding.SpeakerRvItemBinding


class TeamAdapter(private var mentors: List<Mentor>, private val callback: TeamAdapterCallback, private val type:String) :
    RecyclerView.Adapter<TeamAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SpeakerRvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mentor=mentors[position]
        holder.binding.speakerImage.load(mentor.img_url, holder.itemView.context.getDrawable(R.drawable.profile_pic_placeholder)!!)
        holder.binding.speakerName.text=mentor.name

        holder.binding.root.setOnClickThrottleBounceListener{
            callback.onTeamMemberClicked(mentor, type)
        }
    }

    inner class ViewHolder(val binding: SpeakerRvItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemCount() = mentors.size

    interface TeamAdapterCallback{
        fun onTeamMemberClicked(mentor: Mentor, type: String)
    }

}
