package com.ncs.marioapp.Domain.Models.Events

data class PollResponse(
    val message: String,
    val polls: List<Poll>,
    val success: Boolean
)