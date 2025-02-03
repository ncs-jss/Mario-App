package com.ncs.marioapp.Domain.Api

import com.google.gson.JsonObject
import com.ncs.marioapp.Domain.Models.EventMeetInvite
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface MailApiService {
    @Headers("Content-Type: application/json")
    @POST("/event-meet-invite")
    suspend fun sendEventMeetInviteMail(@Body payload: EventMeetInvite): Response<JsonObject>

}