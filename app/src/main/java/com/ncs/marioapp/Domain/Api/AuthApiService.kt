package com.ncs.marioapp.Domain.Api

import com.google.gson.JsonObject
import com.ncs.marioapp.Domain.Models.ForgotPasswordBody
import com.ncs.marioapp.Domain.Models.LoginBody
import com.ncs.marioapp.Domain.Models.ResendOTPBody
import com.ncs.marioapp.Domain.Models.ResetPassBody
import com.ncs.marioapp.Domain.Models.SignUpBody
import com.ncs.marioapp.Domain.Models.VerifyOTP
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT


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

    @Headers("Content-Type: application/json")
    @POST("forgot-password")
    suspend fun forgotPassword(@Body payload: ForgotPasswordBody): Response<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("verify-reset-otp")
    suspend fun verifyResetOTP(@Body payload: VerifyOTP): Response<JsonObject>

    @Headers("Content-Type: application/json")
    @PUT("reset-password")
    suspend fun resetPassword(@Body payload: ResetPassBody): Response<JsonObject>
}