package com.ncs.mario.Domain.Repository

import com.ncs.mario.Domain.Api.EventsApi
import com.ncs.mario.Domain.Interfaces.EventRepository
import com.ncs.mario.Domain.Models.EVENTS.GetEvents
import com.ncs.mario.Domain.Models.ServerResult
import javax.inject.Inject

class RetrofitEventRepository @Inject constructor(private val eventsApi: EventsApi):
    EventRepository {
    override suspend fun getEvents(serverResult: (ServerResult<GetEvents>) -> Unit) {
        serverResult(ServerResult.Progress)
        try {
            val response = eventsApi.getEvents()
            if(response.isSuccessful){
                ServerResult.Success(response.body()!!)
            }
            else{
                ServerResult.Failure(Exception(response.message()))
            }
        }
        catch (e:Exception){
            ServerResult.Failure(e)
        }
    }
}