package com.ncs.mario.Domain.Models

data class Profile(
    val admission_number: String="",
    val branch: String="",
    val domain: List<String> = emptyList(),
    val id_card: IdCard = IdCard(),
    val name: String="",
    val photo: Photo = Photo(),
    val points: Int = 0,
    val role: Int = 0,
    val socials: Socials = Socials(),
    val year: Int = 0,
    val other_domain:String = ""
)