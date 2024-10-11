package com.ncs.mario.Domain.Models

data class VerifyOTP(
    val user_id:String,
    val otp:Int
)
