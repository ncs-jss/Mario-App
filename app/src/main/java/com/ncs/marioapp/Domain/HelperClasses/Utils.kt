package com.ncs.marioapp.Domain.HelperClasses

import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import com.google.firebase.Timestamp
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

    fun convertToFirestoreTimestamp(dateString: String): Timestamp? {
        return try {
            val dateFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
            val date = dateFormat.parse(dateString)
            date?.let { Timestamp(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun String.toRoundTimeStamp(): String {
        val inputFormat = SimpleDateFormat("dd MMM yyyy 'at' HH:mm a", Locale.getDefault())
        val date = inputFormat.parse(this) ?: return ""
        val outputFormat = SimpleDateFormat("dd  |  MMM yy", Locale.getDefault())
        return outputFormat.format(date).uppercase()
    }


    fun String.formatToFullDateWithTime(): String {
        val timestamp = this.toLongOrNull() ?: return ""
        val date = Date(timestamp)
        val dateFormat = SimpleDateFormat("dd MMM yyyy 'at' HH:mm a", Locale.getDefault())
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
        )  // Specify the format of the input string
        val date = dateFormat.parse(dateString)  // Parse the date string into a Date object
        return date?.time ?: 0L  // Return the timestamp (in milliseconds)
    }
}