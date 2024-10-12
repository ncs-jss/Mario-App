package com.ncs.mario.Domain.Interfaces

import com.ncs.mario.Domain.Models.Profile.getMyProfile
import com.ncs.mario.Domain.Models.QR.QrScannedResponse
import com.ncs.mario.Domain.Models.ServerResult

interface ProfileRepository {

    suspend fun getProfile(serverResult: (ServerResult<getMyProfile>) -> Unit)

}