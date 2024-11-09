package com.ncs.marioapp.UI.WaitScreen

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.ncs.marioapp.Domain.Api.ProfileApiService
import com.ncs.marioapp.Domain.HelperClasses.PrefManager
import com.ncs.marioapp.Domain.Models.ServerResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class WaitScreenViewModel @Inject constructor(val profileApiService: ProfileApiService) : ViewModel() {

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _kycStatus = MutableLiveData<String>(null)
    val kycStatus: LiveData<String> get() = _kycStatus

    private val _kycRequestStatus = MutableLiveData<Boolean>(null)
    val kycRequestStatus: LiveData<Boolean> get() = _kycRequestStatus

    private val handler = Handler(Looper.getMainLooper())
    private val kycRunnable = object : Runnable {
        override fun run() {
            getKYCStatus()
            handler.postDelayed(this, 20000)
        }
    }

    init {
        startKYCStatusCheck()
    }

    private fun startKYCStatusCheck() {
        kycRunnable.run()
    }

    fun getKYCStatus(){
        viewModelScope.launch {
            try {
                val response = profileApiService.getKYCHeader()
                if (response.isSuccessful) {
                    response.body()?.get("approvalToken")?.let { PrefManager.setKYCHeaderToken(it.asString) }
                    val status=response.body()?.get("is_approved")
                    if (status != null) {
                        _kycStatus.value=status.asString
                    }
                }  else {
                    val errorResponse = response.errorBody()?.string()
                    val loginResponse = Gson().fromJson(errorResponse, ServerResponse::class.java)
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

    fun requestKYCToPending(){
        viewModelScope.launch {
            try {
                val response = profileApiService.requestKYCToPending()
                if (response.isSuccessful) {
                    _kycRequestStatus.value=true
                }  else {
                    val errorResponse = response.errorBody()?.string()
                    val loginResponse = Gson().fromJson(errorResponse, ServerResponse::class.java)
                    _kycRequestStatus.value=false
                }
            } catch (e: SocketTimeoutException) {
                _errorMessage.value = "Network timeout. Please try again."
                _kycRequestStatus.value=false
            } catch (e: IOException) {
                _errorMessage.value = "Network error. Please check your connection."
                _kycRequestStatus.value=false
            } catch (e: Exception) {
                _errorMessage.value = "Something went wrong. Please try again."
                _kycRequestStatus.value=false
            }
        }
    }

    fun stopKYCStatusCheck() {
        handler.removeCallbacks(kycRunnable)
    }

}