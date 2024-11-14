package com.ncs.marioapp.Data.RepositoryImpl

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.ncs.marioapp.Domain.Models.Admin.Questionnaire
import com.ncs.marioapp.Domain.Models.Admin.Round
import com.ncs.marioapp.Domain.Models.ServerResult
import com.ncs.marioapp.Domain.Repository.FirestoreRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class FirestoreRepositoryImpl @Inject constructor(val firestore: FirebaseFirestore) :
    FirestoreRepository {
    override suspend fun postRound(round: Round, callback: (ServerResult<Boolean>) -> Unit) {
        try {
            callback.invoke(ServerResult.Progress)
            firestore.collection("Rounds")
                .document(round.roundID)
                .set(round)
                .await()

            callback.invoke(ServerResult.Success(true))

        } catch (e: Exception) {
            Log.d("FirestoreRepository", "postRound: ${e.message}")
            callback.invoke(ServerResult.Failure(e.message.toString()))
        }


    }

    override suspend fun postQuestionnaire(questionnaire: Questionnaire, callback: (ServerResult<Boolean>) -> Unit) {
        try {
            callback.invoke(ServerResult.Progress)
            firestore.collection("Questionnaire")
                .document(questionnaire.queID)
                .set(questionnaire)
                .await()

            callback.invoke(ServerResult.Success(true))

        } catch (e: Exception) {
            Log.d("FirestoreRepository", "postQuestionnaire: ${e.message}")
            callback.invoke(ServerResult.Failure(e.message.toString()))
        }

    }

    override suspend fun fetchRoundsByEventId(
        eventId: String,
        callback: (ServerResult<List<Round>>) -> Unit
    ) {
        try {
            callback.invoke(ServerResult.Progress)

            val roundsSnapshot = firestore.collection("Rounds")
                .whereEqualTo("eventID", eventId)
                .get()
                .await()

            val rounds = roundsSnapshot.documents.mapNotNull { document ->
                try {
                    Round(
                        roundID = document.getString("roundID") ?: return@mapNotNull null,
                        roundTitle = document.getString("roundTitle") ?: return@mapNotNull null,
                        eventID = document.getString("eventID") ?: return@mapNotNull null,
                        venue = document.getString("venue") ?: return@mapNotNull null,
                        description = document.getString("description")!!,
                        timeLine = mapOf(
                            "startCollege" to document.getLong("startCollege")!!,
                            "endCollege" to document.getLong("endCollege")!!,
                            "startUniversity" to document.getLong("startUniversity")!!,
                            "endUniversity" to document.getLong("endUniversity")!!,
                        ),
                        questionnaireID = document.getString("questionnaireID")!!,
                        isLive = document.getBoolean("live") ?: false,
                        requireSubmission = document.getBoolean("requireSubmission") ?: false,
                        submissionButtonText = document.getString("submissionButtonText")!!,
                    )
                } catch (e: Exception) {
                    Log.e("FirestoreRepository", "Error parsing document: ${document.id}", e)
                    null
                }
            }.sortedBy { it.seriesNumber }

            callback.invoke(ServerResult.Success(rounds))

        } catch (e: Exception) {
            Log.d("FirestoreRepository", "fetchRoundsByEventId: ${e.message}")
            callback.invoke(ServerResult.Failure(e.message.toString()))
        }
    }




}