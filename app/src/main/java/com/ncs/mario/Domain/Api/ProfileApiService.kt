package com.ncs.mario.Domain.Api

import com.google.gson.JsonObject
import com.ncs.mario.Domain.HelperClasses.PrefManager
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers

interface ProfileApiService {

    @Headers("Content-Type: application/json")
    @GET("create-profile")
    suspend fun createUserProfile(@Header("Authorization") authToken: String=PrefManager.getToken()!!): Response<JsonObject>

    @Headers("Content-Type: application/json")
    @GET("get-my-details")
    suspend fun getMyDetails(@Header("Authorization") authToken: String=PrefManager.getToken()!!): Response<JsonObject>



}