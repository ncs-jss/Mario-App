package com.ncs.marioapp.Domain.HelperClasses

import android.content.Context
import android.net.Uri
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.gson.Gson
import com.ncs.marioapp.Domain.Models.CreateProfileBody

object WorkerUtil {
    fun startProfileUploadWorkflow(
        userImageUri: Uri,
        collegeIdUri: Uri,
        createProfilePayload: CreateProfileBody,
        isUpdate: Boolean,
        context: Context,
        callback: (List<String>)->Unit
    ) {
        val workManager = WorkManager.getInstance(context)

        // Upload User Image Worker
        val uploadUserImageRequest = OneTimeWorkRequestBuilder<UploadUserImageWorker>()
            .setInputData(workDataOf("imageUri" to userImageUri.toString()))
            .build()

        // Upload College ID Worker
        val uploadCollegeIdRequest = OneTimeWorkRequestBuilder<UploadCollegeIDWorker>()
            .setInputData(workDataOf("imageUri" to collegeIdUri.toString()))
            .build()

        // Profile Worker
        val profilePayloadJson = Gson().toJson(createProfilePayload)
        val profileWorkerRequest = OneTimeWorkRequestBuilder<ProfileWorker>()
            .setInputData(
                workDataOf(
                    "isUpdate" to isUpdate,
                    "photoToken" to PrefManager.getUserSelfieToken(),
                    "idCardToken" to PrefManager.getUserCollegeIDToken(),
                    "profilePayload" to profilePayloadJson
                )
            )
            .build()

        callback(listOf(uploadUserImageRequest.id.toString(), uploadCollegeIdRequest.id.toString(), profileWorkerRequest.id.toString()))

        // Chain Workers
        workManager
            .beginWith(uploadUserImageRequest)
            .then(uploadCollegeIdRequest)
            .then(profileWorkerRequest)
            .enqueue()
    }

}