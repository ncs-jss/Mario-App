package com.ncs.mario.Domain.API

import com.ncs.mario.Domain.Models.Profile.getMyProfile
import com.ncs.mario.Domain.Models.ServerResult
import retrofit2.Response
import retrofit2.http.GET

interface ProfileApi {
    @GET("/get-my-details")
    suspend fun getProfile(): Response<ServerResult<getMyProfile>>

}