package com.ncs.mario.Domain.Interfaces

import com.ncs.mario.Domain.Models.Events.GetEvents
import com.ncs.mario.Domain.Models.Events.ParticipatedEventResponse
import com.ncs.mario.Domain.Models.ServerResult

interface EventRepository {

    suspend fun getEvents(serverResult: (ServerResult<GetEvents>) -> Unit)
    suspend fun getMyEvents(serverResult: (ServerResult<ParticipatedEventResponse>) -> Unit)

}