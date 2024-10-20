package com.ncs.mario.Domain.Models.Posts

data class PostResponse(
    val message: String,
    val posts: List<Post>,
    val success: Boolean
)