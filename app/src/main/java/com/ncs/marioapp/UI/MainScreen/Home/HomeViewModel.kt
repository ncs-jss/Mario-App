package com.ncs.marioapp.UI.MainScreen.Home

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.ncs.marioapp.Domain.Api.BannerApiService
import com.ncs.marioapp.Domain.Api.EventsApi
import com.ncs.marioapp.Domain.Api.PostApiService
import com.ncs.marioapp.Domain.Models.Banner
import com.ncs.marioapp.Domain.Models.BannerResponse
import com.ncs.marioapp.Domain.Models.Events.AnswerPollBody
import com.ncs.marioapp.Domain.Models.Events.Event
import com.ncs.marioapp.Domain.Models.Events.ParticipatedEvent
import com.ncs.marioapp.Domain.Models.Events.Poll
import com.ncs.marioapp.Domain.Models.Events.PollResponse
import com.ncs.marioapp.Domain.Models.Posts.LikePostBody
import com.ncs.marioapp.Domain.Models.Posts.Post
import com.ncs.marioapp.Domain.Models.Posts.PostResponse
import com.ncs.marioapp.Domain.Models.ServerResponse
import com.ncs.marioapp.Domain.Models.ServerResult
import com.ncs.marioapp.Domain.Models.Story
import com.ncs.marioapp.Domain.Repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val bannerApiService: BannerApiService,
    private val eventRepository: EventRepository,
    private val eventsApi: EventsApi,
    private val postApiService: PostApiService,
    private val firestore: FirebaseFirestore
) : ViewModel() {
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

    val _story = MutableLiveData<Story?>()
    val story: LiveData<Story?> get() = _story

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

    private val _likeResult = MutableLiveData<Boolean>(false)
    val likeResult: LiveData<Boolean> = _likeResult

    private val _unlikeResult = MutableLiveData<Boolean>(false)
    val unlikeResult: LiveData<Boolean> = _unlikeResult

    private val _unenrollResult = MutableLiveData<Boolean>()
    val unenrollResult: LiveData<Boolean> = _unenrollResult

    private val _ticketResultBitmap = MutableLiveData<Bitmap?>(null)
    val ticketResultBitmap: LiveData<Bitmap?> = _ticketResultBitmap


    private val loadJob = viewModelScope

    fun getHomePageItems() {
        loadJob.launch {

            try {
                _progressState.value = true

                val bannersDeferred = async { getBanners() }
                val eventsDeferred = async { getEvents() }
                val myEventsDeferred = async { getMyEvents() }
                val pollsDeferred = async { getPolls() }
                val postsDeferred = async { getPosts() }

                awaitAll(
                    bannersDeferred,
                    eventsDeferred,
                    myEventsDeferred,
                    pollsDeferred,
                    postsDeferred
                )

                _progressState.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load home page items."
                _progressState.value = false
            }

        }
    }

    fun cancelJobs() {
        loadJob.cancel("Job paused..")
    }


    fun resetErrorMessage() {
        _errorMessage.value = null
        _normalErrorMessage.value = null
    }

    suspend fun getBanners(): Unit = withContext(Dispatchers.IO) {
        try {
            _progressState.postValue(true)

            val apiBanners = mutableListOf<Banner>()
            val response = bannerApiService.getBanners()
            if (response.isSuccessful) {
                val responseBody = response.body()
                val bannerResponse = Gson().fromJson(responseBody, BannerResponse::class.java)
                apiBanners.addAll(bannerResponse.banners)
            } else {
                val errorResponse = response.errorBody()?.string()
                val loginResponse = Gson().fromJson(errorResponse, ServerResponse::class.java)
                _errorMessage.postValue(loginResponse.message)
            }

            val firestoreBanners = mutableListOf<Banner>()
            val firestoreResult = firestore.collection("Banners")
                .get()
                .await()
            for (document in firestoreResult) {
                document.toObject(Banner::class.java)?.let { firestoreBanners.add(it) }
            }

            val combinedBanners = (apiBanners + firestoreBanners).sortedByDescending { it.createdAt }
            _banners.postValue(combinedBanners)

        } catch (e: SocketTimeoutException) {
            _errorMessage.postValue("Network timeout. Please try again.")
        } catch (e: IOException) {
            _errorMessage.postValue("Network error. Please check your connection.")
        } catch (e: Exception) {
            _errorMessage.postValue("Something went wrong. Please try again.")
            Log.d("checkingerros", e.message.toString())
        } finally {
            _progressState.postValue(false)
        }
    }
    fun getStory(storyId: String, callback: (Story?) -> Unit) {
        val tag = "STORY_FETCH"
        viewModelScope.launch {
            try {
                Log.d(tag, "Fetching story with ID: $storyId - started.")
                _progressState.postValue(true)

                // Fetch the story document by ID from Firestore
                val firestoreResult = firestore.collection("Stories")
                    .document(storyId)
                    .get()
                    .await()

                Log.d(tag, "Firestore result fetched successfully.")

                // Convert Firestore document to Story object and post value
                firestoreResult.toObject(Story::class.java)?.let {
                    _story.postValue(it)
                    callback.invoke(it)
                    Log.d(tag, "Story posted to LiveData: $it")
                } ?: Log.d(tag, "Story document not found or could not be converted.")

            } catch (e: SocketTimeoutException) {
                _errorMessage.postValue("Network timeout. Please try again.")
                Log.e(tag, "Network timeout error: ${e.message}", e)
            } catch (e: IOException) {
                _errorMessage.postValue("Network error. Please check your connection.")
                Log.e(tag, "Network error: ${e.message}", e)
            } catch (e: Exception) {
                _errorMessage.postValue("Something went wrong. Please try again.")
                Log.e(tag, "Unexpected error: ${e.message}", e)
            } finally {
                _progressState.postValue(false)
                Log.d(tag, "Fetching story with ID: $storyId - completed.")
            }
        }
    }

    fun getStoryFromViewModel() :String?{
        return _story.value?.storyText
    }

    suspend fun getEvents(): Unit = withContext(Dispatchers.IO) {
        eventRepository.getEvents {
            when (it) {
                is ServerResult.Failure -> _getEventsResponse.postValue(ServerResult.Failure(it.message))
                ServerResult.Progress -> _getEventsResponse.postValue(ServerResult.Progress)
                is ServerResult.Success -> _getEventsResponse.postValue(ServerResult.Success(it.data.events))
            }
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


    fun resetTicketResult() {
        _ticketResultBitmap.value = null
    }

    fun getTicket(eventId: String) {
        viewModelScope.launch {
            try {
                val response = eventsApi.getTicket(eventID = eventId)
                if (response.isSuccessful) {
                    val inputStream: InputStream = response.body()!!.byteStream()
                    val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
                    _ticketResultBitmap.value = bitmap
                } else {
                    val errorResponse = response.errorBody()?.string()
                    _normalErrorMessage.value = "Failed to get your ticket"
                }
            }
            catch (e: IOException) {
                Log.d("signupResult",e.message.toString())
                _progressState.value = false
                _normalErrorMessage.value = "Network error. Please check your connection."
            }catch (e: SocketTimeoutException) {
                _normalErrorMessage.value = "Network timeout. Please try again."
            } catch (e: Exception) {
                _normalErrorMessage.value = "Failed to get ticket.."
            }
        }
    }

//    fun enrollUser(eventId: String) {
//        viewModelScope.launch {
//            _progressState.value = true
//            _normalErrorMessage.value = "Enrolling you to the event"
//            try {
//                val response = eventsApi.enrollUser(payload = EnrollUser(eventId))
//                if (response.isSuccessful) {
//                    val inputStream: InputStream = response.body()!!.byteStream()
//                    val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
//                    _ticketResultBitmap.value = bitmap
//                    _progressState.value = false
//                    _normalErrorMessage.value = "Enrolled you to the event"
//                    _enrollResult.value = true
//                    getMyEvents()
//                } else {
//                    _progressState.value = false
//                    val errorResponse = response.errorBody()?.string()
//                    _normalErrorMessage.value = "Failed to enroll you to the event"
//                }
//            } catch (e: SocketTimeoutException) {
//                _progressState.value = false
//                _normalErrorMessage.value = "Network timeout. Please try again."
//            } catch (e: Exception) {
//                Log.d("checkexc", e.message.toString())
//                Log.d("checkexc", e.toString())
//                Log.d("checkexc", e.localizedMessage.toString())
//                _progressState.value = false
//                _normalErrorMessage.value = "Failed to enroll you..."
//            }
//        }
//    }

//    fun unenrollUser(eventId: String) {
//        viewModelScope.launch {
//            _progressState.value = true
//            _normalErrorMessage.value = "Unenrolling you from the event"
//            try {
//                val response = eventsApi.optOutUser(payload = EnrollUser(eventId))
//
//                if (response.isSuccessful) {
//                    _progressState.value = false
//                    _normalErrorMessage.value = "Unenrolled you from the event"
//                    _unenrollResult.value = true
//                    getMyEvents()
//                } else {
//                    val errorResponse = response.errorBody()?.string()
//                    _progressState.value = false
//                    _normalErrorMessage.value = "Failed to unenroll you"
//                }
//            } catch (e: SocketTimeoutException) {
//                _progressState.value = false
//                _normalErrorMessage.value = "Network timeout. Please try again."
//            } catch (e: Exception) {
//                _progressState.value = false
//                _normalErrorMessage.value = "Failed to unenroll you.."
//            }
//        }
//    }

    suspend fun getPolls(): Unit = withContext(Dispatchers.IO) {
        viewModelScope.launch {
            _progressState.value = true
            try {
                val response = eventsApi.getPolls()
                if (response.isSuccessful) {
                    val res = response.body().toString()
                    val pollResponse = Gson().fromJson(res, PollResponse::class.java)
                    _polls.value = pollResponse.polls
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
                Log.d("exceptionCheck", e.message.toString())
                _progressState.value = false
                _errorMessage.value = "Failed to load polls.."
            }
        }
    }

    fun answerPoll(answerPollBody: AnswerPollBody) {
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
            }catch (e: IOException) {
                Log.d("signupResult",e.message.toString())
                _progressState.value = false
                _normalErrorMessage.value = "Network error. Please check your connection."
            } catch (e: SocketTimeoutException) {
                _progressState.value = false
                _normalErrorMessage.value = "Network timeout. Please try again."
            } catch (e: Exception) {
                _progressState.value = false
                _normalErrorMessage.value = "Failed to load answer poll.."
            }
        }
    }

    private suspend fun getPosts(): Unit = withContext(Dispatchers.IO) {
        viewModelScope.launch {
            try {
                _progressState.value = true
                val response = postApiService.getPosts()
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    val postResponse = Gson().fromJson(responseBody, PostResponse::class.java)
                    _posts.value = postResponse.posts
                    _progressState.value = false
                } else {
                    val errorResponse = response.errorBody()?.string()
                    val loginResponse = Gson().fromJson(errorResponse, ServerResponse::class.java)
                    _progressState.value = false
                    _errorMessage.value = loginResponse.message
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
                _errorMessage.value = "Failed to load posts.."
                _progressState.value = false
            }
        }
    }

    fun likePost(likePostBody: LikePostBody) {
        viewModelScope.launch {
            try {
                val response = postApiService.likePost(payload = likePostBody)
                if (response.isSuccessful) {
                    if (likePostBody.action == "LIKE") {
                        _likeResult.value = true
                    } else {
                        _unlikeResult.value = true
                    }
                } else {
                    if (likePostBody.action == "LIKE") {
                        _likeResult.value = false
                    } else {
                        _unlikeResult.value = false
                    }
                    _normalErrorMessage.value = "Failed to like the post"
                }
            } catch (e: IOException) {
                Log.d("signupResult",e.message.toString())
                _progressState.value = false
                _normalErrorMessage.value = "Network error. Please check your connection."
            }catch (e: SocketTimeoutException) {
                _normalErrorMessage.value = "Network timeout. Please try again."
            } catch (e: Exception) {
                _normalErrorMessage.value = "Something went wrong. Please try again."
            }
        }
    }
}