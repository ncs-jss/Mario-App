package com.ncs.marioapp.Domain.Models.Score

data class Transaction(
    val _id: String,
    val coins: Int,
    val operation: String,
    val time: Long,
    val title: String,
    val type: String
)