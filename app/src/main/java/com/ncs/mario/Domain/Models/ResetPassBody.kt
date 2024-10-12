package com.ncs.mario.Domain.Models

data class ResetPassBody(
    val temp_token:String,
    val password:String,
)
