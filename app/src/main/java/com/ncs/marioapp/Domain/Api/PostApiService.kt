package com.ncs.marioapp.Domain.Api

import com.google.gson.JsonObject
import com.ncs.marioapp.Domain.HelperClasses.PrefManager
import com.ncs.marioapp.Domain.Models.Posts.LikePostBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface PostApiService {
    @Headers("Content-Type: application/json")
    @GET("get-posts")
    suspend fun getPosts(@Header("Authorization") authToken: String= PrefManager.getToken()!!,
                         @Header("ban-kyc-token") banKycToken: String = PrefManager.getKYCHeaderToken()!!,
    ): Response<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("like-post")
    suspend fun likePost(@Header("Authorization") authToken: String=PrefManager.getToken()!!,
                         @Header("ban-kyc-token") banKycToken: String = PrefManager.getKYCHeaderToken()!!,
                         @Body payload: LikePostBody): Response<JsonObject>
}