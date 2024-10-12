package com.ncs.mario.Domain.Repository

import com.ncs.mario.Domain.API.QRAPI
import com.ncs.mario.Domain.Interfaces.QrRepository
import com.ncs.mario.Domain.Models.QR.QrScannedResponse
import com.ncs.mario.Domain.Models.ServerResult
import javax.inject.Inject

class RetrofitQrRepository@Inject constructor(private val qrAPI: QRAPI): QrRepository {
    override suspend fun getMyRewards(serverResult: (ServerResult<QrScannedResponse>) -> Unit) {
        serverResult(ServerResult.Progress)
        try {
            val response = qrAPI.getMyRewards()
            if(response.isSuccessful){
                ServerResult.Success(response.body()!!)
            }
            else{
                ServerResult.Failure(Exception(response.message()))
            }
        }
        catch (e:Exception){
            ServerResult.Failure(e)
        }

    }

    override suspend fun validateScannedQR(
        couponCode: String,
        serverResult: (ServerResult<QrScannedResponse>) -> Unit
    ) {
        serverResult(ServerResult.Progress)
        try {
            val response = qrAPI.validateScannedQR(couponCode)
            if(response.isSuccessful){
                ServerResult.Success(response.body()!!)
            }
            else{
                ServerResult.Failure(Exception(response.message()))
            }
        }
        catch (e:Exception){
            ServerResult.Failure(e)
        }
    }
}