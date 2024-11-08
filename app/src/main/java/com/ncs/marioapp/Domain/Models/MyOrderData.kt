package com.ncs.marioapp.Domain.Models

data class MyOrderData (
    val _id: Any,
    val name: String,
    val image: String,
    val cost: Int,
    val status: OrderStatus,
    val createdAt: Long
)
enum class OrderStatus {
    PENDING,
    FULFILLED,
    REFUND,
    CANCELLED
}