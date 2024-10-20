package com.ncs.mario.Domain.Models.Events

data class GetEvents(
    val success: Boolean,
    val message: String,
    val events: List<Event>
)
