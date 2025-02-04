package com.ncs.marioapp.UI.EventDetailsScreen

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.gson.Gson
import com.ncs.marioapp.Domain.Api.EventsApi
import com.ncs.marioapp.Domain.Api.MailApiService
import com.ncs.marioapp.Domain.HelperClasses.PrefManager
import com.ncs.marioapp.Domain.Models.Admin.Round
import com.ncs.marioapp.Domain.Models.Admin.RoundQuestionnaire
import com.ncs.marioapp.Domain.Models.Answer
import com.ncs.marioapp.Domain.Models.EventMeetInvite
import com.ncs.marioapp.Domain.Models.Events.EnrollUser
import com.ncs.marioapp.Domain.Models.Events.Event
import com.ncs.marioapp.Domain.Models.Events.EventDetails.EventDetails
import com.ncs.marioapp.Domain.Models.Events.EventDetails.EventDetailsResponse
import com.ncs.marioapp.Domain.Models.Events.EventDetails.Submission
import com.ncs.marioapp.Domain.Models.Events.ParticipatedEvent
import com.ncs.marioapp.Domain.Models.MeetLinks
import com.ncs.marioapp.Domain.Models.ServerResponse
import com.ncs.marioapp.Domain.Models.ServerResult
import com.ncs.marioapp.Domain.Repository.EventRepository
import com.ncs.marioapp.Domain.Repository.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException
import java.io.InputStream
import java.net.SocketTimeoutException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class EventDetailsViewModel @Inject constructor(
    private val eventsApi: EventsApi,
    private val eventRepository: EventRepository,
    private val firestoreRepository: FirestoreRepository) : ViewModel() {

    private val _event = MutableLiveData<Event?>(null)
    val event: LiveData<Event?> get() = _event

    private val _enrolledCount = MutableLiveData<String?>("10+")
    val enrolledCount: LiveData<String?> get() = _enrolledCount

    private val _progressState = MutableLiveData<Boolean>(false)
    val progressState: LiveData<Boolean> get() = _progressState

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _questionnaireId = MutableLiveData<String?>(null)
    val questionnaireId: LiveData<String?> get() = _questionnaireId

    private val _roundId = MutableLiveData<String?>(null)
    val roundId: LiveData<String?> get() = _roundId

    private val _eventDetails = MutableLiveData<EventDetails>(null)
    val eventDetails: LiveData<EventDetails> = _eventDetails

    private val _eventStartTimeStamp = MutableLiveData<Timestamp?>()
    val eventStartTimeStamp: LiveData<Timestamp?> = _eventStartTimeStamp

    private val _eventStartTimeStampFetchResult = MutableLiveData<Boolean>(false)
    val eventStartTimeStampFetchResult: LiveData<Boolean> = _eventStartTimeStampFetchResult

    private val _getQuestionnaireForRound = MutableLiveData<ServerResult<RoundQuestionnaire>>()
    val getQuestionnaireForRound: LiveData<ServerResult<RoundQuestionnaire>> = _getQuestionnaireForRound

    private val _getSubmissionsForEvent = MutableLiveData<ServerResult<List<Submission>>>()
    val getSubmissionsForEvent: LiveData<ServerResult<List<Submission>>> = _getSubmissionsForEvent

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

    private val _postSubmissionResult = MutableLiveData<ServerResult<Boolean>>()
    val postSubmissionResult: LiveData<ServerResult<Boolean>> = _postSubmissionResult

    private val _getMyEventsResponse = MutableLiveData<ServerResult<List<ParticipatedEvent>>>()
    val getMyEventsResponse: LiveData<ServerResult<List<ParticipatedEvent>>> = _getMyEventsResponse

    private val _roundsListLiveData = MutableLiveData<ServerResult<List<Round>>>()
    val roundsListLiveData: LiveData<ServerResult<List<Round>>> get() = _roundsListLiveData

    private var _currentISTTime = MutableLiveData<ServerResult<String>>()
    val currentISTTime: LiveData<ServerResult<String>> get() = _currentISTTime

    var isUserEnrolled = -1

    var currentTime: String? = null

    fun setQuestionnaireId(id: String) {
        _questionnaireId.value = id
    }

    fun setRoundId(id: String) {
        _roundId.value = id
    }

    fun resetRoundId() {
        _roundId.value = null
    }

    fun resetQuestionnaireId() {
        _questionnaireId.value = null
    }

    fun getQuestionnaireId(): String? {
        return _questionnaireId.value
    }

    fun getRoundId(): String? {
        return _roundId.value
    }

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

    fun setEnrolledCount(enrolledCount:String) {
        _enrolledCount.value = enrolledCount
    }

    fun getEnrolledCount(): String? {
        return _enrolledCount.value
    }

    init {
        viewModelScope.launch {
            getMyEvents()
            getEventStartTimestamp()
        }
    }

    var isSummaryShown: Boolean = false

    fun setEventDetails(details: EventDetails) {
        _eventDetails.value = details
    }


    fun getCurrentTime() {
        viewModelScope.launch {
            eventRepository.getCurrentTime { currentTimeReponse ->
                when (currentTimeReponse) {
                    is ServerResult.Failure -> {
                        _normalErrorMessage.value = "Failed to load submissions.."
                        _progressState.value = false
                        _currentISTTime.value = ServerResult.Failure(currentTimeReponse.message)
                    }

                    ServerResult.Progress -> {
                        _progressState.value = true
                        _currentISTTime.value = ServerResult.Progress
                    }

                    is ServerResult.Success -> {
                        _progressState.value = false
                        _currentISTTime.value =
                            ServerResult.Success(currentTimeReponse.data.datetime)
                    }

                }
            }
        }
    }



    fun getAllSubmissionsForRounds(eventID: String, userId:String) {
        viewModelScope.launch {
            firestoreRepository.getSubmissions(eventId =eventID, userId = userId ) { it ->
                when (it) {
                    is ServerResult.Failure -> {
                        _normalErrorMessage.value = "Failed to load submissions.."
                        _progressState.value = false
                        _getSubmissionsForEvent.value = ServerResult.Failure(it.message)
                    }

                    ServerResult.Progress -> {
                        _progressState.value = true
                        _getSubmissionsForEvent.value = ServerResult.Progress
                    }

                    is ServerResult.Success -> {
                        _progressState.value = false
                        _getSubmissionsForEvent.value = ServerResult.Success(it.data)
                    }
                }
            }
        }
    }


    fun getAllQuestionnairesById(questionnaireID: String) {
        viewModelScope.launch {
            firestoreRepository.getQuestionnaire(questionnaireID) { it ->
                when (it) {
                    is ServerResult.Failure -> {
                        _normalErrorMessage.value = "Failed to load rounds.."
                        _progressState.value = false
                        _getQuestionnaireForRound.value = ServerResult.Failure(it.message)
                    }

                    ServerResult.Progress -> {
                        _progressState.value = true
                        _getQuestionnaireForRound.value = ServerResult.Progress
                    }

                    is ServerResult.Success -> {
                        _progressState.value = false
                        _getQuestionnaireForRound.value = ServerResult.Success(it.data)
                    }
                }
            }
        }
    }

    fun postSubmission(submission: Submission) {
        viewModelScope.launch {
            firestoreRepository.postSubmission(submission) { it ->
                when (it) {
                    is ServerResult.Failure -> {
                        _normalErrorMessage.value = "Failed to submit"
                        _progressState.value = false
                        _postSubmissionResult.value=ServerResult.Failure(false.toString())
                    }

                    ServerResult.Progress -> {
                        _progressState.value = true
                        _postSubmissionResult.value=ServerResult.Progress
                    }

                    is ServerResult.Success -> {
                        _progressState.value = false
                        _postSubmissionResult.value=ServerResult.Success(true)
                    }
                }
            }
        }
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

//    fun enrollUser(enrollUser: EnrollUser) {
//        viewModelScope.launch {
//            _progressState.value = true
//            _normalErrorMessage.value = "Enrolling you to the event"
//            try {
//                val response = eventsApi.enrollUser(payload = enrollUser)
//                if (response.isSuccessful) {
//                    val inputStream: InputStream = response.body()!!.byteStream()
//                    val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
//                    _ticketResultBitmap.value = bitmap
//                    _progressState.value = false
//                    _normalErrorMessage.value = "Enrolled you to the event"
//                    _enrollResult.value = true
//                } else {
//                    _progressState.value = false
//                    val errorResponse = response.errorBody()?.string()
//                    _normalErrorMessage.value = "Failed to enroll you to the event"
//                }
//            }
//            catch (e: IOException) {
//                Log.d("signupResult",e.message.toString())
//                _progressState.value = false
//                _normalErrorMessage.value = "Network error. Please check your connection."
//            }
//            catch (e: SocketTimeoutException) {
//                _progressState.value = false
//                _normalErrorMessage.value = "Network timeout. Please try again."
//            } catch (e: Exception) {
//                _progressState.value = false
//                Log.d("eventviewmodel",e.message.toString())
//                _normalErrorMessage.value = "Failed to enroll you..."
//            }
//        }
//    }

    fun enrollUser(enrollUser: EnrollUser) {
        viewModelScope.launch {
            _progressState.value = true
            _normalErrorMessage.value = "Enrolling you to the event"
            try {
                val response = eventsApi.enrollUser(payload = enrollUser)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val inputStream: InputStream = responseBody.byteStream()
                        val bitmap: Bitmap? = BitmapFactory.decodeStream(inputStream)

                        if (bitmap != null) {
                            _ticketResultBitmap.value = bitmap!!
                            _normalErrorMessage.value = "Enrolled you to the event successfully"
                        } else {
                            handleNoBitmapScenario()
                            _normalErrorMessage.value = "Enrolled you to the event successfully"
                        }
                        _enrollResult.value = true
                    } else {
                        _normalErrorMessage.value = "Enrollment successful, but response body is empty."
                    }
                    _progressState.value = false
                } else {
                    _progressState.value = false
                    val errorResponse = response.errorBody()?.string()
                    _normalErrorMessage.value = "Failed to enroll you to the event: $errorResponse"
                }
            } catch (e: IOException) {
                Log.d("signupResult", e.message.toString())
                _progressState.value = false
                _normalErrorMessage.value = "Network error. Please check your connection."
            } catch (e: SocketTimeoutException) {
                _progressState.value = false
                _normalErrorMessage.value = "Network timeout. Please try again."
            } catch (e: Exception) {
                _progressState.value = false
                Log.d("eventviewmodel", e.message.toString())
                _normalErrorMessage.value = "Failed to enroll you..."
            }
        }
    }

    private fun handleNoBitmapScenario() {
        Log.d("enrollUser", "No ticket bitmap found")
    }

    fun getEventStartTimestamp(){
        viewModelScope.launch {
            firestoreRepository.getEventStartTimeStamp(eventID = event.value?._id!!) {
                when(it){
                    is ServerResult.Failure -> {
                        _eventStartTimeStamp.value=null
                    }
                    ServerResult.Progress -> {}
                    is ServerResult.Success -> {
                        _eventStartTimeStamp.value=it.data
                    }
                }
            }
        }
    }

    fun getEventStartTimestampValue() : Timestamp?{
        return _eventStartTimeStamp.value
    }


    fun checkAndSendTheEmailInvites(enrollUser: EnrollUser){
        if (event.value?.venue=="Online"){
            val first= enrollUser.response?.get(0)
            viewModelScope.launch {
                firestoreRepository.getAllLinksForAnEvent(eventID = event.value?._id!!) { res ->
                    when (res) {
                        is ServerResult.Failure -> {
                            _enrollResult.value = false
                        }
                        ServerResult.Progress -> {
                        }
                        is ServerResult.Success -> {
                            viewModelScope.launch {
                                val links = res.data
                                val filtered = links.filter { it.type == first?.answer }

                                val linkToUpdate: MeetLinks? = if (filtered.isEmpty()) {
                                    links.firstOrNull { it.count < 95 }
                                } else {
                                    filtered.filter { it.count < 95 }
                                        .maxByOrNull { it.count }
                                }

                                if (linkToUpdate != null) {
                                    linkToUpdate.count += 1
                                    firestoreRepository.updateLink(event.value?._id!!, linkToUpdate) { success ->
                                        if (success) {
                                            val _enrollUser = EnrollUser(
                                                event_id = enrollUser.event_id,
                                                response = enrollUser.response,
                                                link = linkToUpdate.link
                                            )
                                            enrollUser(_enrollUser)
                                        }
                                    }
                                } else {
                                    Log.d("LinkUpdate", "No links found with count < 95.")
                                }
                            }

//                            viewModelScope.launch {
//                                val links = res.data
//                                val filtered = links.filter { it.type == first?.answer }
//
//                                if (filtered.isEmpty()) {
//                                    var linkToUpdate: MeetLinks? = null
//
//                                    for (link in links) {
//                                        if (link.count < 95) {
//                                            linkToUpdate = link
//                                            break
//                                        }
//                                    }
//
//                                    if (linkToUpdate != null) {
//                                        linkToUpdate.count += 1
//                                        firestoreRepository.updateLink(event.value?._id!!, linkToUpdate){
//                                            if (it){
//                                                val _enrollUser=EnrollUser(event_id = enrollUser.event_id, response = enrollUser.response, link = linkToUpdate?.link!!)
//                                                enrollUser(_enrollUser)
//                                            }
//                                        }
//                                    } else {
//                                        Log.d("LinkUpdate", "No links found with count < 95.")
//                                    }
//                                }
//                                else {
//
//                                    var linkToUpdate: MeetLinks? = null
//
//                                    for (link in filtered) {
//                                        if (link.count < 95) {
//                                            linkToUpdate = link
//                                            break
//                                        }
//                                    }
//
//                                    if (linkToUpdate != null) {
//                                        linkToUpdate.count += 1
//                                        firestoreRepository.updateLink(event.value?._id!!, linkToUpdate){
//                                            if (it){
//                                                val _enrollUser=EnrollUser(event_id = enrollUser.event_id, response = enrollUser.response, link = linkToUpdate?.link!!)
//                                                enrollUser(_enrollUser)
//                                            }
//                                        }
//                                    } else {
//                                        Log.d("LinkUpdate", "No links found with count < 95.")
//                                    }
//                                }
//                            }
                        }
                    }
                }

            }

        }
        else{
            enrollUser(enrollUser)
        }
    }

