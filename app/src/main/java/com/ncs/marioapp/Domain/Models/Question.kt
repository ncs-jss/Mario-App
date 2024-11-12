package com.ncs.marioapp.Domain.Models

data class Question(
    val options: List<Any>,
    val question: String,
    val type: String
)

data class Answer(
    val question: String,
    var answer: String = ""
)
