package com.ncs.marioapp.Data.RepositoryImpl

import com.ncs.marioapp.Domain.Api.EventsApi
import com.ncs.marioapp.Domain.Api.TimeApiService
import com.ncs.marioapp.Domain.Models.Events.EventDetails.TimeResponse
import com.ncs.marioapp.Domain.Models.Events.GetEvents
import com.ncs.marioapp.Domain.Models.Events.ParticipatedEventResponse
import com.ncs.marioapp.Domain.Models.ServerResult
import com.ncs.marioapp.Domain.Repository.EventRepository
import timber.log.Timber
import javax.inject.Inject

class RetrofitEventRepository @Inject constructor(
    private val eventsApi: EventsApi,
    private val timeApi: TimeApiService
) :
    EventRepository {

    override suspend fun getEvents(serverResult: (ServerResult<GetEvents>) -> Unit) {
        serverResult(ServerResult.Progress)
        try {
            val response = eventsApi.getEvents()
            if (response.isSuccessful) {
                serverResult(ServerResult.Success(response.body()!!))
            } else {
                serverResult(ServerResult.Failure(response.message()))
            }
        } catch (e: Exception) {
            serverResult(ServerResult.Failure(e.message.toString()))
        }
    }

    override suspend fun getMyEvents(serverResult: (ServerResult<ParticipatedEventResponse>) -> Unit) {
        serverResult(ServerResult.Progress)
        try {
            val response = eventsApi.getMyEvents()
            if (response.isSuccessful) {
                serverResult(ServerResult.Success(response.body()!!))
            } else {
                serverResult(ServerResult.Failure(response.message()))
            }
        } catch (e: Exception) {
            serverResult(ServerResult.Failure(e.message.toString()))
        }
    }

    override suspend fun getCurrentTime(serverResult: (ServerResult<TimeResponse>) -> Unit) {
        serverResult(ServerResult.Progress)
        try {
            val response = timeApi.getCurrentTime()
            Timber.tag("Retrofit").d(response.body().toString())

            if (response.isSuccessful) {
                serverResult(ServerResult.Success(response.body()!!))
            } else {
                serverResult(ServerResult.Failure("Response code : ${response.code()} :" + response.message()))
            }
        } catch (e: Exception) {
            serverResult(ServerResult.Failure(e.message.toString()))
        }
    }


}