package com.ncs.mario.Domain.Models.EVENTS

data class ParticepatedEvent(
val createdAt: Long,
val enrolled: List<String>,
val time: Long?,
val id: Any,
val image: String,
val domain: List<String>,
val title: String,
val description: String,
val registrationLink: String?,
val venue: String?,
val enrolledCount: Int
)
