package com.ncs.marioapp.Domain.Models.Events

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Event(
    val createdAt: Long = 0L,
    val points:Int = 0,
    val enrolled: List<String> = emptyList(),
    val time: String = "",
    val _id: String= "",
    val image: String= "",
    val domain: List<String> = emptyList(),
    val title: String="",
    val description: String="",
    val registrationLink: String="",
    val venue: String="",
    val enrolledCount: Int=0,
    val eligibility:String="",
    val isEligibile:Boolean=true
) : Parcelable
