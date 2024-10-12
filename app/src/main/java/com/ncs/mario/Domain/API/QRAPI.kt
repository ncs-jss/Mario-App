package com.ncs.mario.Domain.API

import com.ncs.mario.Domain.Models.QR.QrScannedResponse
import com.ncs.mario.Domain.Models.ServerResult
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface QRAPI {


    @GET("qr/scanned/{coupon_code}")
    suspend fun validateScannedQR(
        @Path("coupon_code") couponCode: String
    ): Response<ServerResult<QrScannedResponse>>

    @GET("qr/get-my-rewards/")
    suspend fun getMyRewards():Response<ServerResult<QrScannedResponse>>
}