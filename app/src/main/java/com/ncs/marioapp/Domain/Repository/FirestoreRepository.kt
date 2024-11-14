package com.ncs.marioapp.Domain.Repository

import com.ncs.marioapp.Domain.Models.Admin.Round
import com.ncs.marioapp.Domain.Models.ServerResult

interface FirestoreRepository {
    suspend fun postRound(round: Round, callback: (ServerResult<Boolean>) -> Unit): Unit
}