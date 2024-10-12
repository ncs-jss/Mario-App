package com.ncs.mario.Domain.Api

import com.google.gson.JsonObject
import com.ncs.mario.Domain.Models.LoginBody
import com.ncs.mario.Domain.Models.ResendOTPBody
import com.ncs.mario.Domain.Models.SignUpBody
import com.ncs.mario.Domain.Models.VerifyOTP
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


interface AuthApiService {
    @Headers("Content-Type: application/json")
    @POST("signup")
    suspend fun signUp(@Body payload: SignUpBody): Response<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("verify-signup-otp")
    suspend fun verifyOTP(@Body payload: VerifyOTP): Response<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("resend-otp")
    suspend fun resendOTP(@Body payload: ResendOTPBody): Response<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("login")
    suspend fun login(@Body payload: LoginBody): Response<JsonObject>

}