package com.ncs.marioapp.Data.RepositoryImpl

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
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

    override suspend fun getRounds(eventID: String, callback: (ServerResult<List<Round>>) -> Unit) {
        try {
            callback.invoke(ServerResult.Progress)

            val querySnapshot = firestore.collection("Rounds")
                .whereEqualTo("eventID", eventID)
                .get()
                .await()

            val rounds = querySnapshot.documents.mapNotNull { document ->
                document.toObject(Round::class.java)
            }

            Log.d("FirestoreRepository", "roundList: ${rounds}")
            callback.invoke(ServerResult.Success(rounds))

        } catch (e: Exception) {
            Log.d("FirestoreRepository", "getRounds: ${e.message}")
            callback.invoke(ServerResult.Failure(e.message.toString()))
        }
    }


}