package com.ncs.mario.UI.MainScreen.Score

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ncs.mario.Domain.Interfaces.EventRepository
import com.ncs.mario.Domain.Models.Events.ParticipatedEventResponse
import com.ncs.mario.Domain.Models.ServerResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScoreViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    private val _getEventsResponse = MutableLiveData<ServerResult<ParticipatedEventResponse>>()
    val getEventsResponse: LiveData<ServerResult<ParticipatedEventResponse>> = _getEventsResponse

    private val _progressState = MutableLiveData<Boolean>(false)
    val progressState: LiveData<Boolean> get() = _progressState

    fun getMyEvents() {
        viewModelScope.launch {
            _progressState.value = true
            eventRepository.getMyEvents {
                when (it) {
                    is ServerResult.Failure -> {
                        _progressState.value = false
                        _getEventsResponse.value = ServerResult.Failure(it.exception)
                    }
                    ServerResult.Progress -> {
                        _getEventsResponse.value = ServerResult.Progress
                    }
                    is ServerResult.Success -> {
                        _progressState.value = false
                        _getEventsResponse.value = ServerResult.Success(it.data)
                    }
                }
            }
        }
    }
}