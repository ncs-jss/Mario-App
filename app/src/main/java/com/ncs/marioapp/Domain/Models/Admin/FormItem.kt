package com.ncs.marioapp.Domain.Models.Admin

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class FormItem(
    val title: String,
    val type: FormType,
    var value: String = "",
    var options: List<String> = mutableListOf("False", "True")
)

@Parcelize
data class Round(
    val description: String = "",
    val eventID: String = "",
    val roundTitle: String = "",
    val questionnaireID: String = "",
    val requireSubmission: Boolean = false,
    val roundID: String = "",
    val timeLine: Map<String, Long> = emptyMap(),
    val venue: String = "",
    val live: Boolean = false,
    val submissionButtonText: String = "",
    var startTime: String? = null,
    var endTime: String? = null,

)
    var endTime: String? = null,
    var sameAsCollege: Boolean = false,
    var seriesNumber: Int = -1
) : Parcelable

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