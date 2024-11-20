package com.ncs.marioapp.Domain.Models

data class CreateProfileBody(
    val FCM_token: String="",
    val admission_number: String="",
    val branch: String="",
    val domain: List<String> = emptyList(),
    val other_domain:String = "",
    val name: String = "",
    val socials: Map<String, String> = emptyMap(),
    val year: Int = 0,
    val admitted_to:String ="",
    var photo_token:String ="",
    var id_card_token:String=""
)