package com.ncs.marioapp.Domain.HelperClasses

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Message
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ncs.marioapp.Domain.Models.Profile
import com.ncs.marioapp.Domain.Models.UriTypeAdapter
import com.ncs.marioapp.Domain.Models.UserSurvey
import com.ncs.marioapp.Domain.Models.WorkerFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object PrefManager {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor : SharedPreferences.Editor
    private val gson = Gson()


    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences("NCS_MARIO_PREFS", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
    }


    fun setUserCoins(coins:Int){
        editor.putInt("coins",coins)
        editor.apply()
    }

    fun getUserCoins():Int{
        return sharedPreferences.getInt("coins",0)
    }

    fun setEventIdByDeeplink(eventId:String?){
        editor.putString("EventIdByDeeplink",eventId)
        editor.apply()
    }

    fun getEventIdByDeeplink():String?{
        return sharedPreferences.getString("EventIdByDeeplink",null)
    }

    fun setKYCHeaderToken(uri:String){
        editor.putString("KYCHeaderToken",uri)
        editor.apply()
    }

    fun getKYCHeaderToken():String?{
        return sharedPreferences.getString("KYCHeaderToken","")
    }

    fun setUserSelfieToken(token:String){
        editor.putString("userSelfieToken",token)
        editor.apply()
    }
    fun getUserSelfieToken():String?{
        return sharedPreferences.getString("userSelfieToken",null)
    }
    fun setUserCollegeIDToken(token:String){
        editor.putString("userCollegeIDToken",token)
        editor.apply()
    }
    fun getUserCollegeIDToken():String?{
        return sharedPreferences.getString("userCollegeIDToken",null)
    }

    fun setUserDPCacheData(uri:String){
        editor.putString("userDPCache",uri)
        editor.apply()
    }

    fun getUserDPCacheData():String?{
        return sharedPreferences.getString("userDPCache","")
    }

    fun setCollegeIDCacheData(uri:String){
        editor.putString("collegeIDCache",uri)
        editor.apply()
    }

    fun getCollegeIDCacheData():String?{
        return sharedPreferences.getString("collegeIDCache","")
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

    fun setAlertMessage(message:String?){
        editor.putString("alertMessage", message)
        editor.apply()
    }

    fun getAlertMessage():String?{
        return sharedPreferences.getString("alertMessage",null)
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

    fun setUserProfile(profile: Profile) {
        val json = gson.toJson(profile)
        editor.putString("profile", json)
        editor.apply()
    }

    fun getUserProfile(): Profile? {
        val json = sharedPreferences.getString("profile", null)
        return if (json != null) gson.fromJson(json, Profile::class.java) else Profile()
    }

    fun setUserProfileForCache(profile: Profile) {
        val json = gson.toJson(profile)
        editor.putString("profile_cache", json)
        editor.apply()
    }

    fun getUserProfileForCache(): Profile? {
        val json = sharedPreferences.getString("profile_cache", null)
        return if (json != null) gson.fromJson(json, Profile::class.java) else Profile()
    }



    fun setFCMToken(fcmtoken: String){
        editor.putString("fcmtoken", fcmtoken)
        editor.apply()
    }

    fun getFCMToken(): String? {
        return sharedPreferences.getString("fcmtoken","")
    }

    fun setToken(token: String){
        editor.putString("token", token)
        editor.apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString("token","")
    }

    fun saveWorkerFlow(workerFlow: WorkerFlow) {
        val json = Json.encodeToString(workerFlow)
        Log.d("WorkerFlowSave", "Serialized JSON: $json")
        editor.putString("workerFlow", json)
        editor.apply()
    }

    fun getWorkerFlow(): WorkerFlow? {
        val json = sharedPreferences.getString("workerFlow", null)
        return if (json != null) {
            try {
                val workerFlow = Json.decodeFromString<WorkerFlow>(json)
                Log.d("WorkerFlowLoad", "Deserialized Object: $workerFlow")
                workerFlow
            } catch (e: Exception) {
                Log.e("WorkerFlowLoad", "Error deserializing WorkerFlow", e)
                null
            }
        } else {
            null
        }
    }



    fun clearPrefs(){
        with(sharedPreferences.edit()) {
            clear()
            apply()
        }
    }
    fun setMyPoints(point:Int){
        editor.putInt("marioScore",point)
        editor.apply()
    }
    fun getMyPoints():Int{
        return sharedPreferences.getInt("marioScore",0)
    }

    fun setPostNotifPref(isAllowed:Boolean){
        editor.putBoolean("postNotifPref",isAllowed)
        editor.apply()
    }

    fun getPostNotifPref():Boolean{
        return sharedPreferences.getBoolean("postNotifPref",true)
    }

    fun setPollNotifPref(isAllowed:Boolean){
        editor.putBoolean("pollNotifPref",isAllowed)
        editor.apply()
    }

    fun getPollNotifPref():Boolean{
        return sharedPreferences.getBoolean("pollNotifPref",true)
    }

    fun setMerchNotifPref(isAllowed:Boolean){
        editor.putBoolean("merchNotifPref",isAllowed)
        editor.apply()
    }

    fun getMerchNotifPref():Boolean{
        return sharedPreferences.getBoolean("merchNotifPref",true)
    }

    fun setEventNotifPref(isAllowed:Boolean){
        editor.putBoolean("eventNotifPref",isAllowed)
        editor.apply()
    }

    fun getEventNotifPref():Boolean{
        return sharedPreferences.getBoolean("eventNotifPref",true)
    }

}