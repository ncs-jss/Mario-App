package com.ncs.marioapp.Domain.Models.Events.EventDetails

data class Questionnaire(
    val ques_count: Int,
    val questions: List<Question>
)