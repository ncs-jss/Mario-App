package com.ncs.mario.UI.StartScreen

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.ncs.mario.Domain.Api.AuthApiService
import com.ncs.mario.Domain.Api.ProfileApiService
import com.ncs.mario.Domain.HelperClasses.PrefManager
import com.ncs.mario.Domain.Models.LoginBody
import com.ncs.mario.Domain.Models.Profile
import com.ncs.mario.Domain.Models.ServerResponse
import com.ncs.mario.Domain.Models.SetFCMTokenBody
import com.ncs.mario.Domain.Models.User
import com.ncs.mario.Domain.Models.VerifyOTP
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class StartScreenViewModel @Inject constructor(val profileApiService: ProfileApiService) : ViewModel() {

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _progressState = MutableLiveData<Boolean>(false)
    val progressState: LiveData<Boolean> get() = _progressState

    private val _userDetailsResult = MutableLiveData<Boolean>()
    val userDetailsResult: LiveData<Boolean> get() = _userDetailsResult

    private val _userDetails = MutableLiveData<User>(null)
    val userDetails: LiveData<User> get() = _userDetails

    private val _kycStatus = MutableLiveData<Boolean>(null)
    val kycStatus: LiveData<Boolean> get() = _kycStatus

    init {
        getFCMToken()
    }

    fun fetchUserDetails() {
        viewModelScope.launch {
            try {
                val response = profileApiService.getMyDetails()
                if (response.isSuccessful) {
                    Log.d("signupResult", "Details found Successful: ${response.body()}")
                    val loginResponse = response.body().toString()
                    val User= Gson().fromJson(loginResponse, User::class.java)
                    _userDetails.value=User
                    _userDetailsResult.value = true
                } else {
                    val errorResponse = response.errorBody()?.string()
                    val loginResponse = Gson().fromJson(errorResponse, ServerResponse::class.java)
                    Log.d("signupResult", "Details found Failed: ${loginResponse.message}")
                    _userDetails.value=User(message = "", profile = Profile(),success = false)
                    _userDetailsResult.value = false
                }
            } catch (e: SocketTimeoutException) {
                Log.e("signupResult", "Request timed out: ${e.message}")
                _errorMessage.value = "Network timeout. Please try again."
            } catch (e: IOException) {
                Log.e("signupResult", "Network error: ${e.message}")
                 _errorMessage.value = "Network error. Please check your connection."
            } catch (e: Exception) {
                Log.e("signupResult", "Error: ${e.message}")
                 _errorMessage.value = "Something went wrong. Please try again."
            }
        }

    }

    fun getKYCStatus(){
        viewModelScope.launch {
            try {
                val response = profileApiService.getKycStatus()
                if (response.isSuccessful) {
                    Log.d("signupResult", "KYC Successful: ${response.body()}")
                    val status=response.body()?.get("approved")
                    if (status!!.asString=="ACCEPT"){
                        _kycStatus.value=true
                    }
                    else{
                        _kycStatus.value=false
                    }
                } else {
                    val errorResponse = response.errorBody()?.string()
                    val loginResponse = Gson().fromJson(errorResponse, ServerResponse::class.java)
                    Log.d("signupResult", "KYC Failed: ${loginResponse.message}")
                    _userDetailsResult.value = false
                }
            } catch (e: SocketTimeoutException) {
                Log.e("signupResult", "Request timed out: ${e.message}")
                _errorMessage.value = "Network timeout. Please try again."
            } catch (e: IOException) {
                Log.e("signupResult", "Network error: ${e.message}")
                _errorMessage.value = "Network error. Please check your connection."
            } catch (e: Exception) {
                Log.e("signupResult", "Error: ${e.message}")
                _errorMessage.value = "Something went wrong. Please try again."
            }
        }
    }

    fun setFCMToken(newFCMToken:String) {
        viewModelScope.launch {
            try {
                val response = profileApiService.setFCMToken(payload = SetFCMTokenBody(newFCMToken))
                if (response.isSuccessful) {
                    Log.d("signupResult", "FCM Token Set: ${response.body()}")
                } else {
                    val errorResponse = response.errorBody()?.string()
                    val loginResponse = Gson().fromJson(errorResponse, ServerResponse::class.java)
                    Log.d("signupResult", "FCM Token Set Failed: ${loginResponse.message}")
                }
            } catch (e: Exception) {
                _userDetailsResult.value = false
            } finally {
            }
        }
    }


    private fun getFCMToken() {

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@addOnCompleteListener
            }
            val actualFCM = task.result
            PrefManager.setFCMToken(actualFCM)
            setFCMToken(actualFCM)
        }

    }

}