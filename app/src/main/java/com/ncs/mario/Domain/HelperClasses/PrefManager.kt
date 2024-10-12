package com.ncs.mario.Domain.HelperClasses

import android.content.Context
import android.content.SharedPreferences

object PrefManager {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor : SharedPreferences.Editor

    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences("NCS_MARIO_PREFS", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
    }

    fun setMyPoints(point:Int){
        editor.putInt("marioScore",point)
        editor.apply()
    }
    fun getMyPoints():Int{
        return sharedPreferences.getInt("marioScore",0)
    }


}