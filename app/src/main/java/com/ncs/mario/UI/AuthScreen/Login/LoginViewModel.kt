package com.ncs.mario.UI.AuthScreen.Login

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()


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

    }


}