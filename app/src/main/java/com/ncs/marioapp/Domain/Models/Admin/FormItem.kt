package com.ncs.marioapp.Domain.Models.Admin

data class FormItem(
    val title: String,
    val type: FormType,
    var value: String = "",
    var options: List<String> = mutableListOf("False", "True")
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
    var endTime: String? = null,

)

data class Questionnaire(
    val queID:String,
    val queTitle:String,
    val questions:List<QuestionItem>
)

data class QuestionItem(
    val qID:String,
    val questionText:String,
    val type:String,
    val options:List<String> = emptyList()
)

enum class FormType {
    EDIT_TEXT,
    DROPDOWN,
    DATE_PICKER,
    SEPARATOR,
    BUTTON,
    RADIO,
}