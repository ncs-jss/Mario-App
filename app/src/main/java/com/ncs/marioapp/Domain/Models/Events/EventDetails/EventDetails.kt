package com.ncs.marioapp.Domain.Models.Events.EventDetails

data class EventDetails(
    val banner: String,
    val contact: List<Mentor>,
    val deadline: Long,
    val description: String,
    val domain: List<String>,
    val duration: String,
    val eligibility: String,
    val event_type: String,
    val image: String,
    val mentors: List<Mentor>,
    val points: Int,
    val questionnaire: Questionnaire,
    val requirements: List<String>,
    val time: Long,
    val title: String,
    val topics: List<String>,
    val venue: String
)