//    private suspend fun sendLinkToMail(meetLinks: MeetLinks){
//        val currentUser=PrefManager.getUserProfile()
//        val (formattedDate, formattedTime) = formatTimestamp(eventDetails.value?.time!!)
//
//        val mail= EventMeetInvite(
//            mail_type = "EVENT-MEET-INVITE",
//            email = PrefManager.getUserSignUpEmail()!!,
//            user_name = currentUser?.name!!.capitalize(),
//            event_title = eventDetails.value?.title!!,
//            date_time = "$formattedDate || $formattedTime",
//            venue = "Google Meet",
//            link = meetLinks.link
//        )
//        val response = mailApiService.sendEventMeetInviteMail(mail)
//        Timber.tag("MailService").d("Mail  : $mail")
//        if (response.isSuccessful) {
//            withContext(Dispatchers.Main) {
//                Timber.tag("MailService").d("Mail sending to ${mail.email} Successful : ${response.body()}")
//                _enrollResult.value = true
//            }
//        } else {
//            withContext(Dispatchers.Main) {
//                Timber.tag("MailService").d("Mail sending to ${mail.email} failed: ${response.body()}")
//                _enrollResult.value = false
//            }
//        }
//    }

    fun formatTimestamp(timestamp: Long): Pair<String, String> {
        val date = Date(timestamp)
        val dateFormat = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val formattedDate = dateFormat.format(date)
        val formattedTime = timeFormat.format(date)
        return Pair(formattedDate, formattedTime)
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
        }
        catch (e: IOException) {
            Log.d("signupResult",e.message.toString())
            _progressState.value = false
            _normalErrorMessage.value = "No internet connection."
        }
        catch (e: Exception) {
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