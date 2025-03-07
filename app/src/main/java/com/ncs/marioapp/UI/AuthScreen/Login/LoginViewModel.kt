package com.ncs.marioapp.UI.AuthScreen.Login

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.ncs.marioapp.Domain.Api.AuthApiService
import com.ncs.marioapp.Domain.Api.ProfileApiService
import com.ncs.marioapp.Domain.HelperClasses.PrefManager
import com.ncs.marioapp.Domain.Models.LoginBody
import com.ncs.marioapp.Domain.Models.ServerResponse
import com.ncs.marioapp.Domain.Models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(val authApiService: AuthApiService, val profileApiService: ProfileApiService) : ViewModel() {

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    private val _progressState = MutableLiveData<Boolean>(false)
    val progressState: LiveData<Boolean> get() = _progressState

    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> get() = _loginResult

    fun login() {
        val emailValue = email.value?.trim()
        val passwordValue = password.value?.trim()

        if (emailValue.isNullOrEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()) {
            _errorMessage.value = "Enter a valid email address."
            return
        }

        if (passwordValue.isNullOrEmpty()) {
            _errorMessage.value = "Password can't be empty."
            return
        }

        performLogin(emailValue,passwordValue)
    }

    private fun performLogin(email:String, password:String) {
        viewModelScope.launch {
            _progressState.postValue(true)
            try {
                val response = authApiService.login(LoginBody(email = email, password = password))
                if (response.isSuccessful) {
                    Log.d("signupResult", "Login Successful: ${response.body()}")
                    response.body()?.get("user_id")?.let { PrefManager.setUserID(it.asString) }
                    PrefManager.setUserSignUpEmail(email)
                    PrefManager.setToken(response.body()!!.get("token").asString)
                    fetchUserDetails()
                } else {
                    val errorResponse = response.errorBody()?.string()
                    val loginResponse = Gson().fromJson(errorResponse, ServerResponse::class.java)
                    _errorMessage.value = "Authentication: "+loginResponse.message
                    Log.d("signupResult", "Login Failed: ${loginResponse.message}")
                    _loginResult.value = false
                }
            } catch (e: SocketTimeoutException) {
                Log.e("signupResult", "Request timed out: ${e.message}")
                _errorMessage.value = "Network timeout. Please try again."
                _loginResult.value = false
            } catch (e: IOException) {
                Log.e("signupResult", "Network error: ${e.message}")
                _errorMessage.value = "Network error. Please check your connection."
                _loginResult.value = false
            } catch (e: Exception) {
                Log.e("signupResult", "Error: ${e.message}")
                _errorMessage.value = "Something went wrong. Please try again."
                _loginResult.value = false
            } finally {
                _progressState.postValue(false)
            }
        }
    }

    fun fetchUserDetails() {
        _progressState.value=true
        viewModelScope.launch {
            try {
                val response = profileApiService.getMyDetails()
                if (response.isSuccessful) {
                    response.body()?.let {
                        _loginResult.value = true
                        _errorMessage.value = "Login Successful"
                        val user = Gson().fromJson(it.toString(), User::class.java)
                        PrefManager.setUserProfile(user.profile)

                        PrefManager.setUserProfileForCache(user.profile)
                        _progressState.value = false
                    }
                } else {
                    val errorResponse = response.errorBody()?.string()
                    val loginResponse = Gson().fromJson(errorResponse, ServerResponse::class.java)
                    _errorMessage.value = loginResponse.message
                    Log.d("signupResult", "Login Failed: ${loginResponse.message}")
                    _loginResult.value = false
                    _progressState.value = false
                }
            } catch (e: SocketTimeoutException) {
                _errorMessage.value = "Network timeout. Please try again."
                _progressState.value = false
                _loginResult.value = false
            } catch (e: IOException) {
                _errorMessage.value = "Network error. Please check your connection."
                _progressState.value = false
                _loginResult.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Something went wrong. Please try again."
                _progressState.value = false
                _loginResult.value = false
            }
        }
    }

}