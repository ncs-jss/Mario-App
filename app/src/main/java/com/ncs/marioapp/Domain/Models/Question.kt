package com.ncs.marioapp.Domain.Models

data class Question(
    val question: String,
    val type:String,
    val options: List<String> = emptyList()
)

data class Answer(
    val question: String,
    var answer: String = ""
)
