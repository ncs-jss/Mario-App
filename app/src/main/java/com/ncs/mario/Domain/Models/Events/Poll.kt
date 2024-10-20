package com.ncs.mario.Domain.Models.Events

data class Poll(
    val _id: String,
    val createdAt: Long,
    val options: List<Option>,
    val question: String,
    val userChoice:String?=null
)