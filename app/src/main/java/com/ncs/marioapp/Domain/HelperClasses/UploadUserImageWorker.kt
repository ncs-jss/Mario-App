package com.ncs.marioapp.Domain.HelperClasses

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.gson.Gson
import com.ncs.marioapp.Domain.Api.ProfileApiService
import com.ncs.marioapp.Domain.Models.ImageBody
import com.ncs.marioapp.Domain.Models.ImageUploadResult
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.ByteArrayOutputStream

@HiltWorker
class UploadUserImageWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val profileApiService: ProfileApiService
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val uri = inputData.getString("imageUri") ?: return Result.failure()
        val context = applicationContext
        return try {
            val bitmap = getBitmapFromUri(Uri.parse(uri), context)
            val base64Image = bitmapToBase64WithMimeType(bitmap)

            val response = profileApiService.uploadUserPicture(payload = ImageBody(base64Image))
            if (response.isSuccessful) {
                val responseResult = response.body()
                val uploadRes = Gson().fromJson(responseResult, ImageUploadResult::class.java)
                val photoToken = uploadRes.photo_token ?: return Result.failure()
                PrefManager.setUserSelfieToken(photoToken)
                Result.success(workDataOf("photoToken" to photoToken))
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun getBitmapFromUri(uri: Uri, context: Context): Bitmap {
        val inputStream = context.contentResolver.openInputStream(uri)
        return BitmapFactory.decodeStream(inputStream)
    }

    private fun bitmapToBase64WithMimeType(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        val base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT)

        return "data:image/jpeg;base64,$base64Image"
    }

}
