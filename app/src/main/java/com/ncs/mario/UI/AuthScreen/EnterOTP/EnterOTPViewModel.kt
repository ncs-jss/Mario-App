package com.ncs.mario.UI.AuthScreen.EnterOTP

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EnterOTPViewModel @Inject constructor() : ViewModel() {

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _otpResult = MutableLiveData<Boolean>()
    val otpResult: LiveData<Boolean> get() = _otpResult

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
        countDownTimer = object : CountDownTimer(180000, 1000) {
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
        _otpResult.value = true
    }

    override fun onCleared() {
        super.onCleared()
        countDownTimer.cancel()
    }

}
