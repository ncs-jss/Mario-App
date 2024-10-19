package com.ncs.mario.UI.MainScreen.Store

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.ncs.mario.Domain.Api.MerchApi
import com.ncs.mario.Domain.Models.Merch
import com.ncs.mario.Domain.Models.MerchResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class StoreViewModel @Inject constructor(val merchApi: MerchApi): ViewModel() {
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _progressState = MutableLiveData<Boolean>(false)
    val progressState: LiveData<Boolean> get() = _progressState

    private val _getMerch = MutableLiveData<MerchResponse>()
    val getMerch: LiveData<MerchResponse> get() = _getMerch

    private val _merchResult = MutableLiveData<Boolean>()
    val merchResult: LiveData<Boolean> get() = _merchResult

    fun getNCSMerch() {
        viewModelScope.launch {
            try {


            val response = merchApi.getMerch()
            if (response.isSuccessful) {
                Log.d("merchResult", "Merch ${response.body()}")
                val res= response.body().toString()
                val Merch =Gson().fromJson(res,MerchResponse::class.java)
                _getMerch.value = Merch
                _merchResult.value = true
            } else {
                val error = response.errorBody()?.string()
                _errorMessage.value = error
                _getMerch.value = MerchResponse(false,"", listOf(Merch()))
                _merchResult.value = false
            }
            }
            catch (e: SocketTimeoutException) {
                Log.e("merchResult", "Request timed out: ${e.message}")
                _errorMessage.value = "Network timeout. Please try again."
            } catch (e: IOException) {
                Log.e("merchResult", "Network error: ${e.message}")
                _errorMessage.value = "Network error. Please check your connection."
            } catch (e: Exception) {
                Log.e("merchResult", "Error: ${e.message}")
                _errorMessage.value = "Something went wrong. Please try again."
            }
        }
    }

}