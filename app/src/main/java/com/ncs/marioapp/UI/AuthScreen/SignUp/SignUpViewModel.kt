package com.ncs.marioapp.UI.AuthScreen.SignUp

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.ncs.marioapp.Domain.Api.AuthApiService
import com.ncs.marioapp.Domain.HelperClasses.PrefManager
import com.ncs.marioapp.Domain.Models.ServerResponse
import com.ncs.marioapp.Domain.Models.SignUpBody
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(val authApiService: AuthApiService) : ViewModel() {

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _progressState = MutableLiveData<Boolean>(false)
    val progressState: LiveData<Boolean> get() = _progressState

    private val _signupResult = MutableLiveData<Boolean>()
    val signupResult: LiveData<Boolean> get() = _signupResult


    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val confirmPassword = MutableLiveData<String>()
    val phone_num = MutableLiveData<String>(null)


    fun validateAndSignup() {
        val emailValue = email.value?.trim()
        val passwordValue = password.value?.trim()
        val confirmPasswordValue = confirmPassword.value?.trim()
        val phone_numValue=phone_num.value?.trim()

        if (emailValue.isNullOrEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()) {
            _errorMessage.value = "Enter a valid email address."
            return
        }

        if (phone_numValue.isNullOrEmpty()) {
            _errorMessage.value = "Phone Number can't be empty"
            return
        }

        if (phone_numValue.length!=10) {
            _errorMessage.value = "Enter a valid Phone Number"
            return
        }

        if (passwordValue.isNullOrEmpty() || passwordValue.length < 6) {
            _errorMessage.value = "Password must be at least 6 characters long."
            return
        }
        if(passwordValue.length > 72){
            _errorMessage.value = "Password must not be greater than 72 characters."
            return
        }

        if (passwordValue != confirmPasswordValue) {
            _errorMessage.value = "Passwords do not match."
            return
        }



        performSignup(emailValue,phone_numValue ,passwordValue)
    }

    private fun performSignup(email: String, phone:String,password: String) {
//        viewModelScope.launch {
//            _progressState.postValue(true)
//            val response = authApiService.signUp(SignUpBody(email,phone,password))
//            if (response.isSuccessful) {
//                Log.d("signupResult","Signup Successful : ${response.body()}")
//                _signupResult.value = true
//                _progressState.postValue(false)
//                response.body()?.get("user_id")?.let { PrefManager.setUserID(it.asString) }
//                PrefManager.setUserSignUpEmail(email)
//            } else {
//                val errorResponse = response.errorBody()?.string()
//                val signupresponse = Gson().fromJson(errorResponse, ServerResponse::class.java)
//                _errorMessage.value = signupresponse.message
//                Log.d("signupResult", "Signup Failed: ${signupresponse.message}")
//                _signupResult.value = false
//                _progressState.postValue(false)
//            }
//        }
        viewModelScope.launch {
            _progressState.postValue(true)
            try {
                val response = authApiService.signUp(SignUpBody(email, phone, password))

                if (response.isSuccessful) {
                    Log.d("signupResult", "Signup Successful : ${response.body()}")
                    _signupResult.value = true
                    response.body()?.get("user_id")?.let { PrefManager.setUserID(it.asString) }
                    PrefManager.setUserSignUpEmail(email)
                } else {
                    val errorResponse = response.errorBody()?.string()
                    val signupResponse = Gson().fromJson(errorResponse, ServerResponse::class.java)
                    _errorMessage.value = signupResponse.message
                    Log.d("signupResult", "Signup Failed: ${signupResponse.message}")
                    _signupResult.value = false
                }
            } catch (e: SocketTimeoutException) {
                Log.e("signupResult", "Request timed out: ${e.message}")
                _errorMessage.value = "Network timeout. Please try again."
                _signupResult.value = false
            } catch (e: Exception) {
                Log.e("signupResult", "Error: ${e.message}")
                _errorMessage.value = "Something went wrong. Please try again."
                _signupResult.value = false
            } finally {
                _progressState.postValue(false)
            }
        }

    }


}