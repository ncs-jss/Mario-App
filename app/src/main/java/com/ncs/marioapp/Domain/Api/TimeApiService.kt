package com.ncs.marioapp.Domain.Api

import com.ncs.marioapp.Domain.Models.Events.EventDetails.TimeResponse
import retrofit2.Response
import retrofit2.http.GET


interface TimeApiService {
    @GET("timezone/Asia/Kolkata")
    suspend fun getCurrentTime(): Response<TimeResponse>
}
