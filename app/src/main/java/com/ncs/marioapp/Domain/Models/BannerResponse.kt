package com.ncs.marioapp.Domain.Models

data class BannerResponse(
    val banners: List<Banner>,
    val message: String,
    val success: Boolean
)