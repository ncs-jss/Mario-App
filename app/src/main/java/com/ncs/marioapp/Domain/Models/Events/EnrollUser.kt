package com.ncs.marioapp.Domain.Models.Events

import com.ncs.marioapp.Domain.Models.Answer


data class EnrollUser(
    val event_id: String,
    val response: List<Answer>?
)


