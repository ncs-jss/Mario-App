package com.ncs.mario.Domain.Api

import com.google.gson.JsonObject
import com.ncs.mario.Domain.HelperClasses.PrefManager
import com.ncs.mario.Domain.Models.CreateProfileBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface BannerApiService {
    @Headers("Content-Type: application/json")
    @GET("get-banners")
    suspend fun getBanners(@Header("Authorization") authToken: String= PrefManager.getToken()!!): Response<JsonObject>
}