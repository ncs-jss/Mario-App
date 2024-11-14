package com.ncs.marioapp.UI.AuthScreen.ForgotPassword

import android.os.CountDownTimer
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.ncs.marioapp.Domain.Api.AuthApiService
import com.ncs.marioapp.Domain.HelperClasses.PrefManager
import com.ncs.marioapp.Domain.Models.ForgotPasswordBody
import com.ncs.marioapp.Domain.Models.ResendOTPBody
import com.ncs.marioapp.Domain.Models.ResetPassBody
import com.ncs.marioapp.Domain.Models.ServerResponse
import com.ncs.marioapp.Domain.Models.VerifyOTP
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject


@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(val authApiService: AuthApiService) : ViewModel() {

    private val _errorMessageForgotPassFragment = MutableLiveData<String?>()
    val errorMessageForgotPassFragment: LiveData<String?> get() = _errorMessageForgotPassFragment

    val email = MutableLiveData<String>()

    private val _progressStateForgotPassFragment = MutableLiveData<Boolean>(false)
    val progressStateForgotPassFragment: LiveData<Boolean> get() = _progressStateForgotPassFragment

    private val _forgotPassFragmentResult = MutableLiveData<Boolean>()
    val forgotPassFragmentResult: LiveData<Boolean> get() = _forgotPassFragmentResult

    private val _errorMessageOTPFragment = MutableLiveData<String?>()
    val errorMessageOTPFragment: LiveData<String?> get() = _errorMessageOTPFragment

    private val _otpResultOTPFragment = MutableLiveData<Boolean>()
    val otpResultOTPFragment: LiveData<Boolean> get() = _otpResultOTPFragment

    private val _progressStateOTPFragment = MutableLiveData<Boolean>(false)
    val progressStateOTPFragment: LiveData<Boolean> get() = _progressStateOTPFragment

    private val _timerText = MutableLiveData<String>()
    val timerText: LiveData<String> get() = _timerText

    private val _isResendVisible = MutableLiveData<Boolean>(false)
    val isResendVisible: LiveData<Boolean> get() = _isResendVisible

    private val _errorMessageSetPassFragment = MutableLiveData<String?>()
    val errorMessageSetPassFragment: LiveData<String?> get() = _errorMessageSetPassFragment

    private val _progressStateSetPassFragment = MutableLiveData<Boolean>(false)
    val progressStateSetPassFragment: LiveData<Boolean> get() = _progressStateSetPassFragment

    private val _setPassFragmentResult = MutableLiveData<Boolean>()
    val setPassFragmentResult: LiveData<Boolean> get() = _setPassFragmentResult

    val password = MutableLiveData<String>()
    val confirmPassword = MutableLiveData<String>()

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
            _errorMessageOTPFragment.value = "Enter a valid OTP."
        } else {
            performOTPValidation(otp)
        }
    }


    fun validateEmail() {
        val emailValue = email.value?.trim()

        if (emailValue.isNullOrEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()) {
            _errorMessageForgotPassFragment.value = "Enter a valid email address."
            return
        }

        sendEmail(emailValue)
    }

    private fun sendEmail(emailValue: String) {
        viewModelScope.launch {
            _progressStateForgotPassFragment.postValue(true)
            try {
                val response = authApiService.forgotPassword(ForgotPasswordBody(email = emailValue))
                if (response.isSuccessful) {
                    _forgotPassFragmentResult.value = true
                    _errorMessageForgotPassFragment.value = "OTP Sent Successfully"
                    response.body()?.get("user_id")?.let { PrefManager.setUserID(it.asString) }
                    PrefManager.setUserSignUpEmail(emailValue)
                } else {
                    val errorResponse = response.errorBody()?.string()
                    val loginResponse = Gson().fromJson(errorResponse, ServerResponse::class.java)
                    _errorMessageForgotPassFragment.value = loginResponse.message
                    _forgotPassFragmentResult.value = false
                }
            } catch (e: SocketTimeoutException) {
                _errorMessageForgotPassFragment.value = "Network timeout. Please try again."
                _forgotPassFragmentResult.value = false
            } catch (e: IOException) {
                _errorMessageForgotPassFragment.value = "Network error. Please check your connection."
                _forgotPassFragmentResult.value = false
            } catch (e: Exception) {
                _errorMessageForgotPassFragment.value = "Something went wrong. Please try again."
                _forgotPassFragmentResult.value = false
            } finally {
                _progressStateForgotPassFragment.postValue(false)
            }
        }
    }

    private fun performOTPValidation(otp: String) {
        viewModelScope.launch {
            _progressStateOTPFragment.postValue(true)
            try {
                val response = authApiService.verifyResetOTP(VerifyOTP(user_id = PrefManager.getUserID()!!, otp = otp.toInt()))

                if (response.isSuccessful) {
                    _otpResultOTPFragment.value = true
                    response.body()?.get("temp_token")?.let { PrefManager.setToken(it.asString) }
                } else {
                    val errorResponse = response.errorBody()?.string()
                    val otpValidationResponse = Gson().fromJson(errorResponse, ServerResponse::class.java)
                    _errorMessageOTPFragment.value = otpValidationResponse.message
                    _otpResultOTPFragment.value = false
                }
            } catch (e: SocketTimeoutException) {
                _errorMessageOTPFragment.value = "Network timeout. Please try again."
                _otpResultOTPFragment.value = false
            } catch (e: Exception) {
                _errorMessageOTPFragment.value = "Something went wrong. Please try again."
                _otpResultOTPFragment.value = false
            } finally {
                _progressStateOTPFragment.postValue(false)
            }
        }

    }

    fun resendOTP() {
        viewModelScope.launch {
            _errorMessageOTPFragment.value = "Sending OTP again..."
            try {
                val response = authApiService.resendOTP(ResendOTPBody(user_id = PrefManager.getUserID()!!, action = "RESETPASSWORD"))

                if (response.isSuccessful) {
                    _errorMessageOTPFragment.value = "OTP sent successfully..."
                } else {
                    val errorResponse = response.errorBody()?.string()
                    _errorMessageOTPFragment.value = "Failed to resend OTP..."
                }
            } catch (e: SocketTimeoutException) {
                _errorMessageOTPFragment.value = "Network timeout. Please try again."
            } catch (e: Exception) {
                _errorMessageOTPFragment.value = "Something went wrong. Please try again."
            }
        }

    }

    override fun onCleared() {
        super.onCleared()
        countDownTimer.cancel()
    }

    fun validateAndSetPassword() {
        val passwordValue = password.value?.trim()
        val confirmPasswordValue = confirmPassword.value?.trim()

        if (passwordValue.isNullOrEmpty() || passwordValue.length < 6 || passwordValue.length > 72) {
            _errorMessageSetPassFragment.value = "Password must be at least 6 characters long."
            return
        }

        if (passwordValue != confirmPasswordValue) {
            _errorMessageSetPassFragment.value = "Passwords do not match."
            return
        }

        performPasswordReset(passwordValue)
    }

    private fun performPasswordReset(password: String) {

        viewModelScope.launch {
            _progressStateSetPassFragment.postValue(true)
            try {
                val response = authApiService.resetPassword(ResetPassBody(PrefManager.getToken()!!, password))

                if (response.isSuccessful) {
                    _setPassFragmentResult.value = true
                } else {
                    val errorResponse = response.errorBody()?.string()
                    val signupResponse = Gson().fromJson(errorResponse, ServerResponse::class.java)
                    _errorMessageSetPassFragment.value = signupResponse.message
                    _setPassFragmentResult.value = false
                }
            } catch (e: SocketTimeoutException) {
                _errorMessageSetPassFragment.value = "Network timeout. Please try again."
                _setPassFragmentResult.value = false
            } catch (e: Exception) {
                _errorMessageSetPassFragment.value = "Something went wrong. Please try again."
                _setPassFragmentResult.value = false
            } finally {
                _progressStateSetPassFragment.postValue(false)
            }
        }

    }

}