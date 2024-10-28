package com.ncs.mario.UI.StartScreen

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
class StartScreenViewModel @Inject constructor(
    val profileApiService: ProfileApiService
) : ViewModel() {

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _progressState = MutableLiveData<Boolean>(false)
    val progressState: LiveData<Boolean> get() = _progressState

    private val _userDetailsResult = MutableLiveData<Boolean>()
    val userDetailsResult: LiveData<Boolean> get() = _userDetailsResult

    private val _userDetails = MutableLiveData<User>(null)
    val userDetails: LiveData<User> get() = _userDetails

    private val _kycStatus = MutableLiveData<String>(null)
    val kycStatus: LiveData<String> get() = _kycStatus

    init {
        getFCMToken()
    }

    suspend fun fetchUserKYCHeaderToken(): String? {
        return try {
            val response = profileApiService.getKYCHeader()
            if (response.isSuccessful) {
                response.body()?.get("approvalToken")?.let { PrefManager.setKYCHeaderToken(it.asString) }
                response.body()?.get("is_approved")?.asString.also {
                    _kycStatus.value = it
                }
            } else {
                handleError(response.errorBody()?.string())
                null
            }
        } catch (e: SocketTimeoutException) {
            _errorMessage.value = "Network timeout. Please try again."
            null
        } catch (e: IOException) {
            _errorMessage.value = "Network error. Please check your connection."
            null
        } catch (e: Exception) {
            _errorMessage.value = "Something went wrong. Please try again."
            null
        }
    }

    suspend fun fetchUserDetails(): User? {
        return try {
            val response = profileApiService.getMyDetails()
            if (response.isSuccessful) {
                response.body()?.let {
                    val user = Gson().fromJson(it.toString(), User::class.java)
                    _userDetails.value = user
                    PrefManager.setUserProfile(user.profile)
                    _userDetailsResult.value = true
                    user
                }
            } else {
                handleError(response.errorBody()?.string())
                null
            }
        } catch (e: SocketTimeoutException) {
            _errorMessage.value = "Network timeout. Please try again."
            null
        } catch (e: IOException) {
            _errorMessage.value = "Network error. Please check your connection."
            null
        } catch (e: Exception) {
            _errorMessage.value = "Something went wrong. Please try again."
            null
        }
    }


    private fun handleError(errorResponse: String?) {
        val loginResponse = Gson().fromJson(errorResponse, ServerResponse::class.java)
        _userDetails.value = User(message = "", profile = Profile(), success = false)
        _userDetailsResult.value = false
        _errorMessage.value = loginResponse.message
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