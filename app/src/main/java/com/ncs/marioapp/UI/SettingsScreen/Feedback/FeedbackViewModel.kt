package com.ncs.marioapp.UI.SettingsScreen.Feedback

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.ncs.marioapp.Domain.Api.ReportApiService
import com.ncs.marioapp.Domain.Models.Report.ReportBody
import com.ncs.marioapp.Domain.Models.ServerResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class FeedbackViewModel @Inject constructor(
    private val reportApiService: ReportApiService
) : ViewModel(){

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _progressState = MutableLiveData<Boolean>(false)
    val progressState: LiveData<Boolean> get() = _progressState

    private val _feedbackResult = MutableLiveData<Boolean>()
    val feedbackResult: LiveData<Boolean> = _feedbackResult

    fun addReport(reportBody: ReportBody){
        viewModelScope.launch {
            _progressState.value = true
            try {
                val response = reportApiService.addReport(payload = reportBody)
                if (response.isSuccessful) {
                    _errorMessage.value="Your feedback was submitted. Thank you!"
                    _progressState.value = false
                    _feedbackResult.value=true
                } else {
                    val errorResponse = response.errorBody()?.string()
                    val loginResponse = Gson().fromJson(errorResponse, ServerResponse::class.java)
                    _progressState.value = false
                    _errorMessage.value=loginResponse.message
                    _feedbackResult.value=false
                }
            }catch (e: IOException) {
                Log.d("signupResult",e.message.toString())
                _progressState.value = false
                _feedbackResult.value=false
                _errorMessage.value = "Network error. Please check your connection."
            } catch (e: SocketTimeoutException) {
                _progressState.value = false
                _feedbackResult.value=false
                _errorMessage.value = "Network timeout. Please try again."
            } catch (e: Exception) {
                _progressState.value = false
                _feedbackResult.value=false
                _errorMessage.value = "Something went wrong. Please try again."
            }
        }
    }

}