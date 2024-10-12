package com.ncs.mario.Domain.Models

data class SignUpBody(
    val email: String,
    val phone:String,
    val password:String
)