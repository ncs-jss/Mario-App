package com.ncs.marioapp.Domain.HelperClasses

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

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
}