package com.ncs.mario.Domain.Repository

import android.util.Log
import com.ncs.mario.Domain.Api.QRAPI
import com.ncs.mario.Domain.Interfaces.QrRepository
import com.ncs.mario.Domain.Models.QR.QrScannedResponse
import com.ncs.mario.Domain.Models.ServerResult
import javax.inject.Inject

class RetrofitQrRepository @Inject constructor(private val qrAPI: QRAPI) : QrRepository {
    override suspend fun getMyRewards(serverResult: (ServerResult<QrScannedResponse>) -> Unit) {
        serverResult(ServerResult.Progress)
        try {
            val response = qrAPI.getMyRewards()
            if (response.isSuccessful) {
                serverResult(ServerResult.Success(response.body()!!))
            } else {
                serverResult(ServerResult.Failure(Exception(response.message())))
            }
        } catch (e: Exception) {
            serverResult(ServerResult.Failure(e))
        }
    }

    override suspend fun validateScannedQR(
        couponCode: String,
        serverResult: (ServerResult<QrScannedResponse>) -> Unit
    ) {
        serverResult(ServerResult.Progress)
        try {
            val response = qrAPI.validateScannedQR(couponCode = couponCode)
            if (response.isSuccessful) {
                if (response.body()!!.success){
                    serverResult(ServerResult.Success(response.body()!!))
                }
                else{
                    serverResult(ServerResult.Failure(Exception("Invalid QR!!")))
                }
            } else {
                serverResult(ServerResult.Failure(Exception("Invalid QR!!")))
            }
        } catch (e: Exception) {
            serverResult(ServerResult.Failure(e))
        }
    }
}
