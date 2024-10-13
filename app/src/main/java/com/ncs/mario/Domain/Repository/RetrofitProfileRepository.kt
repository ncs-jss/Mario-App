package com.ncs.mario.Domain.Repository

import com.ncs.mario.Domain.Api.ProfileApi
import com.ncs.mario.Domain.Interfaces.ProfileRepository
import com.ncs.mario.Domain.Models.Profile.getMyProfile
import com.ncs.mario.Domain.Models.ServerResult
import javax.inject.Inject

class RetrofitProfileRepository @Inject constructor(private val profileApi: ProfileApi):
    ProfileRepository {
    override suspend fun getProfile(serverResult: (ServerResult<getMyProfile>) -> Unit) {
        serverResult(ServerResult.Progress)
        try {
            val response = profileApi.getProfile()
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