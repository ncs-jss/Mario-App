package com.ncs.marioapp.Domain.HelperClasses

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.ncs.marioapp.Domain.Api.ProfileApiService
import com.ncs.marioapp.Domain.Models.CreateProfileBody
import com.ncs.marioapp.Domain.Models.UpdateProfileBody
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ProfileWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val profileApiService: ProfileApiService
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val isUpdate = inputData.getBoolean("isUpdate", false)
            val photoToken = inputData.getString("photoToken") ?: run {
                Log.e("ProfileWorker", "Missing photoToken")
                return Result.failure()
            }
            val idCardToken = inputData.getString("idCardToken") ?: run {
                Log.e("ProfileWorker", "Missing idCardToken")
                return Result.failure()
            }
            val payloadJson = inputData.getString("profilePayload") ?: run {
                Log.e("ProfileWorker", "Missing profilePayload")
                return Result.failure()
            }

            Log.d("ProfileWorker", "Inputs - isUpdate: $isUpdate, photoToken: $photoToken, idCardToken: $idCardToken, payloadJson: $payloadJson")

            val payload = try {
                Gson().fromJson(payloadJson, CreateProfileBody::class.java).apply {
                    this.photo_token = photoToken
                    this.id_card_token = idCardToken
                }
            } catch (e: Exception) {
                Log.e("ProfileWorker", "Error parsing profilePayload JSON: $e")
                return Result.failure()
            }

            val response = if (isUpdate) {
                Log.d("ProfileWorker", "Updating user profile")

                val _payload = try {
                    Gson().fromJson(payloadJson, CreateProfileBody::class.java).apply {
                        this.photo_token = photoToken
                        this.id_card_token = idCardToken
                    }
                } catch (e: Exception) {
                    Log.e("ProfileWorker", "Error parsing profilePayload JSON: $e")
                    return Result.failure()
                }

                val updateProfileBody=UpdateProfileBody(
                    branch = _payload.branch,
                    domain = _payload.domain,
                    other_domain = _payload.other_domain,
                    name = _payload.name,
                    admitted_to = _payload.admitted_to,
                    socials = _payload.socials,
                    year = _payload.year,
                    photo_token = _payload.photo_token,
                    id_card_token = _payload.id_card_token
                )

                profileApiService.updateUserProfile(payload = updateProfileBody)
                profileApiService.requestKYCToPending()
            } else {
                Log.d("ProfileWorker", "Creating user profile and requesting KYC to pending")
                profileApiService.createUserProfile(payload = payload)
            }

            if (response.isSuccessful) {
                Log.d("ProfileWorker", "Profile update/create success: ${response.body()}")
                Result.success()
            } else {
                Log.e("ProfileWorker", "API response failed: ${response.code()} - ${response.errorBody()?.string()}")
                Result.retry()
            }
        } catch (e: Exception) {
            Log.e("ProfileWorker", "Unexpected error: $e", e)
            Result.failure()
        }
    }
}
