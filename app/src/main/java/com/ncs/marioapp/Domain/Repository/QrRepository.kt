package com.ncs.marioapp.Domain.Repository

import com.ncs.marioapp.Domain.Models.QR.QrScannedResponse
import com.ncs.marioapp.Domain.Models.ServerResult

interface QrRepository {

    suspend fun getMyRewards(serverResult: (ServerResult<QrScannedResponse>) -> Unit)

    suspend fun validateScannedQR(couponCode: String, serverResult:(ServerResult<QrScannedResponse>) -> Unit)

}