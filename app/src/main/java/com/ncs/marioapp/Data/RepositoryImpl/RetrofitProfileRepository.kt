package com.ncs.marioapp.Data.RepositoryImpl

import com.ncs.marioapp.Domain.Api.ProfileApiService
import com.ncs.marioapp.Domain.Models.ProfileData.getMyProfile
import com.ncs.marioapp.Domain.Models.ServerResult
import com.ncs.marioapp.Domain.Repository.ProfileRepository
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
                ServerResult.Failure(response.message())
            }
        }
        catch (e:Exception){
            ServerResult.Failure(e.message.toString())
        }
    }
}