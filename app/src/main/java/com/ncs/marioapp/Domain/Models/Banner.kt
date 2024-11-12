package com.ncs.marioapp.Domain.Models

data class Banner(
    val _id: String = "",
    val createdAt: Long = 0L,
    val image: String = "",
    val link: String = "https://www.instagram.com/hackncs/",
    val type: String = "link",  // default "link" or "story"
    val storyId: String = ""
)
