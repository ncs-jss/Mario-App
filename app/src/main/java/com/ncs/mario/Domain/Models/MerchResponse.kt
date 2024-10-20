package com.ncs.mario.Domain.Models

data class MerchResponse(
    val success: Boolean,
    val message: String,
    val merchandise: List<Merch>?
)
