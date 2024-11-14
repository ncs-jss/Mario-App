package com.ncs.marioapp.UI.EventDetailsScreen

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.ncs.marioapp.Domain.Api.EventsApi
import com.ncs.marioapp.Domain.Models.Admin.Round
import com.ncs.marioapp.Domain.Models.Answer
import com.ncs.marioapp.Domain.Models.Events.EnrollUser
import com.ncs.marioapp.Domain.Models.Events.Event
import com.ncs.marioapp.Domain.Models.Events.EventDetails.EventDetails
import com.ncs.marioapp.Domain.Models.Events.EventDetails.EventDetailsResponse
import com.ncs.marioapp.Domain.Models.Events.ParticipatedEvent
import com.ncs.marioapp.Domain.Models.ServerResponse
import com.ncs.marioapp.Domain.Models.ServerResult
import com.ncs.marioapp.Domain.Repository.EventRepository
import com.ncs.marioapp.Domain.Repository.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class EventDetailsViewModel @Inject constructor(
    private val eventsApi: EventsApi,
    private val eventRepository: EventRepository,
    private val firestoreRepository: FirestoreRepository
    ) : ViewModel() {

    private val _event = MutableLiveData<Event?>(null)
    val event: LiveData<Event?> get() = _event

    private val _progressState = MutableLiveData<Boolean>(false)
    val progressState: LiveData<Boolean> get() = _progressState

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _eventDetails = MutableLiveData<EventDetails>(null)
    val eventDetails: LiveData<EventDetails> = _eventDetails

    private val _currentQuestionIndex = MutableLiveData(0)
    val currentQuestionIndex: LiveData<Int> get() = _currentQuestionIndex

    private val _answers = MutableLiveData<MutableList<Answer>>(mutableListOf())
    val answers: LiveData<MutableList<Answer>> get() = _answers

    private val _normalErrorMessage = MutableLiveData<String?>(null)
    val normalErrorMessage: LiveData<String?> get() = _normalErrorMessage

    private val _ticketResultBitmap = MutableLiveData<Bitmap>(null)
    val ticketResultBitmap: LiveData<Bitmap> = _ticketResultBitmap

    private val _enrollResult = MutableLiveData<Boolean>()
    val enrollResult: LiveData<Boolean> = _enrollResult

    private val _getMyEventsResponse = MutableLiveData<ServerResult<List<ParticipatedEvent>>>()
    val getMyEventsResponse: LiveData<ServerResult<List<ParticipatedEvent>>> = _getMyEventsResponse

    private val _roundsListLiveData = MutableLiveData<ServerResult<List<Round>>>(null)
    val roundsListLiveData: LiveData<ServerResult<List<Round>>> get() = _roundsListLiveData


    fun resetErrorMessage() {
        _errorMessage.value = null
        _normalErrorMessage.value = null
    }

    fun setEvent(event: Event) {
        _event.value = event
    }

    fun getEvent(): Event? {
        return _event.value
    }

    init {
        viewModelScope.launch {
            getMyEvents()
        }
    }

    var isSummaryShown: Boolean = false

    fun setEventDetails(details: EventDetails) {
        _eventDetails.value = details
    }

    fun getAllRoundsForEvent(eventID: String) {
        viewModelScope.launch {
            firestoreRepository.getRounds(eventID) { it ->
                when (it) {
                    is ServerResult.Failure -> {
                        _normalErrorMessage.value = "Failed to load rounds.."
                        _roundsListLiveData.value = ServerResult.Failure(it.message)
                    }

                    ServerResult.Progress -> {
                        _progressState.value = true
                        _roundsListLiveData.value = ServerResult.Progress
                    }

                    is ServerResult.Success -> {
                        _roundsListLiveData.value = ServerResult.Success(it.data)
                    }
                }
            }
        }
    }

    fun enrollUser(enrollUser: EnrollUser) {
        viewModelScope.launch {
            _progressState.value = true
            _normalErrorMessage.value = "Enrolling you to the event"
            try {
                val response = eventsApi.enrollUser(payload = enrollUser)
                if (response.isSuccessful) {
                    val inputStream: InputStream = response.body()!!.byteStream()
                    val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
                    _ticketResultBitmap.value = bitmap
                    _progressState.value = false
                    _normalErrorMessage.value = "Enrolled you to the event"
                    _enrollResult.value = true
                } else {
                    _progressState.value = false
                    val errorResponse = response.errorBody()?.string()
                    _normalErrorMessage.value = "Failed to enroll you to the event"
                }
            } catch (e: SocketTimeoutException) {
                _progressState.value = false
                _normalErrorMessage.value = "Network timeout. Please try again."
            } catch (e: Exception) {
                _progressState.value = false
                _normalErrorMessage.value = "Failed to enroll you..."
            }
        }
    }

    suspend fun getEvents(): Unit = withContext(Dispatchers.IO) {
        eventRepository.getEvents { res ->
            when (res) {
                is ServerResult.Failure -> {}
                ServerResult.Progress -> {}
                is ServerResult.Success -> {
                    _event.value = res.data.events.firstOrNull { it._id == _event.value?._id }
                }
            }
        }
    }

    fun getNumberOfQuestions():Int {
        return _eventDetails.value!!.questionnaire.questions.size
    }
    fun submitAnswer(answer: Answer) {
        _answers.value?.apply {
            add(answer)
            _answers.value = this
        }

        if (_currentQuestionIndex.value!! < _eventDetails.value!!.questionnaire.questions.size - 1) {
            nextQuestion()
        } else {
            isSummaryShown = true
        }
    }

    fun nextQuestion() {
        _currentQuestionIndex.value = _currentQuestionIndex.value?.plus(1)
    }

    fun previousQuestion() {
        if (_currentQuestionIndex.value!! > 0) {
            _currentQuestionIndex.value = _currentQuestionIndex.value?.minus(1)
        }
    }

    suspend fun getMyEvents(): Unit = withContext(Dispatchers.IO) {
        try {
            eventRepository.getMyEvents {
                when (it) {
                    is ServerResult.Failure -> _getMyEventsResponse.postValue(
                        ServerResult.Failure(
                            it.message
                        )
                    )

                    ServerResult.Progress -> _getMyEventsResponse.postValue(ServerResult.Progress)
                    is ServerResult.Success -> _getMyEventsResponse.postValue(
                        ServerResult.Success(
                            it.data.events
                        )
                    )
                }
            }
        } catch (e: Exception) {
            _errorMessage.postValue("Failed to load my events.")
        }
    }

    fun getEventDetails(event_id: String) {
        viewModelScope.launch {
            _progressState.value = true
            try {
                val response = eventsApi.getEventsDetails(eventID = event_id)
                if (response.isSuccessful) {
                    val res = response.body().toString()
                    val eventDetailsResponse = Gson().fromJson(res, EventDetailsResponse::class.java)
                    _eventDetails.value = eventDetailsResponse.event
                    _progressState.value = false
                } else {
                    val errorResponse = response.errorBody()?.string()
                    val loginResponse = Gson().fromJson(errorResponse, ServerResponse::class.java)
                    _progressState.value = false
                    _errorMessage.value = loginResponse.message
                    Log.d("exceptionCheck", errorResponse!!)

                }
            } catch (e: SocketTimeoutException) {
                _progressState.value = false
                _errorMessage.value = "Network timeout. Please try again."
            } catch (e: IOException) {
                _progressState.value = false
                _errorMessage.value = "Network error. Please check your connection."
            } catch (e: Exception) {
                _progressState.value = false
                _errorMessage.value = "Failed to load event..."
            }
        }
    }

}