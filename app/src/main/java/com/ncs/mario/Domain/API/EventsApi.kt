package com.ncs.mario.Domain.API

import com.ncs.mario.Domain.Models.EVENTS.GetEvents
import com.ncs.mario.Domain.Models.Profile.getMyProfile
import com.ncs.mario.Domain.Models.ServerResult
import retrofit2.Response
import retrofit2.http.GET

interface EventsApi {
    @GET("/get-events")
    suspend fun getEvents(): Response<ServerResult<GetEvents>>


}