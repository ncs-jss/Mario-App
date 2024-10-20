package com.ncs.mario.Domain.Models.EVENTS

import java.util.Date

data class Event(
    val createdAt: Long,
    val points:Int?=20,
    val enrolled: List<String>,
    val time: String?,
    val _id: Any,
    val image: String,
    val domain: List<String>,
    val title: String,
    val description: String,
    val registrationLink: String? = null,
    val venue: String? = null,
    val enrolledCount: Int

)
