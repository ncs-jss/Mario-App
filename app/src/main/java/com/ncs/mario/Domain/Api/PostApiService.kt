package com.ncs.mario.Domain.Api

import com.google.gson.JsonObject
import com.ncs.mario.Domain.HelperClasses.PrefManager
import com.ncs.mario.Domain.Models.Events.AnswerPollBody
import com.ncs.mario.Domain.Models.Posts.LikePostBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface PostApiService {
    @Headers("Content-Type: application/json")
    @GET("get-posts")
    suspend fun getPosts(@Header("Authorization") authToken: String= PrefManager.getToken()!!): Response<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("like-post")
    suspend fun likePost(@Header("Authorization") authToken: String=PrefManager.getToken()!!, @Body payload: LikePostBody): Response<JsonObject>
}