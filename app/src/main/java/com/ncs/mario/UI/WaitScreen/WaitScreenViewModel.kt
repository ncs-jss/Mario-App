package com.ncs.mario.UI.WaitScreen

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.ncs.mario.Domain.Api.ProfileApiService
import com.ncs.mario.Domain.HelperClasses.PrefManager
import com.ncs.mario.Domain.Models.Profile
import com.ncs.mario.Domain.Models.ServerResponse
import com.ncs.mario.Domain.Models.SetFCMTokenBody
import com.ncs.mario.Domain.Models.User
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
                val response = profileApiService.getKycStatus()
                if (response.isSuccessful) {
                    val status=response.body()?.get("approved")
                    _kycStatus.value=status?.asString
                } else {
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

    fun stopKYCStatusCheck() {
        handler.removeCallbacks(kycRunnable)
    }

}