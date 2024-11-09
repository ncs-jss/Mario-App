package com.ncs.marioapp.Domain.Models

data class ResetPassBody(
    val temp_token:String,
    val password:String,
)
