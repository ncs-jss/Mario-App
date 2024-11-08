package com.ncs.marioapp.Domain.Models.Score

data class TransactionsResponse(
    val message: String,
    val success: Boolean,
    val transactions: List<Transaction>
)