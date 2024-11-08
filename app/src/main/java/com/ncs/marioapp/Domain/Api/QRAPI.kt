package com.ncs.marioapp.Domain.Api

import com.google.gson.JsonObject
import com.ncs.marioapp.Domain.HelperClasses.PrefManager
import com.ncs.marioapp.Domain.Models.Admin.GiftCoinsPostBody
import com.ncs.marioapp.Domain.Models.QR.QrScannedResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
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


    @Headers("Content-Type: application/json")
    @POST("admin/gift-coins")
    suspend fun giftCoins(@Header("Authorization") authToken: String=PrefManager.getToken()!!,
                         @Header("ban-kyc-token") banKycToken: String = PrefManager.getKYCHeaderToken()!!,
                         @Body payload: GiftCoinsPostBody
    ): Response<JsonObject>
}