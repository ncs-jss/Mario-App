package com.ncs.mario.Domain.HelperClasses

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.ncs.mario.Domain.Models.UserSurvey

object PrefManager {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor : SharedPreferences.Editor
    private val gson = Gson()

    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences("NCS_MARIO_PREFS", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
    }

    fun getUserID(): String? {
        return sharedPreferences.getString("userID","")
    }

    fun setUserID(userID: String){
        editor.putString("userID", userID)
        editor.apply()
    }

    fun getUserSignUpEmail(): String? {
        return sharedPreferences.getString("UserSignUpEmail","")
    }

    fun setUserSignUpEmail(userSignUpEmail: String){
        editor.putString("UserSignUpEmail", userSignUpEmail)
        editor.apply()
    }

    fun getShowProfileCompletionAlert(): Boolean {
        return sharedPreferences.getBoolean("ShowProfileCompletionAlert",false)
    }

    fun setShowProfileCompletionAlert(status: Boolean){
        editor.putBoolean("ShowProfileCompletionAlert", status)
        editor.apply()
    }

    fun getSignUpStatus(): Boolean {
        return sharedPreferences.getBoolean("SignUpStatus",false)
    }

    fun setSignUpStatus(status: Boolean){
        editor.putBoolean("SignUpStatus", status)
        editor.apply()
    }

    fun getLoginStatus(): Boolean {
        return sharedPreferences.getBoolean("LoginStatus",false)
    }

    fun setLoginStatus(status: Boolean){
        editor.putBoolean("LoginStatus", status)
        editor.apply()
    }

    fun getSurveyStatus(): Boolean {
        return sharedPreferences.getBoolean("SurveyStatus",false)
    }

    fun setSurveyStatus(status: Boolean){
        editor.putBoolean("SurveyStatus", status)
        editor.apply()
    }

    fun setUserSurvey(userSurvey: UserSurvey) {
        val json = gson.toJson(userSurvey)
        editor.putString("UserSurvey", json)
        editor.apply()
    }

    fun getUserSurvey(): UserSurvey? {
        val json = sharedPreferences.getString("UserSurvey", null)
        return if (json != null) gson.fromJson(json, UserSurvey::class.java) else UserSurvey()
    }

    fun setToken(token: String){
        editor.putString("token", token)
        editor.apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString("token","")
    }


}