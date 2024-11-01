package com.ncs.mario.UI.MainScreen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.ncs.mario.Domain.Api.EventsApi
import com.ncs.mario.Domain.Api.ProfileApiService
import com.ncs.mario.Domain.HelperClasses.PrefManager
import com.ncs.mario.Domain.Interfaces.QrRepository
import com.ncs.mario.Domain.Models.Events.AnswerPollBody
import com.ncs.mario.Domain.Models.Events.ScanTicketBody
import com.ncs.mario.Domain.Models.Profile
import com.ncs.mario.Domain.Models.ServerResponse
import com.ncs.mario.Domain.Models.ServerResult
import com.ncs.mario.Domain.Models.User
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

    private val _validateScannedQR = MutableLiveData<ServerResult<String>>()
    val validateScannedQR: LiveData<ServerResult<String>> = _validateScannedQR

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
        fetchUserProfile()
        fetchCriticalInfo()
    }


    fun fetchUserProfile(){
        _cachedUserProfile.value=PrefManager.getUserProfile()
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
                        _validateScannedQR.value = ServerResult.Failure(it.exception)
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