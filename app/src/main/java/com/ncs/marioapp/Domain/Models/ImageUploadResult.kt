package com.ncs.marioapp.Domain.Models

data class ImageUploadResult(
    val success: Boolean,
    val message: String,
    val photo_token:String?,
    val id_card_token:String?
)
