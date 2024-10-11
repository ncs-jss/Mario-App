package com.ncs.mario.UI.StartScreen

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.ncs.mario.Domain.Api.AuthApiService
import com.ncs.mario.Domain.Api.ProfileApiService
import com.ncs.mario.Domain.HelperClasses.PrefManager
import com.ncs.mario.Domain.Models.LoginBody
import com.ncs.mario.Domain.Models.ServerResponse
import com.ncs.mario.Domain.Models.VerifyOTP
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartScreenViewModel @Inject constructor(val profileApiService: ProfileApiService) : ViewModel() {

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _progressState = MutableLiveData<Boolean>(false)
    val progressState: LiveData<Boolean> get() = _progressState

    private val _userDetailsResult = MutableLiveData<Boolean>()
    val userDetailsResult: LiveData<Boolean> get() = _userDetailsResult


    fun fetchUserDetails() {
        viewModelScope.launch {
            val response = profileApiService.getMyDetails()
            if (response.isSuccessful) {
                Log.d("signupResult", "Details found Successful: ${response.body()}")
                _userDetailsResult.value = true
//                _loginResult.value = true
//                _errorMessage.value = "Login Successful"
            } else {
                val errorResponse = response.errorBody()?.string()
                val loginResponse = Gson().fromJson(errorResponse, ServerResponse::class.java)
                Log.d("signupResult", "Details found Failed: ${loginResponse.message}")
                _userDetailsResult.value = false
//                _errorMessage.value = loginResponse.message
//                _loginResult.value = false
            }
        }
    }

}