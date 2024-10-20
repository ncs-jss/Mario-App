package com.ncs.mario.Domain.Models.Posts

data class LikePostBody(
    val post_id:String,
    val action:String
)
