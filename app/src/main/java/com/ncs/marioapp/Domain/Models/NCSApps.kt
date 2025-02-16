package com.ncs.marioapp.Domain.Models

import android.graphics.drawable.Drawable

data class NCSApps(
    val appIcon:Drawable,
    val appName:String,
    val appRating:Double,
    val appDescription:String,
    val appUrl:String
)
