package com.ncs.marioapp.Data.RepositoryImpl

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.ncs.marioapp.Domain.Models.Admin.QuestionItem
import com.ncs.marioapp.Domain.Models.Admin.Round
import com.ncs.marioapp.Domain.Models.Admin.RoundQuestionnaire
import com.ncs.marioapp.Domain.Models.Answer
import com.ncs.marioapp.Domain.Models.Events.EventDetails.Submission
import com.ncs.marioapp.Domain.Models.MeetLinks
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

    override suspend fun postQuestionnaire(questionnaire: RoundQuestionnaire, callback: (ServerResult<Boolean>) -> Unit) {
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

    override suspend fun getQuestionnaire(
        quiestionnaireID: String,
        callback: (ServerResult<RoundQuestionnaire>) -> Unit
    ) {
        try {
            callback.invoke(ServerResult.Progress)

            val querySnapshot = firestore.collection("Questionnaire")
                .whereEqualTo("queID", quiestionnaireID)
                .get()
                .await()

            val document = querySnapshot.documents.firstOrNull()

            if (document != null) {
                val queID = document.getString("queID") ?: ""
                val queTitle = document.getString("queTitle") ?: ""
                val questions = document.get("questions") as? List<Map<String, Any>> ?: emptyList()

                val questionItems = questions.map { questionMap ->
                    QuestionItem(
                        qID = questionMap["qid"] as? String ?: "",
                        questionText = questionMap["questionText"] as? String ?: "",
                        options = questionMap["options"] as? List<String> ?: emptyList(),
                        type= questionMap["type"] as? String ?: ""
                    )
                }

                val roundQuestionnaire = RoundQuestionnaire(
                    queID = queID,
                    queTitle = queTitle,
                    questions = questionItems
                )

                callback.invoke(ServerResult.Success(roundQuestionnaire))
            } else {
                callback.invoke(ServerResult.Failure("No document found with the provided queID"))
            }

        } catch (e: Exception) {
            Log.d("FirestoreRepository", "getQuestionnaire: ${e.message}")
            callback.invoke(ServerResult.Failure(e.message.toString()))
        }
    }

    override suspend fun postSubmission(
        submission: Submission,
        callback: (ServerResult<Boolean>) -> Unit
    ) {
        try {
            callback.invoke(ServerResult.Progress)

            firestore.collection("Submissions")
                .document(submission.submissionID)
                .set(submission)
                .await()

            callback.invoke(ServerResult.Success(true))
        } catch (e: Exception) {
            Log.d("FirestoreRepository", "postSubmission: ${e.message}")
            callback.invoke(ServerResult.Failure(e.message.toString()))
        }
    }

    override suspend fun getSubmissions(eventId: String, userId: String, callback: (ServerResult<List<Submission>>) -> Unit) {
        try {
            callback.invoke(ServerResult.Progress)

            val querySnapshot = firestore.collection("Submissions")
                .whereEqualTo("userID", userId)
                .whereEqualTo("eventID", eventId)
                .get()
                .await()

            val submissions = querySnapshot.documents.mapNotNull { document ->
                try {
                    val submissionID = document.getString("submissionID") ?: return@mapNotNull null
                    val userID = document.getString("userID") ?: return@mapNotNull null
                    val eventID = document.getString("eventID") ?: return@mapNotNull null
                    val roundID = document.getString("roundID") ?: return@mapNotNull null
                    val questionnaireID = document.getString("questionnaireID") ?: return@mapNotNull null

                    val responses = (document.get("response") as? List<Map<String, Any>>)?.mapNotNull { responseMap ->
                        val question = responseMap["question"] as? String ?: return@mapNotNull null
                        val answer = responseMap["answer"] as? String ?: return@mapNotNull null
                        Answer(question, answer)
                    } ?: emptyList()

                    Submission(
                        eventID = eventID,
                        questionnaireID = questionnaireID,
                        roundID = roundID,
                        submissionID = submissionID,
                        userID = userID,
                        response = responses
                    )
                } catch (e: Exception) {
                    Log.e("FirestoreRepository", "Error parsing submission: ${e.message}")
                    null
                }
            }

            callback.invoke(ServerResult.Success(submissions))
        } catch (e: Exception) {
            Log.d("FirestoreRepository", "getSubmissions: ${e.message}")
            callback.invoke(ServerResult.Failure(e.message.toString()))
        }
    }



    override suspend fun getAllLinksForAnEvent(eventID: String, callback: (ServerResult<List<MeetLinks>>) -> Unit) {
        try {
            callback.invoke(ServerResult.Progress)

            val querySnapshot = firestore.collection("AppConfig")
                .document("MeetLinks")
                .collection(eventID)
                .get()
                .await()

            val allLinks = querySnapshot.documents.mapNotNull { document ->
                val count = document.getLong("count")?.toInt()
                val link = document.getString("link")
                val type = document.getString("type")

                if (count != null && link != null && type != null) {
                    MeetLinks(count, link, type)
                } else {
                    null
                }
            }

            callback.invoke(ServerResult.Success(allLinks))
        } catch (e: Exception) {
            Log.d("FirestoreRepository", "getAllLinksForAnEvent: ${e.message}")
            callback.invoke(ServerResult.Failure(e.message.toString()))
        }
    }

    override suspend fun updateLink(eventID: String, link: MeetLinks, callback: (Boolean) -> Unit) {
        try {
            val linkRef = firestore.collection("AppConfig")
                .document("MeetLinks")
                .collection(eventID)

            val querySnapshot = linkRef.whereEqualTo("link", link.link).get().await()

            if (!querySnapshot.isEmpty) {
                val document = querySnapshot.documents.first()
                document.reference.update("count", link.count)
                    .addOnSuccessListener {
                        Log.d("FirestoreRepository", "Link count updated successfully.")
                        callback.invoke(true)
                    }
                    .addOnFailureListener { e ->
                        Log.e("FirestoreRepository", "Error updating link count: ${e.message}")
                        callback.invoke(false)
                    }
            } else {
                Log.d("FirestoreRepository", "Link not found.")
                callback.invoke(false)
            }
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error updating link: ${e.message}")
            callback.invoke(false)
        }
    }

    override suspend fun getEventStartTimeStamp(eventID: String, callback: (ServerResult<Timestamp>) -> Unit) {
        try {
            callback.invoke(ServerResult.Progress)

            val documentSnapshot = firestore.collection("AppConfig")
                .document("EventTimestamps")
                .collection(eventID)
                .document("StartTimeStamps")
                .get()
                .await()

            if (documentSnapshot.exists()) {
                val timestamp = documentSnapshot.getTimestamp("start_timestamp")

                if (timestamp != null) {
                    callback.invoke(ServerResult.Success(timestamp))
                } else {
                    callback.invoke(ServerResult.Failure("Timestamp not found in the document"))
                }
            } else {
                callback.invoke(ServerResult.Failure("Document does not exist"))
            }
        } catch (e: Exception) {
            callback.invoke(ServerResult.Failure(e.message.toString()))
        }
    }

}