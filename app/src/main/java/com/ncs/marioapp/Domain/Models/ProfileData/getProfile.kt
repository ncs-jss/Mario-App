package com.ncs.marioapp.Domain.Models.ProfileData

data class getMyProfile(
    val success: Boolean,
    val message: String,
    val profile: Profile
)

data class Profile(
    val name: String,
    val branch: String,
    val year: Int,
    val admissionNumber: String? = null,
    val domain: List<String>? = null,
    val photo: Media? = null,
    val idCard: Media? = null,
    val socials: Map<String, String>,
    val points: Int
)

data class Media(
    val secureUrl: String,
    val publicId: String
)
