package com.ncs.marioapp.Domain.Models

data class MyOrderResponse(
    val success: Boolean,
    val message: String,
    val orders: List<MyOrderData>?
)
