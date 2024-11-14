package com.ncs.marioapp.Domain.Models

data class UpdateProfileBody(
    val branch: String,
    val domain: List<String>,
    val other_domain:String,
    val name: String,
    val admitted_to: String,
    val socials: Map<String, String>,
    val year: Int
)
