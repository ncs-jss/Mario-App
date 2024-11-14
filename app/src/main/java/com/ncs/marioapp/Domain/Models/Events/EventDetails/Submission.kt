package com.ncs.marioapp.Domain.Models.Events.EventDetails

import com.ncs.marioapp.Domain.Models.Answer

data class Submission(
    val eventID: String,
    val questionnaireID: String,
    val roundID: String,
    val submissionID: String,
    val userID: String,
    val response:List<Answer>
)

