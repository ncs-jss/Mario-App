package com.ncs.marioapp.Domain.Models.Posts

data class Post(
    val _id: String,
    val caption: String,
    val createdAt: Long,
    val image: String,
    val liked: Boolean,
    val likes: Int
)