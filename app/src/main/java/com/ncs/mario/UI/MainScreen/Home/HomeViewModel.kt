package com.ncs.mario.UI.MainScreen.Home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.ncs.mario.Domain.Api.BannerApiService
import com.ncs.mario.Domain.Api.EventsApi
import com.ncs.mario.Domain.Api.PostApiService
import com.ncs.mario.Domain.Interfaces.EventRepository
import com.ncs.mario.Domain.Models.Banner
import com.ncs.mario.Domain.Models.BannerResponse
import com.ncs.mario.Domain.Models.Events.AnswerPollBody
import com.ncs.mario.Domain.Models.Events.EnrollUser
import com.ncs.mario.Domain.Models.Events.Event
import com.ncs.mario.Domain.Models.Events.ParticipatedEvent
import com.ncs.mario.Domain.Models.Events.Poll
import com.ncs.mario.Domain.Models.Events.PollResponse
import com.ncs.mario.Domain.Models.Posts.LikePostBody
import com.ncs.mario.Domain.Models.Posts.Post
import com.ncs.mario.Domain.Models.Posts.PostResponse
import com.ncs.mario.Domain.Models.ServerResponse
import com.ncs.mario.Domain.Models.ServerResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val bannerApiService: BannerApiService,
    private val eventRepository: EventRepository,
    private val eventsApi: EventsApi,
    private val postApiService: PostApiService
    ) : ViewModel(){
    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _normalErrorMessage = MutableLiveData<String?>(null)
    val normalErrorMessage: LiveData<String?> get() = _normalErrorMessage

    private val _errorMessageEventEnroll = MutableLiveData<String?>()
    val errorMessageEventEnroll: LiveData<String?> get() = _errorMessageEventEnroll

    private val _progressState = MutableLiveData<Boolean>(false)
    val progressState: LiveData<Boolean> get() = _progressState

    private val _banners = MutableLiveData<List<Banner>>()
    val banners: LiveData<List<Banner>> get() = _banners

    private val _getEventsResponse = MutableLiveData<ServerResult<List<Event>>>()
    val getEventsResponse: LiveData<ServerResult<List<Event>>> = _getEventsResponse

    private val _getMyEventsResponse = MutableLiveData<ServerResult<List<ParticipatedEvent>>>()
    val getMyEventsResponse: LiveData<ServerResult<List<ParticipatedEvent>>> = _getMyEventsResponse

    private val _polls = MutableLiveData<List<Poll>>()
    val polls: LiveData<List<Poll>> = _polls

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> = _posts

    private val _enrollResult = MutableLiveData<Boolean>()
    val enrollResult: LiveData<Boolean> = _enrollResult

    private val _unenrollResult = MutableLiveData<Boolean>()
    val unenrollResult: LiveData<Boolean> = _unenrollResult


    fun getHomePageItems(){
        getBanners()
        getEvents()
        getMyEvents()
        getPolls()
        getPosts()
    }

    fun resetErrorMessage(){
        _errorMessage.value=null
        _normalErrorMessage.value=null
    }

    fun getBanners(){
        viewModelScope.launch {
            try {
                _progressState.value = true
                val response = bannerApiService.getBanners()
                if (response.isSuccessful) {
                    val responseBody=response.body()
                    val bannerResponse = Gson().fromJson(responseBody, BannerResponse::class.java)
                    _banners.value=bannerResponse.banners
                    _progressState.value = false
                } else {
                    val errorResponse = response.errorBody()?.string()
                    val loginResponse = Gson().fromJson(errorResponse, ServerResponse::class.java)
                    _progressState.value = false
                    _errorMessage.value=loginResponse.message
                    Log.d("exceptionCheck", errorResponse!!)
                }
            } catch (e: SocketTimeoutException) {
                _errorMessage.value = "Network timeout. Please try again."
                _progressState.value = false
            } catch (e: IOException) {
                _errorMessage.value = "Network error. Please check your connection."
                _progressState.value = false
            } catch (e: Exception) {
                Log.d("exceptionCheck", e.message.toString())
                _errorMessage.value = "Something went wrong. Please try again."
                _progressState.value = false
            }
        }
    }

    fun getEvents() {
        viewModelScope.launch {
            _progressState.value = true
            eventRepository.getEvents {
                when (it) {
                    is ServerResult.Failure -> {
                        _errorMessage.value = it.exception.message
                        _progressState.value = false
                        _getEventsResponse.value = ServerResult.Failure(it.exception)
                    }
                    ServerResult.Progress -> {
                        _progressState.value = true
                        _getEventsResponse.value = ServerResult.Progress
                    }
                    is ServerResult.Success -> {
                        if (it.data.success) {
                            _progressState.value = false
                            _getEventsResponse.value = ServerResult.Success(it.data.events)
                        }
                    }
                }
            }
        }
    }

    fun getMyEvents() {
        viewModelScope.launch {
            eventRepository.getMyEvents {
                when (it) {
                    is ServerResult.Failure -> {
                        _errorMessage.value = it.exception.message
                        _progressState.value = false
                        _getMyEventsResponse.value = ServerResult.Failure(it.exception)
                    }
                    ServerResult.Progress -> {
                        _progressState.value = true
                        _getMyEventsResponse.value = ServerResult.Progress
                    }
                    is ServerResult.Success -> {
                        _progressState.value = false
                        _getMyEventsResponse.postValue(ServerResult.Success(it.data.events))
                    }
                }
            }
        }
    }

    fun enrollUser(eventId:String){
        viewModelScope.launch {
            _progressState.value = true
            _normalErrorMessage.value = "Enrolling you to the event"
            try {
                val response = eventsApi.enrollUser(payload = EnrollUser(eventId))

                if (response.isSuccessful) {
                    _progressState.value = false
                    _normalErrorMessage.value = "Enrolled you to the event"
                    _enrollResult.value=true
                    getMyEvents()
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
                _normalErrorMessage.value = "Something went wrong. Please try again."
            }
        }
    }

    fun unenrollUser(eventId:String){
        viewModelScope.launch {
            _progressState.value = true
            _normalErrorMessage.value = "Unenrolling you from the event"
            try {
                val response = eventsApi.optOutUser(payload = EnrollUser(eventId))

                if (response.isSuccessful) {
                    _progressState.value = false
                    _normalErrorMessage.value = "Unenrolled you from the event"
                    _unenrollResult.value=true
                    getMyEvents()
                } else {
                    val errorResponse = response.errorBody()?.string()
                    _progressState.value = false
                    _normalErrorMessage.value = "Failed to unenroll you"
                }
            } catch (e: SocketTimeoutException) {
                _progressState.value = false
                _normalErrorMessage.value = "Network timeout. Please try again."
            } catch (e: Exception) {
                _progressState.value = false
                _normalErrorMessage.value = "Something went wrong. Please try again."
            }
        }
    }

    fun getPolls() {
        viewModelScope.launch {
            _progressState.value = true
            try {
                val response = eventsApi.getPolls()
                if (response.isSuccessful) {
                    val res = response.body().toString()
                    val pollResponse= Gson().fromJson(res, PollResponse::class.java)
                    _polls.value=pollResponse.polls
                    _progressState.value = false
                } else {
                    val errorResponse = response.errorBody()?.string()
                    val loginResponse = Gson().fromJson(errorResponse, ServerResponse::class.java)
                    _progressState.value = false
                    _errorMessage.value=loginResponse.message
                    Log.d("exceptionCheck", errorResponse!!)

                }
            } catch (e: SocketTimeoutException) {
                _progressState.value = false
                _errorMessage.value = "Network timeout. Please try again."
            } catch (e: IOException) {
                _progressState.value = false
                _errorMessage.value = "Network error. Please check your connection."
            } catch (e: Exception) {
                Log.d("exceptionCheck", e.message.toString())
                _progressState.value = false
                _errorMessage.value = "Something went wrong. Please try again."
            }
        }
    }

    fun answerPoll(answerPollBody: AnswerPollBody){
        viewModelScope.launch {
            _progressState.value = true
            try {
                val response = eventsApi.answerPoll(payload = answerPollBody)
                if (response.isSuccessful) {
                    _progressState.value = false
                } else {
                    _progressState.value = false
                    _normalErrorMessage.value = "Failed to submit your response"
                }
            } catch (e: SocketTimeoutException) {
                _progressState.value = false
                _normalErrorMessage.value = "Network timeout. Please try again."
            } catch (e: Exception) {
                _progressState.value = false
                _normalErrorMessage.value = "Something went wrong. Please try again."
            }
        }
    }

    fun getPosts(){
        viewModelScope.launch {
            try {
                _progressState.value = true
                val response = postApiService.getPosts()
                if (response.isSuccessful) {
                    val responseBody=response.body()
                    val postResponse = Gson().fromJson(responseBody, PostResponse::class.java)
                    _posts.value=postResponse.posts
                    _progressState.value = false
                } else {
                    val errorResponse = response.errorBody()?.string()
                    val loginResponse = Gson().fromJson(errorResponse, ServerResponse::class.java)
                    _progressState.value = false
                    _errorMessage.value=loginResponse.message
                    Log.d("exceptionCheck", errorResponse!!)
                }
            } catch (e: SocketTimeoutException) {
                _errorMessage.value = "Network timeout. Please try again."
                _progressState.value = false
            } catch (e: IOException) {
                _errorMessage.value = "Network error. Please check your connection."
                _progressState.value = false
            } catch (e: Exception) {
                Log.d("exceptionCheck", e.message.toString())
                _errorMessage.value = "Something went wrong. Please try again."
                _progressState.value = false
            }
        }
    }

    fun likePost(likePostBody: LikePostBody){
        viewModelScope.launch {
            _progressState.value = true
            try {
                val response = postApiService.likePost(payload = likePostBody)
                if (response.isSuccessful) {
                    _progressState.value = false
                } else {
                    _progressState.value = false
                    _normalErrorMessage.value = "Failed to like the post"
                }
            } catch (e: SocketTimeoutException) {
                _progressState.value = false
                _normalErrorMessage.value = "Network timeout. Please try again."
            } catch (e: Exception) {
                _progressState.value = false
                _normalErrorMessage.value = "Something went wrong. Please try again."
            }
        }
    }
}