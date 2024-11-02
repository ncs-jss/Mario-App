package com.ncs.mario.UI.AdminScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ncs.mario.Domain.Api.EventsApi
import com.ncs.mario.Domain.Api.ProfileApiService
import com.ncs.mario.Domain.Api.QRAPI
import com.ncs.mario.Domain.Interfaces.QrRepository
import com.ncs.mario.Domain.Models.Admin.GiftCoinsPostBody
import com.ncs.mario.Domain.Models.Events.ScanTicketBody
import com.ncs.mario.Domain.Models.ServerResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val qrRepository: QrRepository,
    private val qrapi: QRAPI,
    private val profileApiService: ProfileApiService,
    private val eventsApi: EventsApi
) : ViewModel() {


    private val _validateScannedQR = MutableLiveData<ServerResult<String>>()
    val validateScannedQR: LiveData<ServerResult<String>> = _validateScannedQR

    private val _progressState = MutableLiveData<Boolean>(false)
    val progressState: LiveData<Boolean> get() = _progressState

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _progressStateGiftCoins = MutableLiveData<Boolean>(false)
    val progressStateGiftCoins: LiveData<Boolean> get() = _progressStateGiftCoins

    fun scanTicket(scanTicketBody: ScanTicketBody){
        viewModelScope.launch {
            _progressState.value = true
            try {
                val response = eventsApi.scanTicket(payload = scanTicketBody)
                if (response.isSuccessful) {
                    _progressState.value = false
                    _errorMessage.value = "Attendance Marked"
                } else {
                    _progressState.value = false
                    _errorMessage.value = "Failed to scan ticket"
                }
            } catch (e: SocketTimeoutException) {
                _progressState.value = false
                _errorMessage.value = "Network timeout. Please try again."
            } catch (e: Exception) {
                _progressState.value = false
                _errorMessage.value = "Something went wrong. Please try again."
            }
        }
    }

    fun validateScannedQR(couponCode: String) {
        viewModelScope.launch {
            qrRepository.validateScannedQR(couponCode) {
                when (it) {
                    is ServerResult.Failure -> {
                        _validateScannedQR.value = ServerResult.Failure(it.exception)
                    }
                    ServerResult.Progress -> {
                        _validateScannedQR.value = ServerResult.Progress
                    }
                    is ServerResult.Success -> {
                        _validateScannedQR.value = ServerResult.Success(it.data.message)
                    }
                }
            }
        }
    }

    fun giftCoins(giftCoinsPostBody: GiftCoinsPostBody){
        viewModelScope.launch {
            _progressStateGiftCoins.value = true
            try {
                val response = qrapi.giftCoins(payload = giftCoinsPostBody)
                if (response.isSuccessful) {
                    _progressStateGiftCoins.value = false
                    _errorMessage.value = "Coins gifted successfully"
                } else {
                    _progressStateGiftCoins.value = false
                    _errorMessage.value = "Failed to gift coins"
                }
            } catch (e: SocketTimeoutException) {
                _progressStateGiftCoins.value = false
                _errorMessage.value = "Network timeout. Please try again."
            } catch (e: Exception) {
                _progressStateGiftCoins.value = false
                _errorMessage.value = "Something went wrong. Please try again."
            }
        }
    }
}