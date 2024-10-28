package com.ncs.mario.UI.MainScreen.Score

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.ncs.mario.Domain.Api.MerchApi
import com.ncs.mario.Domain.Api.ProfileApiService
import com.ncs.mario.Domain.HelperClasses.PrefManager
import com.ncs.mario.Domain.Interfaces.EventRepository
import com.ncs.mario.Domain.Models.Events.ParticipatedEventResponse
import com.ncs.mario.Domain.Models.MyOrderResponse
import com.ncs.mario.Domain.Models.Profile
import com.ncs.mario.Domain.Models.Score.Transaction
import com.ncs.mario.Domain.Models.Score.TransactionsResponse
import com.ncs.mario.Domain.Models.ServerResponse
import com.ncs.mario.Domain.Models.ServerResult
import com.ncs.mario.Domain.Models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class ScoreViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val profileApiService: ProfileApiService,
    private val merchApi: MerchApi
) : ViewModel() {

    private val _getEventsResponse = MutableLiveData<ServerResult<ParticipatedEventResponse>>()
    val getEventsResponse: LiveData<ServerResult<ParticipatedEventResponse>> = _getEventsResponse

    private val _progressState = MutableLiveData<Boolean>(false)
    val progressState: LiveData<Boolean> get() = _progressState

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _coinsStatementResult = MutableLiveData<Boolean>()
    val coinsStatementResult: LiveData<Boolean> get() = _coinsStatementResult

    private val _coinsStatement = MutableLiveData<List<Transaction>>(null)
    val coinsStatement: LiveData<List<Transaction>> get() = _coinsStatement

    private val _merchResponse = MutableLiveData<MyOrderResponse>(null)
    val merchResponse: LiveData<MyOrderResponse> get() = _merchResponse

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

    fun getUserCoinStatement(){
        _progressState.value = true
        viewModelScope.launch {
            try {
                val response = profileApiService.getUserCoinStatement()
                if (response.isSuccessful) {
                    val res = response.body().toString()
                    val transactionsResponse= Gson().fromJson(res, TransactionsResponse::class.java)
                    _coinsStatement.value=transactionsResponse.transactions
                    _coinsStatementResult.value = true
                    _progressState.value = false
                } else {
                    val errorResponse = response.errorBody()?.string()
                    val loginResponse = Gson().fromJson(errorResponse, ServerResponse::class.java)
                    _errorMessage.value = loginResponse.message
                    _coinsStatementResult.value = false
                    _progressState.value = false
                }
            } catch (e: SocketTimeoutException) {
                _progressState.value = false
                _coinsStatementResult.value = false
                _errorMessage.value = "Network timeout. Please try again."
            } catch (e: IOException) {
                _progressState.value = false
                _coinsStatementResult.value = false
                _errorMessage.value = "Network error. Please check your connection."
            } catch (e: Exception) {
                _progressState.value = false
                _coinsStatementResult.value = false
                _errorMessage.value = "Something went wrong. Please try again."
            }
        }
    }

    fun getMyMerch(){
        viewModelScope.launch {
            try {
                val response = merchApi.getMyOrders()
                if (response.isSuccessful) {
                    val res= response.body().toString()
                    val respo = Gson().fromJson(res, MyOrderResponse::class.java)
                    _merchResponse.value = respo
                }else{
                    val error = response.errorBody()?.string()
                    val respo = Gson().fromJson(error, MyOrderResponse::class.java)
                    _errorMessage.value = respo.message
                    _merchResponse.value = MyOrderResponse(false,respo.message,null )
                }
            }
            catch (e: SocketTimeoutException) {
                Log.e("merchResult", "Request timed out: ${e.message}")
                _errorMessage.value = "Network timeout. Please try again."
            } catch (e: IOException) {
                Log.e("merchResult", "Network error: ${e.message}")
                _errorMessage.value = "Network error. Please check your connection."
            } catch (e: Exception) {
                Log.e("merchResult", "Error: ${e.message}")
                _errorMessage.value = "Something went wrong. Please try again."
            }
        }
    }

    fun resetErrorMessage(){
        _errorMessage.value=null
    }

}