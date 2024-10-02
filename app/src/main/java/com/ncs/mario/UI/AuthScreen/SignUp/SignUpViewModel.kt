package com.ncs.mario.UI.AuthScreen.SignUp

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor() : ViewModel() {

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _signupResult = MutableLiveData<Boolean>()
    val signupResult: LiveData<Boolean> get() = _signupResult


    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val confirmPassword = MutableLiveData<String>()


    fun validateAndSignup() {
        val emailValue = email.value?.trim()
        val passwordValue = password.value?.trim()
        val confirmPasswordValue = confirmPassword.value?.trim()

        if (emailValue.isNullOrEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()) {
            _errorMessage.value = "Enter a valid email address."
            return
        }

        if (passwordValue.isNullOrEmpty() || passwordValue.length < 6) {
            _errorMessage.value = "Password must be at least 6 characters long."
            return
        }

        if (passwordValue != confirmPasswordValue) {
            _errorMessage.value = "Passwords do not match."
            return
        }

        performSignup(emailValue, passwordValue)
    }

    private fun performSignup(email: String, password: String) {
        _signupResult.value = true
    }


}