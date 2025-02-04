package com.ncs.marioapp.Domain.Models

data class EventMeetInvite(
    val mail_type: String = "",
    val email: String = "",
    val user_name : String = "",
    val event_title: String = "",
    val date_time:String = "",
    val venue:String="",
    val link:String = "",
)