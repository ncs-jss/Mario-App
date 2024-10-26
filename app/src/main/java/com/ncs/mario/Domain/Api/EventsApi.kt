package com.ncs.mario.Domain.Api

import com.google.gson.JsonObject
import com.ncs.mario.Domain.HelperClasses.PrefManager
import com.ncs.mario.Domain.Models.Events.AnswerPollBody
import com.ncs.mario.Domain.Models.Events.EnrollUser
import com.ncs.mario.Domain.Models.Events.GetEvents
import com.ncs.mario.Domain.Models.Events.ParticipatedEventResponse
import com.ncs.mario.Domain.Models.Events.ScanTicketBody
import com.ncs.mario.Domain.Models.QR.QrScannedResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface EventsApi {
    @Headers("Content-Type: application/json")
    @GET("get-events")
    suspend fun getEvents(@Header("Authorization") authToken: String= PrefManager.getToken()!!,
                          @Header("ban-kyc-token") banKycToken: String = PrefManager.getKYCHeaderToken()!!,
    ): Response<GetEvents>

    @Headers("Content-Type: application/json")
    @GET("get-my-events")
    suspend fun getMyEvents(@Header("Authorization") authToken: String= PrefManager.getToken()!!,
                            @Header("ban-kyc-token") banKycToken: String = PrefManager.getKYCHeaderToken()!!,
    ): Response<ParticipatedEventResponse>

    @Headers("Content-Type: application/json")
    @POST("enroll-me")
    suspend fun enrollUser(@Header("Authorization") authToken: String=PrefManager.getToken()!!,
                           @Header("ban-kyc-token") banKycToken: String = PrefManager.getKYCHeaderToken()!!,
                           @Body payload: EnrollUser): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST("opt-out")
    suspend fun optOutUser(@Header("Authorization") authToken: String=PrefManager.getToken()!!,
                           @Header("ban-kyc-token") banKycToken: String = PrefManager.getKYCHeaderToken()!!,
                           @Body payload: EnrollUser): Response<JsonObject>

    @Headers("Content-Type: application/json")
    @GET("get-polls")
    suspend fun getPolls(@Header("Authorization") authToken: String=PrefManager.getToken()!!,
                         @Header("ban-kyc-token") banKycToken: String = PrefManager.getKYCHeaderToken()!!,
    ): Response<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("answer-poll")
    suspend fun answerPoll(@Header("Authorization") authToken: String=PrefManager.getToken()!!,
                           @Header("ban-kyc-token") banKycToken: String = PrefManager.getKYCHeaderToken()!!,
                           @Body payload: AnswerPollBody): Response<JsonObject>

    @Headers("Content-Type: application/json")
    @GET("get-ticket/{event_id}")
    suspend fun getTicket(@Header("Authorization") authToken: String= PrefManager.getToken()!!,
                                  @Header("ban-kyc-token") banKycToken: String = PrefManager.getKYCHeaderToken()!!,
                                  @Path("event_id") eventID: String): Response<ResponseBody>

    //admin endpoint to scan tickets
    @Headers("Content-Type: application/json")
    @POST("admin/scan-ticket")
    suspend fun scanTicket(@Header("Authorization") authToken: String=PrefManager.getToken()!!,
                           @Header("ban-kyc-token") banKycToken: String = PrefManager.getKYCHeaderToken()!!,
                           @Body payload: ScanTicketBody): Response<JsonObject>
}