package com.ncs.mario.UI.MyRedemptionsScreen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.ncs.mario.Domain.Api.MerchApi
import com.ncs.mario.Domain.Models.Merch
import com.ncs.mario.Domain.Models.MerchResponse
import com.ncs.mario.Domain.Models.MyOrderData
import com.ncs.mario.Domain.Models.MyOrderResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class RedemptionViewModel @Inject constructor(
    private val merchApi: MerchApi
):ViewModel() {

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _progressState = MutableLiveData<Boolean>(false)
    val progressState: LiveData<Boolean> get() = _progressState

    private val _merchResponse = MutableLiveData<MyOrderResponse>()
    val merchResponse: LiveData<MyOrderResponse> get() = _merchResponse

    fun getMyMerch(){
        viewModelScope.launch {
            try {
                val response = merchApi.getMyOrders()
                if (response.isSuccessful) {
                    val res= response.body().toString()
                    val respo = Gson().fromJson(res, MyOrderResponse::class.java)
                    _merchResponse.value = respo
                }else{
                    val error = response.errorBody()?.string()
                    val respo = Gson().fromJson(error, MyOrderResponse::class.java)
                    _errorMessage.value = respo.message
                    _merchResponse.value = MyOrderResponse(false,respo.message,null )
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