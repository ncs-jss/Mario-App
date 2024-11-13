package com.ncs.marioapp.UI.AdminScreen.RecordCreation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ncs.marioapp.Domain.Models.Admin.Round
import com.ncs.marioapp.Domain.Models.Events.Event
import com.ncs.marioapp.Domain.Models.ServerResult
import com.ncs.marioapp.Domain.Repository.EventRepository
import com.ncs.marioapp.Domain.Repository.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class RecordCreationViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val firestoreRepository: FirestoreRepository
) : ViewModel() {

    private val _getEventsResponse = MutableLiveData<ServerResult<List<Event>>>()
    val getEventsResponse: LiveData<ServerResult<List<Event>>> = _getEventsResponse

    private val _postRoundResponse = MutableLiveData<ServerResult<String>>()
    val postRoundResponse: LiveData<ServerResult<String>> = _postRoundResponse

    init {
        viewModelScope.launch {
            getEvents()
        }
    }

    private val loadJob = viewModelScope

    suspend fun getEvents(): Unit = withContext(Dispatchers.IO) {
        eventRepository.getEvents {
            when (it) {
                is ServerResult.Failure -> _getEventsResponse.postValue(ServerResult.Failure(it.message))
                ServerResult.Progress -> _getEventsResponse.postValue(ServerResult.Progress)
                is ServerResult.Success -> _getEventsResponse.postValue(ServerResult.Success(it.data.events))
            }
        }
    }

    suspend fun postRound(round: Round) {
        viewModelScope.launch {

            firestoreRepository.postRound(round) {
                when (it) {
                    is ServerResult.Failure -> {
                        _postRoundResponse.postValue(ServerResult.Failure(it.message))
                    }

                    ServerResult.Progress -> {
                        _postRoundResponse.postValue(ServerResult.Progress)
                    }

                    is ServerResult.Success -> {
                        _postRoundResponse.postValue(ServerResult.Success("Successfully added round.."))
                    }
                }
            }
        }
    }


}