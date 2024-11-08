package com.ncs.marioapp.Domain.Models

data class MerchResponse(
    val success: Boolean,
    val message: String,
    val merchandise: List<Merch>?
)
