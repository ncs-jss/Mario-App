package com.ncs.marioapp.Domain.HelperClasses

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
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
import timber.log.Timber
import java.io.ByteArrayOutputStream

@HiltWorker
class UploadCollegeIDWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val profileApiService: ProfileApiService
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val uri = inputData.getString("imageUri") ?: return Result.failure()
        Timber.d("Processing image URI: $uri")

        val context = applicationContext
        return try {
            val bitmap = getBitmapFromUri(Uri.parse(uri), context)
            if (bitmap == null) {
                Timber.e("Failed to load bitmap from URI: $uri")
                return Result.failure()
            }

            val base64Image = bitmapToBase64WithMimeType(bitmap)

            val response = profileApiService.addCollegeIDPicture(payload = ImageBody(base64Image))
            if (response.isSuccessful) {
                val responseResult = response.body()
                val uploadRes = Gson().fromJson(responseResult, ImageUploadResult::class.java)
                val photoToken = uploadRes.id_card_token ?: return Result.failure()
                PrefManager.setUserCollegeIDToken(photoToken)
                Timber.d("Upload successful, photoToken: $photoToken")
                Result.success(workDataOf("idCardToken" to photoToken))
            } else {
                Timber.e("API call failed with response: ${response.errorBody()?.string()}")
                Result.retry()
            }
        } catch (e: Exception) {
            Timber.e(e, "Error during UploadCollegeIDWorker")
            Result.failure()
        }
    }

    private fun getBitmapFromUri(uri: Uri, context: Context): Bitmap? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            Timber.e(e, "Error decoding bitmap from URI: $uri")
            null
        }
    }

    private fun bitmapToBase64WithMimeType(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        val base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT)
        return "data:image/jpeg;base64,$base64Image"
    }
}
