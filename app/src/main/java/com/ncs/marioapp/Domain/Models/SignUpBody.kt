package com.ncs.marioapp.Domain.Models

data class SignUpBody(
    val email: String,
    val phone:String,
    val password:String
)