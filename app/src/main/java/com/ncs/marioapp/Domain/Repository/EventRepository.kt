package com.ncs.marioapp.Domain.Repository

import com.ncs.marioapp.Domain.Models.Events.EventDetails.TimeResponse
import com.ncs.marioapp.Domain.Models.Events.GetEvents
import com.ncs.marioapp.Domain.Models.Events.ParticipatedEventResponse
import com.ncs.marioapp.Domain.Models.ServerResult

interface EventRepository {

    suspend fun getEvents(serverResult: (ServerResult<GetEvents>) -> Unit)
    suspend fun getMyEvents(serverResult: (ServerResult<ParticipatedEventResponse>) -> Unit)
    suspend fun getCurrentTime(serverResult: (ServerResult<TimeResponse>) -> Unit)

}