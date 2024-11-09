package com.ncs.marioapp.Domain.Models

sealed class ServerResult<out T> {
    data class Success<out T>(val data : T) : ServerResult<T>()
    object Progress : ServerResult<Nothing>()
    data class Failure(val message: String) : ServerResult<Nothing>()

}