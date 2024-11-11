package com.ncs.marioapp.Domain.Models

data class Banner(
    val _id: String,
    val createdAt: Long,
    val image: String,
    val link:String="https://www.instagram.com/hackncs/",
    val type:String="link",  //or story
    val storyId:String=""
)
