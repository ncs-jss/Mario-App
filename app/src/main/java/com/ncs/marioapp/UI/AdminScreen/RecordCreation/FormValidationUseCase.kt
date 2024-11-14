package com.ncs.marioapp.UI.AdminScreen.RecordCreation

import com.ncs.marioapp.Domain.Models.Admin.FormItem
import com.ncs.marioapp.Domain.Models.Admin.FormType

class FormValidationUseCase {

    fun validateRequiredFields(fields: List<FormItem>): String? {
        for (field in fields) {
            if (field.type == FormType.EDIT_TEXT && (field.value.isBlank() || field.value == "")) {
                return "${field.title} cannot be empty."
            }

            if (field.type == FormType.DROPDOWN && (field.value.isBlank() || field.value == "Select an Option")) {
                return "${field.title} is not yet chosen."
            }

            if (field.type == FormType.DROPDOWN && (field.value.isBlank() || field.value == "Select an Option")) {
                return "${field.title} is not yet chosen."
            }

            if (field.type == FormType.DATE_PICKER && (field.value.isBlank() || field.value == field.title)) {
                return "${field.title} is not yet set."
            }
        }

        return null
    }
}