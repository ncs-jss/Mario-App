package com.ncs.marioapp.Domain.Models.Admin

import com.google.firebase.Timestamp

data class FormItem(
    val title: String,
    val type: FormType,
    var value: String = "",
    var options: List<String> = listOf("True", "False")
)

data class Round(
    val description: String,
    val eventID: String,
    val roundTitle: String,
    val questionnaireID: String,
    val requireSubmission: Boolean,
    val roundID: String,
    val timeLine: Map<String, Timestamp>,
    val venue: String,
    val isLive: Boolean
)

enum class FormType {
    EDIT_TEXT,
    DROPDOWN,
    DATE_PICKER,
    SEPARATOR,
    BUTTON,
    RADIO,
}