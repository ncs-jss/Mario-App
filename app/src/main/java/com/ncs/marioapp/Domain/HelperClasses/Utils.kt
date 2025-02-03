package com.ncs.marioapp.Domain.HelperClasses

import android.animation.ValueAnimator
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.ProgressBar
import android.widget.TextView
import com.instacart.library.truetime.TrueTime
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object Utils {

    fun generateRandomId(length: Int = 20): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..length)
            .map { chars.random() }
            .joinToString("")
    }


    fun String.toRoundTimeStamp(): String {
        val inputFormat = SimpleDateFormat("dd MMM yyyy 'at' hh:mm a", Locale.getDefault())
        val date = inputFormat.parse(this) ?: return ""
        val outputFormat = SimpleDateFormat("dd  |  MMM yy", Locale.getDefault())
        return outputFormat.format(date).uppercase()
    }


    fun String.formatToFullDateWithTime(): String {
        val timestamp = this.toLongOrNull() ?: return ""
        val date = Date(timestamp)
        val dateFormat = SimpleDateFormat("dd MMM yyyy 'at' hh:mm a", Locale.ENGLISH)
        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Kolkata")
        return dateFormat.format(date)
    }


    fun TextView.setTextWithColorFromSubstring(substring: String, color: Int) {
        val text = this.text.toString()
        val startIndex = text.indexOf(substring)

        if (startIndex != -1) {
            val spannable = SpannableString(text)
            // Apply the color to the portion starting from the substring
            spannable.setSpan(
                ForegroundColorSpan(color),
                startIndex,
                text.length,
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            this.text = spannable
        }
    }

    fun convertToTimestamp(dateString: String): Long {
        val dateFormat = SimpleDateFormat(
            "dd/MM/yyyy",
            Locale.getDefault()
        ).apply {
            timeZone = TimeZone.getTimeZone("Asia/Kolkata") // Explicitly set IST
        }
        val date = dateFormat.parse(dateString)
        return date?.time ?: 0L
    }

    fun convertFullFormatTimeToTimestamp(dateString: String): Long {
        val dateFormat = SimpleDateFormat(
            "dd MMM yyyy 'at' hh:mm a",
            Locale.getDefault()
        ).apply {
            timeZone = TimeZone.getTimeZone("Asia/Kolkata")
        }
        val date = dateFormat.parse(dateString)
        return date?.time ?: 0L
    }

    fun getCurrentTimeFromTrueTime(): Date? {
        return if (TrueTime.isInitialized()) {
            TrueTime.now()
        } else {
            null
        }
    }


    fun getTrueTime(): String? {
        val currentTime = getCurrentTimeFromTrueTime()
        val formatter = SimpleDateFormat("dd MMM yyyy 'at' hh:mm a", Locale.getDefault())
        formatter.timeZone = TimeZone.getTimeZone("Asia/Kolkata")
        return currentTime?.let { formatter.format(it) }
    }


    fun ProgressBar.setProgressWithAnimation(targetProgress: Int, duration: Long = 1000L) {
        val startProgress = progress
        ValueAnimator.ofInt(startProgress, targetProgress).apply {
            this.duration = duration
            addUpdateListener { animator ->
                this@setProgressWithAnimation.progress = animator.animatedValue as Int
            }
            start()
        }
    }

}