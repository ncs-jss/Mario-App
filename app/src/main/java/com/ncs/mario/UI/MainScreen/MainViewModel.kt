package com.ncs.mario.UI.MainScreen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.ncs.mario.Domain.Api.ProfileApiService
import com.ncs.mario.Domain.HelperClasses.PrefManager
import com.ncs.mario.Domain.Interfaces.QrRepository
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
) : ViewModel() {

    private val _validateScannedQR = MutableLiveData<ServerResult<String>>()
    val validateScannedQR: LiveData<ServerResult<String>> = _validateScannedQR

    private val _getMyProfileResponse = MutableLiveData<Profile>()
    val getMyProfileResponse: LiveData<Profile> = _getMyProfileResponse

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
        getMyProfile()
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

    fun getMyProfile() {
        _progressState.value=true
        viewModelScope.launch {
            try {
                val response = profileApiService.getMyDetails()
                if (response.isSuccessful) {
                    val loginResponse = response.body().toString()
                    val User= Gson().fromJson(loginResponse, User::class.java)
                    PrefManager.setUserProfile(User.profile)
                    _progressState.value=false
                    _getMyProfileResponse.value=User.profile
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