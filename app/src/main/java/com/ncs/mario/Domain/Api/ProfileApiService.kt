package com.ncs.mario.Domain.Api

import com.google.gson.JsonObject
import com.ncs.mario.Domain.HelperClasses.PrefManager
import com.ncs.mario.Domain.Models.CreateProfileBody
import com.ncs.mario.Domain.Models.ImageBody
import com.ncs.mario.Domain.Models.SetFCMTokenBody
import com.ncs.mario.Domain.Models.SignUpBody
import com.ncs.mario.Domain.Models.UpdateProfileBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT

interface ProfileApiService {

    @Headers("Content-Type: application/json")
    @POST("create-profile")
    suspend fun createUserProfile(@Header("Authorization") authToken: String=PrefManager.getToken()!!,
                                  @Header("ban-kyc-token") banKycToken: String = PrefManager.getKYCHeaderToken()!!,
                                  @Body payload: CreateProfileBody): Response<JsonObject>

    @Headers("Content-Type: application/json")
    @GET("get-my-details")
    suspend fun getMyDetails(@Header("Authorization") authToken: String=PrefManager.getToken()!!,
                             @Header("ban-kyc-token") banKycToken: String = PrefManager.getKYCHeaderToken()!!,
    ): Response<JsonObject>

    @Headers("Content-Type: application/json")
    @GET("is-approved")
    suspend fun getKYCHeader(@Header("Authorization") authToken: String=PrefManager.getToken()!!,
                             @Header("ban-kyc-token") banKycToken: String = PrefManager.getKYCHeaderToken()!!,
    ): Response<JsonObject>

    @Headers("Content-Type: application/json")
    @PUT("update-picture")
    suspend fun uploadUserPicture(@Header("Authorization") authToken: String=PrefManager.getToken()!!,
                                  @Header("ban-kyc-token") banKycToken: String = PrefManager.getKYCHeaderToken()!!,
                                  @Body payload: ImageBody): Response<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("add-id-card")
    suspend fun addCollegeIDPicture(@Header("Authorization") authToken: String=PrefManager.getToken()!!,
                                    @Header("ban-kyc-token") banKycToken: String = PrefManager.getKYCHeaderToken()!!,
                                    @Body payload: ImageBody): Response<JsonObject>

    @Headers("Content-Type: application/json")
    @GET("is-kyc-verified")
    suspend fun getKycStatus(@Header("Authorization") authToken: String=PrefManager.getToken()!!,
                             @Header("ban-kyc-token") banKycToken: String = PrefManager.getKYCHeaderToken()!!,
    ): Response<JsonObject>

    @Headers("Content-Type: application/json")
    @PUT("set-fcm-token")
    suspend fun setFCMToken(@Header("Authorization") authToken: String=PrefManager.getToken()!!,
                            @Header("ban-kyc-token") banKycToken: String = PrefManager.getKYCHeaderToken()!!, @Body payload: SetFCMTokenBody): Response<JsonObject>

    @Headers("Content-Type: application/json")
    @PUT("update-profile")
    suspend fun updateUserProfile(@Header("Authorization") authToken: String=PrefManager.getToken()!!,
                                  @Header("ban-kyc-token") banKycToken: String = PrefManager.getKYCHeaderToken()!!,
                                  @Body payload: UpdateProfileBody): Response<JsonObject>

    @Headers("Content-Type: application/json")
    @GET("get-my-points")
    suspend fun getUserPoints(@Header("Authorization") authToken: String=PrefManager.getToken()!!,
                             @Header("ban-kyc-token") banKycToken: String = PrefManager.getKYCHeaderToken()!!,
    ): Response<JsonObject>

    @Headers("Content-Type: application/json")
    @GET("get-my-coins")
    suspend fun getUserCoins(@Header("Authorization") authToken: String=PrefManager.getToken()!!,
                              @Header("ban-kyc-token") banKycToken: String = PrefManager.getKYCHeaderToken()!!,
    ): Response<JsonObject>

    @Headers("Content-Type: application/json")
    @PUT("request-KYC")
    suspend fun requestKYCToPending(@Header("Authorization") authToken: String=PrefManager.getToken()!!,
                              @Header("ban-kyc-token") banKycToken: String = PrefManager.getKYCHeaderToken()!!,
    ): Response<JsonObject>



}