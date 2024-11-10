package com.ncs.marioapp.UI.MainScreen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.ncs.marioapp.Domain.Api.EventsApi
import com.ncs.marioapp.Domain.Api.ProfileApiService
import com.ncs.marioapp.Domain.HelperClasses.PrefManager
import com.ncs.marioapp.Domain.Interfaces.QrRepository
import com.ncs.marioapp.Domain.Models.Events.ScanTicketBody
import com.ncs.marioapp.Domain.Models.Profile
import com.ncs.marioapp.Domain.Models.QR.QrScannedResponse
import com.ncs.marioapp.Domain.Models.ServerResponse
import com.ncs.marioapp.Domain.Models.ServerResult
import com.ncs.marioapp.Domain.Models.SetFCMTokenBody
import com.ncs.marioapp.Domain.Models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val qrRepository: QrRepository,
    private val profileApiService: ProfileApiService,
    private val eventsApi: EventsApi
) : ViewModel() {

    private val _validateScannedQR = MutableLiveData<ServerResult<QrScannedResponse>>()
    val validateScannedQR: LiveData<ServerResult<QrScannedResponse>> = _validateScannedQR

    private val _getMyProfileResponse = MutableLiveData<Profile>()
    val getMyProfileResponse: LiveData<Profile> = _getMyProfileResponse

    private val _userCoins = MutableLiveData<Int>()
    val userCoins: LiveData<Int> = _userCoins

    private val _userPoints = MutableLiveData<Int>()
    val userPoints: LiveData<Int> = _userPoints

    private val _cachedUserProfile = MutableLiveData<Profile>()
    val cachedUserProfile: LiveData<Profile> = _cachedUserProfile

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _progressState = MutableLiveData<Boolean>(false)
    val progressState: LiveData<Boolean> get() = _progressState


    init {
        fetchUserDetails()
        fetchCriticalInfo()
        getFCMToken()
    }

    fun setFCMToken(newFCMToken:String) {
        _progressState.value=true
        viewModelScope.launch {
            try {
                val response = profileApiService.setFCMToken(payload = SetFCMTokenBody(newFCMToken))
                if (response.isSuccessful) {
                    Log.d("signupResult", "FCM Token Set: ${response.body()}")
                    _progressState.value=false
                } else {
                    val errorResponse = response.errorBody()?.string()
                    val loginResponse = Gson().fromJson(errorResponse, ServerResponse::class.java)
                    Log.d("signupResult", "FCM Token Set Failed: ${loginResponse.message}")
                    _progressState.value=false
                }
            } catch (e: SocketTimeoutException) {
                _errorMessage.value = "Network timeout. Please try again."
                _progressState.value = false
            } catch (e: IOException) {
                _errorMessage.value = "Network error. Please check your connection."
                _progressState.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Something went wrong. Please try again."
                _progressState.value = false
            }
        }
    }


    private fun getFCMToken() {

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@addOnCompleteListener
            }
            val actualFCM = task.result
            PrefManager.setFCMToken(actualFCM)
            setFCMToken(actualFCM)
        }

    }

    fun fetchUserDetails() {
        _cachedUserProfile.value=PrefManager.getUserProfile()
        _progressState.value=true
        viewModelScope.launch {
            try {
                val response = profileApiService.getMyDetails()
                if (response.isSuccessful) {
                    response.body()?.let {
                        val user = Gson().fromJson(it.toString(), User::class.java)
                        _cachedUserProfile.value=user.profile
                        PrefManager.setUserProfile(user.profile)
                        PrefManager.setUserProfileForCache(user.profile)
                        _progressState.value = false
                    }
                } else {
                    _errorMessage.value = "Something went wrong. Please try again."
                    _progressState.value = false
                }
            } catch (e: SocketTimeoutException) {
                _errorMessage.value = "Network timeout. Please try again."
                _progressState.value = false
            } catch (e: IOException) {
                _errorMessage.value = "Network error. Please check your connection."
                _progressState.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Something went wrong. Please try again."
                _progressState.value = false
            }
        }
    }


    fun fetchCriticalInfo(){
        getMyPoints()
        getMyCoins()
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
                        _validateScannedQR.value = ServerResult.Success(it.data)
                    }
                }
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

    fun getMyPoints() {
        _progressState.value=true
        viewModelScope.launch {
            try {
                val response = profileApiService.getUserPoints()
                if (response.isSuccessful) {
                    val points=response.body()?.get("points")
                    if (points != null) {
                        _userPoints.value = points.asInt
                    }
                    _progressState.value=false
                } else {
                    val errorResponse = response.errorBody()?.string()
                    val loginResponse = Gson().fromJson(errorResponse, ServerResponse::class.java)
                    _progressState.value=false
                }
            } catch (e: SocketTimeoutException) {
                _progressState.value=false
            } catch (e: IOException) {
                _progressState.value=false
            } catch (e: Exception) {
                _progressState.value=false
            }
        }
    }

    fun getMyCoins() {
        _progressState.value=true
        viewModelScope.launch {
            try {
                val response = profileApiService.getUserCoins()
                if (response.isSuccessful) {

                    val coins=response.body()?.get("coins")
                    if (coins != null) {
                        _userCoins.value = coins.asInt
                        PrefManager.setUserCoins(coins.asInt)
                    }
                    _progressState.value=false
                } else {
                    val errorResponse = response.errorBody()?.string()
                    val loginResponse = Gson().fromJson(errorResponse, ServerResponse::class.java)
                    _progressState.value=false
                }
            } catch (e: SocketTimeoutException) {
                _progressState.value=false
            } catch (e: IOException) {
                _progressState.value=false
            } catch (e: Exception) {
                _progressState.value=false
            }
        }
    }


}