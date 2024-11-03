package com.ncs.mario.Domain.Models.Events

data class Event(
    val createdAt: Long,
    val points:Int,
    val enrolled: List<String>,
    val time: String,
    val _id: String,
    val image: String,
    val domain: List<String>,
    val title: String,
    val description: String,
    val registrationLink: String,
    val venue: String,
    val enrolledCount: Int,
    val eligibility:String,
    val isEligibile:Boolean
)
