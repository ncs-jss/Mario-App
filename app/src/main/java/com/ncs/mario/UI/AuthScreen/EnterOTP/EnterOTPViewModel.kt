package com.ncs.mario.UI.AuthScreen.EnterOTP

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.ncs.mario.Domain.Api.AuthApiService
import com.ncs.mario.Domain.HelperClasses.PrefManager
import com.ncs.mario.Domain.Models.ResendOTPBody
import com.ncs.mario.Domain.Models.ServerResponse
import com.ncs.mario.Domain.Models.SignUpBody
import com.ncs.mario.Domain.Models.VerifyOTP
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class EnterOTPViewModel @Inject constructor(val authApiService: AuthApiService) : ViewModel() {

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _otpResult = MutableLiveData<Boolean>()
    val otpResult: LiveData<Boolean> get() = _otpResult

    private val _progressState = MutableLiveData<Boolean>(false)
    val progressState: LiveData<Boolean> get() = _progressState

    private val _timerText = MutableLiveData<String>()
    val timerText: LiveData<String> get() = _timerText

    private val _isResendVisible = MutableLiveData<Boolean>(false)
    val isResendVisible: LiveData<Boolean> get() = _isResendVisible

    val et1 = MutableLiveData<String>()
    val et2 = MutableLiveData<String>()
    val et3 = MutableLiveData<String>()
    val et4 = MutableLiveData<String>()
    val et5 = MutableLiveData<String>()
    val et6 = MutableLiveData<String>()

    private lateinit var countDownTimer: CountDownTimer

    init {
        startTimer()
    }

    fun setIsResendVisible(newValue: Boolean){
        _isResendVisible.value = newValue
    }

    fun startTimer() {
        countDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / 1000 / 60
                val seconds = millisUntilFinished / 1000 % 60
                _timerText.value = String.format("Resend OTP in %02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                _timerText.value = "00:00"
                _isResendVisible.value = true
            }
        }
        countDownTimer.start()
    }

    fun validateAndPerformOTPVerification(otp: String) {
        Log.d("checkOTP", otp.toString())
        if (otp.length != 6 || otp.contains(" ")) {
            _errorMessage.value = "Enter a valid OTP."
        } else {
            performOTPValidation(otp)
        }
    }

    private fun performOTPValidation(otp: String) {
//        viewModelScope.launch {
//            _progressState.postValue(true)
//            val response = authApiService.verifyOTP(VerifyOTP(user_id = PrefManager.getUserID()!!, otp = otp.toInt()))
//            if (response.isSuccessful) {
//                Log.d("signupResult","OTP Successful : ${response.body()}")
//                _otpResult.value = true
//                _progressState.postValue(false)
//            } else {
//                val errorResponse = response.errorBody()?.string()
//                val otpValidationResponse = Gson().fromJson(errorResponse, ServerResponse::class.java)
//                _errorMessage.value = otpValidationResponse.message
//                Log.d("signupResult", "OTP Failed: ${otpValidationResponse.message}")
//                _otpResult.value = false
//                _progressState.postValue(false)
//            }
//        }
        viewModelScope.launch {
            _progressState.postValue(true)
            try {
                val response = authApiService.verifyOTP(VerifyOTP(user_id = PrefManager.getUserID()!!, otp = otp.toInt()))

                if (response.isSuccessful) {
                    Log.d("signupResult", "OTP Successful : ${response.body()}")
                    _otpResult.value = true
                    PrefManager.setToken(response.body()!!.get("token").asString)
                } else {
                    val errorResponse = response.errorBody()?.string()
                    val otpValidationResponse = Gson().fromJson(errorResponse, ServerResponse::class.java)
                    _errorMessage.value = otpValidationResponse.message
                    Log.d("signupResult", "OTP Failed: ${otpValidationResponse.message}")
                    _otpResult.value = false
                }
            } catch (e: SocketTimeoutException) {
                Log.e("signupResult", "Request timed out: ${e.message}")
                _errorMessage.value = "Network timeout. Please try again."
                _otpResult.value = false
            } catch (e: Exception) {
                Log.e("signupResult", "Error: ${e.message}")
                _errorMessage.value = "Something went wrong. Please try again."
                _otpResult.value = false
            } finally {
                _progressState.postValue(false)
            }
        }

    }

    fun resendOTP() {
//        viewModelScope.launch {
//            _errorMessage.value = "Sending OTP again..."
//            val response = authApiService.resendOTP(ResendOTPBody(user_id = PrefManager.getUserID()!!, action = "RESETPASSWORD"))
//            if (response.isSuccessful) {
//                Log.d("signupResult","OTP Resend Successful : ${response.body()}")
//                _errorMessage.value = "OTP sent successfully..."
//            } else {
//                Log.d("signupResult","OTP Resend failed : ${response.body()}")
//                _errorMessage.value = "Failed to resend OTP..."
//            }
//        }
        viewModelScope.launch {
            _errorMessage.value = "Sending OTP again..."
            try {
                val response = authApiService.resendOTP(ResendOTPBody(user_id = PrefManager.getUserID()!!, action = "RESETPASSWORD"))

                if (response.isSuccessful) {
                    Log.d("signupResult", "OTP Resend Successful : ${response.body()}")
                    _errorMessage.value = "OTP sent successfully..."
                } else {
                    val errorResponse = response.errorBody()?.string()
                    Log.d("signupResult", "OTP Resend failed: $errorResponse")
                    _errorMessage.value = "Failed to resend OTP..."
                }
            } catch (e: SocketTimeoutException) {
                Log.e("signupResult", "Request timed out: ${e.message}")
                _errorMessage.value = "Network timeout. Please try again."
            } catch (e: Exception) {
                Log.e("signupResult", "Error: ${e.message}")
                _errorMessage.value = "Something went wrong. Please try again."
            }
        }

    }

    override fun onCleared() {
        super.onCleared()
        countDownTimer.cancel()
    }

}
