package com.ncs.marioapp.Domain.Api

import com.google.gson.JsonObject
import com.ncs.marioapp.Domain.HelperClasses.PrefManager
import com.ncs.marioapp.Domain.Models.Report.ReportBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface ReportApiService {

    @Headers("Content-Type: application/json")
    @POST("create-report")
    suspend fun addReport(@Header("Authorization") authToken: String= PrefManager.getToken()!!,
                         @Header("ban-kyc-token") banKycToken: String = PrefManager.getKYCHeaderToken()!!,
                         @Body payload: ReportBody
    ): Response<JsonObject>

}