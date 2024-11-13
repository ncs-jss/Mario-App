package com.ncs.marioapp.Domain.Repository

import com.ncs.marioapp.Domain.Models.ProfileData.getMyProfile
import com.ncs.marioapp.Domain.Models.ServerResult

interface ProfileRepository {

    suspend fun getProfile(serverResult: (ServerResult<getMyProfile>) -> Unit)

}