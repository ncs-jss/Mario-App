package com.ncs.mario.Domain.Repository

import com.ncs.mario.Domain.Api.ProfileApiService
import com.ncs.mario.Domain.Interfaces.ProfileRepository
import com.ncs.mario.Domain.Models.ProfileData.getMyProfile
import com.ncs.mario.Domain.Models.ServerResult
import javax.inject.Inject

class RetrofitProfileRepository @Inject constructor(private val profileApi: ProfileApiService):
    ProfileRepository {
    override suspend fun getProfile(serverResult: (ServerResult<getMyProfile>) -> Unit) {
        serverResult(ServerResult.Progress)
        try {
            val response = profileApi.getMyDetails()
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