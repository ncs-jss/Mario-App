package com.ncs.marioapp.Domain.Repository

import com.ncs.marioapp.Domain.Api.QRAPI
import com.ncs.marioapp.Domain.Interfaces.QrRepository
import com.ncs.marioapp.Domain.Models.QR.QrScannedResponse
import com.ncs.marioapp.Domain.Models.ServerResult
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
                serverResult(ServerResult.Success(response.body()!!))
            } else {
                serverResult(ServerResult.Failure(Exception(response.message())))
            }
        } catch (e: Exception) {
            serverResult(ServerResult.Failure(e))
        }
    }
}
