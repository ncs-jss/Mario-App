package com.ncs.mario.Domain.Interfaces

import com.ncs.mario.Domain.Models.ProfileData.getMyProfile
import com.ncs.mario.Domain.Models.ServerResult

interface ProfileRepository {

    suspend fun getProfile(serverResult: (ServerResult<getMyProfile>) -> Unit)

}