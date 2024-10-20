package com.ncs.mario.Domain.Api

import com.ncs.mario.Domain.HelperClasses.PrefManager
import com.ncs.mario.Domain.Models.EVENTS.GetEvents
import com.ncs.mario.Domain.Models.EVENTS.ParticipatedEventResponse
import com.ncs.mario.Domain.Models.ServerResult
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers

interface EventsApi {
    @Headers("Content-Type: application/json")
    @GET("get-events")
    suspend fun getEvents(@Header("Authorization") authToken: String= PrefManager.getToken()!!): Response<GetEvents>

    @Headers("Content-Type: application/json")
    @GET("get-my-events")
    suspend fun getMyEvents(@Header("Authorization") authToken: String= PrefManager.getToken()!!): Response<ParticipatedEventResponse>



}