package com.ncs.mario.UI.MainScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ncs.mario.Domain.Interfaces.EventRepository
import com.ncs.mario.Domain.Interfaces.ProfileRepository
import com.ncs.mario.Domain.Interfaces.QrRepository
import com.ncs.mario.Domain.Models.EVENTS.Event
import com.ncs.mario.Domain.Models.Profile.Profile
import com.ncs.mario.Domain.Models.Profile.getMyProfile
import com.ncs.mario.Domain.Models.ServerResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val qrRepository: QrRepository,
    private val profileRepository: ProfileRepository,
    private val eventRepository: EventRepository
):ViewModel() {
    private val _myMarioScore = MutableLiveData<ServerResult<Int>>()
    val myMarioScore: LiveData<ServerResult<Int>> = _myMarioScore

    private val _validateScannedQR = MutableLiveData<ServerResult<String>>()
    val validateScannedQR: LiveData<ServerResult<String>> = _validateScannedQR

    private val _getMyProfileResponse = MutableLiveData<ServerResult<Profile>>()
    val getMyProfileResponse: LiveData<ServerResult<Profile>> = _getMyProfileResponse

    private val _getEventsResponse = MutableLiveData<ServerResult<List<Event>>>()
    val getEventsResponse: LiveData<ServerResult<List<Event>>> = _getEventsResponse


    init {
        getMyMarioScore()
        getMyProfile()
    }

    fun getMyMarioScore(){
        viewModelScope.launch {
            qrRepository.getMyRewards(){
                when(it){
                    is ServerResult.Failure ->{
                        _myMarioScore.value = ServerResult.Failure(it.exception)
                    }
                    ServerResult.Progress -> {
                        _myMarioScore.value = ServerResult.Progress
                    }
                    is ServerResult.Success -> {
                        if(it.data.success){
                            _myMarioScore.value = ServerResult.Success(it.data.rewards!!.points)
                        }
                        else{
                            _myMarioScore.value = ServerResult.Failure(Exception(it.data.message))
                        }
                    }
                }
            }

        }
    }
    fun validateScannedQR(couponCode:String){
        viewModelScope.launch {
            qrRepository.validateScannedQR(couponCode) {
                when(it){
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
    fun getMyProfile(){
        viewModelScope.launch {
            profileRepository.getProfile{
                when(it){
                    is ServerResult.Failure -> {
                        _getMyProfileResponse.value = ServerResult.Failure(it.exception)
                    }

                    ServerResult.Progress -> {
                        _getMyProfileResponse.value = ServerResult.Progress
                    }
                    is ServerResult.Success -> {
                        if (it.data.success){
                            _getMyProfileResponse.postValue(ServerResult.Success(it.data.profile!!))
                        }
                    }
                }
            }
        }
    }
    fun getEvents(){
        viewModelScope.launch {
            eventRepository.getEvents {
                when(it){
                    is ServerResult.Failure -> {
                        _getEventsResponse.value = ServerResult.Failure(it.exception)

                    }
                    ServerResult.Progress -> {
                        _getEventsResponse.value = ServerResult.Progress
                    }
                    is ServerResult.Success -> {
                        if(it.data.success){
                            _getEventsResponse.value = ServerResult.Success(it.data.event)
                        }
                    }
                }
            }
        }
    }
}