package com.ncs.mario.Domain.Models

import java.lang.Exception

sealed class ServerResult<out T> {
    data class Success<out T>(val data : T) : ServerResult<T>()
    object Progress : ServerResult<Nothing>()
    data class Failure(val exception: Exception) : ServerResult<Nothing>()

}