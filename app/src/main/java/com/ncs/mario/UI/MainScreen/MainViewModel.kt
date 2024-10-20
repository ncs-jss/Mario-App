package com.ncs.mario.UI.MainScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.ncs.mario.Domain.Api.BannerApiService
import com.ncs.mario.Domain.Interfaces.EventRepository
import com.ncs.mario.Domain.Interfaces.ProfileRepository
import com.ncs.mario.Domain.Interfaces.QrRepository
import com.ncs.mario.Domain.Models.Banner
import com.ncs.mario.Domain.Models.BannerResponse
import com.ncs.mario.Domain.Models.Events.Event
import com.ncs.mario.Domain.Models.ProfileData.Profile
import com.ncs.mario.Domain.Models.ServerResponse
import com.ncs.mario.Domain.Models.ServerResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val qrRepository: QrRepository,
    private val profileRepository: ProfileRepository,
    private val eventRepository: EventRepository,
    private val bannerApiService: BannerApiService
) : ViewModel() {
    private val _myMarioScore = MutableLiveData<ServerResult<Int>>()
    val myMarioScore: LiveData<ServerResult<Int>> = _myMarioScore

    private val _validateScannedQR = MutableLiveData<ServerResult<String>>()
    val validateScannedQR: LiveData<ServerResult<String>> = _validateScannedQR

    private val _getMyProfileResponse = MutableLiveData<ServerResult<Profile>>()
    val getMyProfileResponse: LiveData<ServerResult<Profile>> = _getMyProfileResponse

    private val _getEventsResponse = MutableLiveData<ServerResult<List<Event>>>()
    val getEventsResponse: LiveData<ServerResult<List<Event>>> = _getEventsResponse

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _progressState = MutableLiveData<Boolean>(false)
    val progressState: LiveData<Boolean> get() = _progressState

    private val _bannerResult = MutableLiveData<Boolean>()
    val bannerResult: LiveData<Boolean> get() = _bannerResult

    private val _banners = MutableLiveData<List<Banner>>()
    val banners: LiveData<List<Banner>> get() = _banners

    init {
        getMyProfile()
        getBanners()
    }

//    fun getMyMarioScore() {
//        viewModelScope.launch {
//            qrRepository.getMyRewards() {
//                when (it) {
//                    is ServerResult.Failure -> {
//                        _myMarioScore.value = ServerResult.Failure(it.exception)
//                    }
//                    ServerResult.Progress -> {
//                        _myMarioScore.value = ServerResult.Progress
//                    }
//                    is ServerResult.Success -> {
//                        if (it.data.success) {
//                            _myMarioScore.value = ServerResult.Success(it.data.rewards!!.sumOf { it.points })
//                        } else {
//                            _myMarioScore.value = ServerResult.Failure(Exception(it.data.message))
//                        }
//                    }
//                }
//            }
//        }
//    }


    fun getBanners(){
        viewModelScope.launch {
            try {
                val response = bannerApiService.getBanners()
                if (response.isSuccessful) {
                    val responseBody=response.body()?.asString
                    val bannerResponse = Gson().fromJson(responseBody, BannerResponse::class.java)
                    _banners.value=bannerResponse.banners
                } else {
                    val errorResponse = response.errorBody()?.string()
                    val loginResponse = Gson().fromJson(errorResponse, ServerResponse::class.java)
                    _bannerResult.value = false
                    _errorMessage.value=loginResponse.message
                }
            } catch (e: SocketTimeoutException) {
                _errorMessage.value = "Network timeout. Please try again."
            } catch (e: IOException) {
                _errorMessage.value = "Network error. Please check your connection."
            } catch (e: Exception) {
                _errorMessage.value = "Something went wrong. Please try again."
            }
        }
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
        viewModelScope.launch {
            profileRepository.getProfile {
                when (it) {
                    is ServerResult.Failure -> {
                        _getMyProfileResponse.value = ServerResult.Failure(it.exception)
                    }
                    ServerResult.Progress -> {
                        _getMyProfileResponse.value = ServerResult.Progress
                    }
                    is ServerResult.Success -> {
                        if (it.data.success) {
                            _getMyProfileResponse.postValue(ServerResult.Success(it.data.profile!!))
                        } else {
                        }
                    }
                }
            }
        }
    }

    fun getEvents() {
        viewModelScope.launch {
            eventRepository.getEvents {
                when (it) {
                    is ServerResult.Failure -> {
                        _getEventsResponse.value = ServerResult.Failure(it.exception)
                    }
                    ServerResult.Progress -> {
                        _getEventsResponse.value = ServerResult.Progress
                    }
                    is ServerResult.Success -> {
                        if (it.data.success) {
                            _getEventsResponse.value = ServerResult.Success(it.data.events)
                        } else {
                        }
                    }
                }
            }
        }
    }
}