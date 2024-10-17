package com.ncs.mario.Domain.Models.EVENTS

import java.util.Date

data class Event(
    val _id:String,
    val domain:String,
    val image:Photo,
    val registration_link:String,
    val time: Date,
    val venue: String

)
