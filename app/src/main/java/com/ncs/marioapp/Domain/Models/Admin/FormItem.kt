package com.ncs.marioapp.Domain.Models.Admin

data class FormItem(
    val title: String,
    val type: FormType,
    var value: String = "",
    var options: List<String> = listOf("False", "True")
)

data class Round(
    val description: String,
    val eventID: String,
    val roundTitle: String,
    val questionnaireID: String,
    val requireSubmission: Boolean,
    val roundID: String,
    val timeLine: Map<String, Long>,
    val venue: String,
    val isLive: Boolean,
    val submissionButtonText: String,
    var startTime: String? = null,
    var endTime: String? = null
)

enum class FormType {
    EDIT_TEXT,
    DROPDOWN,
    DATE_PICKER,
    SEPARATOR,
    BUTTON,
    RADIO,
}