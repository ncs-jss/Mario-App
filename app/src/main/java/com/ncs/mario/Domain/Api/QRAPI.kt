package com.ncs.mario.Domain.Api

import com.ncs.mario.Domain.HelperClasses.PrefManager
import com.ncs.mario.Domain.Models.QR.QrScannedResponse
import com.ncs.mario.Domain.Models.ServerResult
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path

interface QRAPI {

    @Headers("Content-Type: application/json")
    @GET("scanned/{coupon_code}")
    suspend fun validateScannedQR(@Header("Authorization") authToken: String= PrefManager.getToken()!!,
                                  @Header("ban-kyc-token") banKycToken: String = PrefManager.getKYCHeaderToken()!!,
                                  @Path("coupon_code") couponCode: String): Response<QrScannedResponse>

    @Headers("Content-Type: application/json")
    @GET("get-my-rewards/")
    suspend fun getMyRewards(@Header("Authorization") authToken: String= PrefManager.getToken()!!,
                             @Header("ban-kyc-token") banKycToken: String = PrefManager.getKYCHeaderToken()!!,
    ):Response<QrScannedResponse>
}