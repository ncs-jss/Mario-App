package com.ncs.mario.Domain.Models

data class Merch(
    val id:String="",
    val image:String="",
    val name:String="",
    val cost:Int=1000,
    val stock:Int=0
)
