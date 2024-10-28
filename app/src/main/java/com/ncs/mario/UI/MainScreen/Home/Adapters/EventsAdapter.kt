package com.ncs.mario.UI.MainScreen.Home.Adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ncs.mario.Domain.Models.Events.Event
import com.ncs.mario.Domain.Models.Events.ParticipatedEvent
import com.ncs.mario.Domain.Utility.ExtensionsUtil.gone
import com.ncs.mario.Domain.Utility.ExtensionsUtil.load
import com.ncs.mario.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.mario.Domain.Utility.ExtensionsUtil.visible
import com.ncs.mario.R
import com.ncs.mario.databinding.EventsItemViewBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Random


class EventsAdapter(private var events: List<Event>, private val callback: Callback) :
    RecyclerView.Adapter<EventsAdapter.ViewHolder>() {

        val userEvents= mutableListOf<ParticipatedEvent>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = EventsItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event=events[position]
        holder.binding.title.text=event.title
        holder.binding.desc.text=event.description
        holder.binding.image.load(event.image,holder.binding.image.context.getDrawable(R.drawable.ncs)!!)
        holder.binding.venue.text=event.venue

        val isEnrolled = userEvents.any { it._id == event._id }

        if (isEnrolled) {
            holder.binding.btnEnroll.text = "Enrolled!"
            holder.binding.btnEnrollMe.setBackgroundResource(R.drawable.selected_enroll_button)
            holder.binding.claimTicketBtn.visible()
        } else {
            holder.binding.btnEnroll.text = "Enroll"
            holder.binding.btnEnrollMe.setBackgroundResource(R.drawable.enroll_button)
            holder.binding.claimTicketBtn.gone()
        }

        holder.binding.claimTicketBtn.setOnClickThrottleBounceListener {
            callback.onGetTicketClick(event)
        }

        if (event.time.isNullOrEmpty()){
            holder.binding.time.text="TBA"
            holder.binding.date.text="TBA"
        }
        else{
            val (formattedDate, formattedTime) = formatTimestamp(event.time.toLong())
            holder.binding.time.text=formattedTime
            holder.binding.date.text=formattedDate
        }

        if (event.enrolled.isEmpty()){
            holder.binding.profilePic1.setImageResource(R.drawable.apphd)
            holder.binding.profilePic2.setImageResource(R.drawable.logo)
            holder.binding.profilePic3.setImageResource(R.drawable.ncs)
        }
        else if (event.enrolled.size==1){
            holder.binding.profilePic1.setImageResource(R.drawable.apphd)
            holder.binding.profilePic2.setImageResource(R.drawable.logo)
            holder.binding.profilePic3.load(event.enrolled[0],holder.binding.image.context.getDrawable(R.drawable.profile_pic_placeholder)!!)
        }
        else if (event.enrolled.size==2){
            holder.binding.profilePic1.setImageResource(R.drawable.apphd)
            holder.binding.profilePic2.load(event.enrolled[1],holder.binding.image.context.getDrawable(R.drawable.profile_pic_placeholder)!!)
            holder.binding.profilePic3.load(event.enrolled[0],holder.binding.image.context.getDrawable(R.drawable.profile_pic_placeholder)!!)
        }
        else{
            holder.binding.profilePic1.load(event.enrolled[2],holder.binding.image.context.getDrawable(R.drawable.profile_pic_placeholder)!!)
            holder.binding.profilePic2.load(event.enrolled[1],holder.binding.image.context.getDrawable(R.drawable.profile_pic_placeholder)!!)
            holder.binding.profilePic3.load(event.enrolled[0],holder.binding.image.context.getDrawable(R.drawable.profile_pic_placeholder)!!)
        }

        holder.binding.enrolledCount.text="${getInflatedEnrolledUserCount(event.enrolledCount)} +"

        holder.binding.btnEnrollMe.setOnClickListener{
            if (holder.binding.btnEnroll.text=="Enroll"){
                callback.onClick(event,false)
            }
            else{
                callback.onClick(event,true)
            }
        }
    }

    fun userEvents(events: List<ParticipatedEvent>){
        Log.d("checkkk","$events")
        userEvents.clear()
        userEvents.addAll(events)
        notifyDataSetChanged()
    }

    fun addToUserEvents(event: ParticipatedEvent){
        userEvents.add(event)
        notifyDataSetChanged()
    }

    fun removeFromUserEvents(id:String){
        val ev=events.first { it._id==id }
        userEvents.remove(ParticipatedEvent(
            createdAt=ev.createdAt ,
            points=ev.points,
            enrolled=ev.enrolled,
            time=ev.time,
            _id=ev._id,
            image=ev.image,
            domain=ev.domain,
            title=ev.title,
            description=ev.description,
            registrationLink=ev.registrationLink,
            venue=ev.venue,
            enrolledCount=ev.enrolledCount
        ))
        notifyDataSetChanged()
    }

    fun getInflatedEnrolledUserCount(count:Int):Int{
        return if (count==0) Random().nextInt(5)+1 else (0.2 * count).toInt()+count
    }

    fun formatTimestamp(timestamp: Long): Pair<String, String> {
        val date = Date(timestamp)
        val dateFormat = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val formattedDate = dateFormat.format(date)
        val formattedTime = timeFormat.format(date)
        return Pair(formattedDate, formattedTime)
    }

    inner class ViewHolder(val binding: EventsItemViewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemCount() = events.size

    interface Callback{
        fun onClick(event: Event, isEnrolled:Boolean)
        fun onGetTicketClick(event: Event)
    }
}
