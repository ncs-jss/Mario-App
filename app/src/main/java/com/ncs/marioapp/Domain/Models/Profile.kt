package com.ncs.marioapp.Domain.Models

data class Profile(
    var admission_number: String="",
    var branch: String="",
    var domain: List<String> = emptyList(),
    val id_card: IdCard = IdCard(),
    var name: String="",
    val photo: Photo = Photo(),
    val points: Int = 0,
    val role: Int = 0,
    val socials: Socials = Socials(),
    var year: Int = 0,
    var other_domain:String = ""
)