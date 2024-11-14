package com.ncs.marioapp.UI.AdminScreen

import com.ncs.marioapp.Domain.Models.Admin.FormItem
import com.ncs.marioapp.Domain.Models.Admin.FormType

abstract class BaseFormPage {

    abstract fun onSubmit()

    fun validateRequiredFields(fields: List<FormItem>): Boolean {
        for (field in fields) {
            if (field.type == FormType.EDIT_TEXT && (field.value.isBlank() || field.value == "")) {
                showValidationError(field.title, "This field is required")
                return false
            }
        }
        return true
    }

    fun validateEmail(fields: List<FormItem>): Boolean {
        for (field in fields) {
            if (field.type == FormType.EDIT_TEXT && field.title.contains("Email", true)) {
                val email = field.value
                if (!isValidEmail(email)) {
                    showValidationError(field.title, "Invalid email format")
                    return false
                }
            }
        }
        return true
    }

    private fun isValidEmail(email: String?): Boolean {
        return email?.matches(Regex("^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) == true
    }

    private fun showValidationError(fieldTitle: String, errorMessage: String) {
        println("Error in field '$fieldTitle': $errorMessage")
    }

    fun resetForm(fields: List<FormItem>) {
        for (field in fields) {
            field.value = ""
        }
    }

    fun getFilledFields(fields: List<FormItem>): List<FormItem> {
        return fields.filter { it.value.isNotBlank() }
    }
}