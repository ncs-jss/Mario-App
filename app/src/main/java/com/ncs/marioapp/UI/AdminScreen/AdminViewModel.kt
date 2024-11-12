package com.ncs.marioapp.UI.AdminScreen

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudinary.Cloudinary
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.firebase.firestore.FirebaseFirestore
import com.ncs.marioapp.BuildConfig
import com.ncs.marioapp.Domain.Api.EventsApi
import com.ncs.marioapp.Domain.Api.ProfileApiService
import com.ncs.marioapp.Domain.Api.QRAPI
import com.ncs.marioapp.Domain.Interfaces.QrRepository
import com.ncs.marioapp.Domain.Models.Admin.GiftCoinsPostBody
import com.ncs.marioapp.Domain.Models.Banner
import com.ncs.marioapp.Domain.Models.Events.ScanTicketBody
import com.ncs.marioapp.Domain.Models.ServerResult
import com.ncs.marioapp.Domain.Models.Story
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val qrRepository: QrRepository,
    private val qrapi: QRAPI,
    private val profileApiService: ProfileApiService,
    private val eventsApi: EventsApi,
    private val firestore: FirebaseFirestore
) : ViewModel() {


    private val _validateScannedQR = MutableLiveData<ServerResult<String>>()
    val validateScannedQR: LiveData<ServerResult<String>> = _validateScannedQR

    private val _progressState = MutableLiveData<Boolean>(false)
    val progressState: LiveData<Boolean> get() = _progressState

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _progressStateGiftCoins = MutableLiveData<Boolean>(false)
    val progressStateGiftCoins: LiveData<Boolean> get() = _progressStateGiftCoins

    private val _addBannerResult = MutableLiveData<Boolean>()
    val addBannerResult: LiveData<Boolean> = _addBannerResult
    private val _addStoryResult = MutableLiveData<Boolean>()
    val addStoryResult: LiveData<Boolean> = _addStoryResult

    private val _imageUrlLiveData = MutableLiveData<String?>()
    val imageUrlLiveData :LiveData<String?> = _imageUrlLiveData

    fun uploadImageToCloudinary(imageUri: Uri) {
        try {
            imageUri.let {
                MediaManager.get().upload(it)
                    .option("resource_type", "image")
                    .callback(object : UploadCallback {

                        override fun onStart(requestId: String?) {
                            _progressState.postValue(true)
                        }
                        override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}
                        override fun onSuccess(
                            requestId: String?,
                            resultData: MutableMap<Any?, Any?>?
                        ) {
                            val imageUrl = resultData?.get("secure_url") as String
                            Log.d("Image URL", imageUrl)
                            _progressState.postValue(false)
                            _imageUrlLiveData.postValue(imageUrl)
                        }

                        override fun onError(requestId: String?, error: ErrorInfo?) {
                            _errorMessage.postValue("Upload error: $error")
                            _progressState.postValue(false)
                            Log.d("Image URL", error.toString())
                        }

                        override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                        }


                    })
                    .dispatch()
            } ?: { _errorMessage.postValue("Failed to open image stream") }
            _progressState.postValue(false)

        } catch (e: Exception) {
            _progressState.postValue(false)
            _errorMessage.postValue("Exception: ${e.localizedMessage}")
        }
    }

    fun addBanner(banner: Banner) {
        _progressState.value = true
        viewModelScope.launch {
            try {
                firestore.collection("Banners")
                    .document(banner._id)
                    .set(banner)
                    .addOnSuccessListener {succ->
                        _progressState.value = false
                        _addBannerResult.postValue(true)
                    }
                    .addOnFailureListener {
                        _progressState.value = false
                        _errorMessage.postValue(it.message)
                    }
            } catch (e: Exception) {
                _progressState.value = false
                _errorMessage.postValue(e.message)
            }
        }
    }

    fun addStory(story:Story){
        _progressState.value = true
        viewModelScope.launch {
            try {
                firestore.collection("Stories")
                    .document(story.storyId)
                    .set(story)
                    .addOnSuccessListener {succ->
                        _progressState.value = false
                        _addStoryResult.postValue(true)
                        _imageUrlLiveData.postValue(null)
                    }
                    .addOnFailureListener { it->
                        _progressState.value = false
                        _errorMessage.postValue(it.message)
                    }

            } catch (e: Exception) {
                _progressState.value = false
                _errorMessage.postValue(e.message)
            }
        }
    }

    fun scanTicket(scanTicketBody: ScanTicketBody){
        viewModelScope.launch {
            _progressState.value = true
            try {
                val response = eventsApi.scanTicket(payload = scanTicketBody)
                if (response.isSuccessful) {
                    _progressState.value = false
                    _errorMessage.value = "Attendance Marked"
                } else {
                    _progressState.value = false
                    _errorMessage.value = "Failed to scan ticket"
                }
            } catch (e: SocketTimeoutException) {
                _progressState.value = false
                _errorMessage.value = "Network timeout. Please try again."
            } catch (e: Exception) {
                _progressState.value = false
                _errorMessage.value = "Something went wrong. Please try again."
            }
        }
    }

    fun validateScannedQR(couponCode: String) {
        viewModelScope.launch {
            qrRepository.validateScannedQR(couponCode) {
                when (it) {
                    is ServerResult.Failure -> {
                        _validateScannedQR.value = ServerResult.Failure(it.message)
                    }
                    ServerResult.Progress -> {
                        _validateScannedQR.value = ServerResult.Progress
                    }
                    is ServerResult.Success -> {
                        _validateScannedQR.value = ServerResult.Success(it.data.message)
                    }
                }
            }
        }
    }

    fun giftCoins(giftCoinsPostBody: GiftCoinsPostBody){
        viewModelScope.launch {
            _progressStateGiftCoins.value = true
            try {
                val response = qrapi.giftCoins(payload = giftCoinsPostBody)
                if (response.isSuccessful) {
                    _progressStateGiftCoins.value = false
                    _errorMessage.value = "Coins gifted successfully"
                } else {
                    _progressStateGiftCoins.value = false
                    _errorMessage.value = "Failed to gift coins"
                }
            } catch (e: SocketTimeoutException) {
                _progressStateGiftCoins.value = false
                _errorMessage.value = "Network timeout. Please try again."
            } catch (e: Exception) {
                _progressStateGiftCoins.value = false
                _errorMessage.value = "Something went wrong. Please try again."
            }
        }
    }
}