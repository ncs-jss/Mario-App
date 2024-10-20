package com.ncs.mario.Domain.Interfaces

import com.ncs.mario.Domain.Models.EVENTS.GetEvents
import com.ncs.mario.Domain.Models.EVENTS.ParticipatedEventResponse
import com.ncs.mario.Domain.Models.QR.QrScannedResponse
import com.ncs.mario.Domain.Models.ServerResult

interface EventRepository {

    suspend fun getEvents(serverResult: (ServerResult<GetEvents>) -> Unit)
    suspend fun getMyEvents(serverResult: (ServerResult<ParticipatedEventResponse>) -> Unit)

}