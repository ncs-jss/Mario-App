package com.ncs.mario.Domain.Models.EVENTS

data class GetEvents(
    val success: Boolean,
    val message: String,
    val event: List<Event>
)
