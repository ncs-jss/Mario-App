package com.ncs.marioapp.Domain.Repository

import com.ncs.marioapp.Domain.Models.Admin.Questionnaire
import com.ncs.marioapp.Domain.Models.Admin.Round
import com.ncs.marioapp.Domain.Models.ServerResult

interface FirestoreRepository {
    suspend fun postQuestionnaire(questionnaire: Questionnaire, callback: (ServerResult<Boolean>) -> Unit)
    suspend fun postRound(round: Round, callback: (ServerResult<Boolean>) -> Unit)
    suspend fun getRounds(eventID: String, callback: (ServerResult<List<Round>>) -> Unit)
}