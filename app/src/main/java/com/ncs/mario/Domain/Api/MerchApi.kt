package com.ncs.mario.Domain.Api

import com.google.gson.JsonObject
import com.ncs.mario.Domain.HelperClasses.PrefManager
import com.ncs.mario.Domain.Models.MerchPurchase
import com.ncs.mario.Domain.Models.ServerResult
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface MerchApi {

    @Headers("Content-Type: application/json")
    @GET("get-merch")
    suspend fun getMerch(@Header("Authorization") authToken: String= PrefManager.getToken()!!): Response<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("buy-merch")
    suspend fun buyMerch(@Header("Authorization") authToken: String= PrefManager.getToken()!!,@Body payload:MerchPurchase): Response<JsonObject>

    @Headers("Content-Type: application/json")
    @GET("get-my-orders")
    suspend fun getMyOrders(@Header("Authorization") authToken: String= PrefManager.getToken()!!): Response<JsonObject>
}