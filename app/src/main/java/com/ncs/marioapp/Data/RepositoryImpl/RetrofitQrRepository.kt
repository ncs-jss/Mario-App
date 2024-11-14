package com.ncs.marioapp.Data.RepositoryImpl

import com.ncs.marioapp.Domain.Api.QRAPI
import com.ncs.marioapp.Domain.Models.QR.QrScannedResponse
import com.ncs.marioapp.Domain.Models.ServerResult
import com.ncs.marioapp.Domain.Repository.QrRepository
import javax.inject.Inject

class RetrofitQrRepository @Inject constructor(private val qrAPI: QRAPI) : QrRepository {
    override suspend fun getMyRewards(serverResult: (ServerResult<QrScannedResponse>) -> Unit) {
        serverResult(ServerResult.Progress)
        try {
            val response = qrAPI.getMyRewards()
            if (response.isSuccessful) {
                serverResult(ServerResult.Success(response.body()!!))
            } else {
                serverResult(ServerResult.Failure(response.message()))
            }
        } catch (e: Exception) {
            serverResult(ServerResult.Failure(e.message.toString()))
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
                    serverResult(ServerResult.Failure("Invalid QR!!"))
                }
            } else {
                serverResult(ServerResult.Failure("Invalid QR!!"))
            }
        } catch (e: Exception) {
            serverResult(ServerResult.Failure(e.message.toString()))
        }
    }
}
