package com.ncs.marioapp.Domain.Models.Events.EventDetails

data class EventDetailsResponse(
    val event: EventDetails,
    val message: String,
    val success: Boolean
)