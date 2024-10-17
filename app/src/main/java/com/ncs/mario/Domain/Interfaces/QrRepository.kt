package com.ncs.mario.Domain.Interfaces

import com.ncs.mario.Domain.Models.QR.QrScannedResponse
import com.ncs.mario.Domain.Models.ServerResult
import retrofit2.Response

interface QrRepository {

    suspend fun getMyRewards(serverResult: (ServerResult<QrScannedResponse>) -> Unit)

    suspend fun validateScannedQR(couponCode: String, serverResult:(ServerResult<QrScannedResponse>) -> Unit)

}