package com.ncs.marioapp.Domain.Api

import com.google.gson.JsonObject
import com.ncs.marioapp.Domain.HelperClasses.PrefManager
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers

interface BannerApiService {
    @Headers("Content-Type: application/json")
    @GET("get-banners")
    suspend fun getBanners(@Header("Authorization") authToken: String= PrefManager.getToken()!!,
                           @Header("ban-kyc-token") banKycToken: String = PrefManager.getKYCHeaderToken()!!,
    ): Response<JsonObject>
}