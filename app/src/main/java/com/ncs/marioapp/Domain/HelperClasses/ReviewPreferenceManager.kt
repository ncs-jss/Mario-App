package com.ncs.marioapp.Domain.HelperClasses

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import java.util.concurrent.TimeUnit

class ReviewPreferenceManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("review_prefs", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    private val defaultInstallDate = 1739164800000L

    fun getInstallDate(): Long {
        return sharedPreferences.getLong("install_date", defaultInstallDate)
    }

    fun setInstallDateIfNotSet() {
        if (!sharedPreferences.contains("install_date")) {
            editor.putLong("install_date", System.currentTimeMillis())
            editor.apply()
        }
    }

    fun getLastReviewPrompt(): Long {
        return sharedPreferences.getLong("last_review_prompt", defaultInstallDate)
    }

    fun setLastReviewPrompt() {
        editor.putLong("last_review_prompt", System.currentTimeMillis())
        editor.apply()
    }

    fun hasUserReviewed(): Boolean {
        return sharedPreferences.getBoolean("has_reviewed", false)
    }

    fun setUserReviewed() {
        editor.putBoolean("has_reviewed", true)
        editor.putLong("last_review_prompt", System.currentTimeMillis())
        editor.apply()
    }

    fun shouldShowReviewDialog(): Boolean {
        val installDate = getInstallDate()
        val lastPromptTime = getLastReviewPrompt()
        val currentTime = System.currentTimeMillis()
        val threeDays = TimeUnit.DAYS.toMillis(3)
        val sixMonths = TimeUnit.DAYS.toMillis(180)

        Log.d("ReviewReq", "Install date: $installDate (${installDate.toDateString()})")
        Log.d("ReviewReq", "Last prompt: $lastPromptTime (${lastPromptTime.toDateString()})")
        Log.d("ReviewReq", "Current time: $currentTime (${currentTime.toDateString()})")

        return if (hasUserReviewed()) {
            Log.d("ReviewReq", "User has reviewed. Checking for 6 months delay...")
            (currentTime - lastPromptTime >= sixMonths)
        } else {
            Log.d("ReviewReq", "User has NOT reviewed. Checking for 3 days delay...")
            (currentTime - installDate >= threeDays) && (currentTime - lastPromptTime >= threeDays)
        }
    }

    fun Long.toDateString(): String {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(this))
    }
}
