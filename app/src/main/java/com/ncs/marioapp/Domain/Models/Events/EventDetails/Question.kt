package com.ncs.marioapp.Domain.Models.Events.EventDetails

data class Question(
    val options: List<Any>,
    val question: String,
    val type: String
)