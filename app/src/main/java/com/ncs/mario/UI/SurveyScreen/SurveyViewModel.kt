package com.ncs.mario.UI.SurveyScreen

import android.graphics.Bitmap
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SurveyViewModel @Inject constructor() : ViewModel() {
    enum class SurveyStep {
        PERSONAL_DETAILS,
        TECHNICAL_DETAILS,
        SOCIAL_DETAILS,
        KYC_DETAILS
    }

    private val _linksCount = MutableLiveData(0)
    val linksCount: LiveData<Int> get() = _linksCount

    private val _errorMessagePersonalDetails = MutableLiveData<String?>()
    val errorMessagePersonalDetails: LiveData<String?> get() = _errorMessagePersonalDetails

    private val _errorMessageTechncialDetails = MutableLiveData<String?>()
    val errorMessageTechncialDetails: LiveData<String?> get() = _errorMessageTechncialDetails

    private val _errorMessageSocialDetails = MutableLiveData<String?>()
    val errorMessageSocialDetails: LiveData<String?> get() = _errorMessageSocialDetails

    private val _errorMessageKYCDetails = MutableLiveData<String?>()
    val errorMessageKYCDetails: LiveData<String?> get() = _errorMessageKYCDetails

    private val _userSelfie = MutableLiveData<String>(null)
    val userSelfie: LiveData<String?> get() = _userSelfie

    private val _userCollegeID = MutableLiveData<String?>(null)
    val userCollegeID: LiveData<String?> get() = _userCollegeID



    val name = MutableLiveData<String>(null)
    val admission_num = MutableLiveData<String>(null)
    val phone_num = MutableLiveData<String>(null)
    val bio = MutableLiveData<String>(null)

    val interestedDomains = MutableLiveData<String>(null)
    val knownLanguages = MutableLiveData<String>(null)
    val userInput = MutableLiveData<String>(null)

    val linkedIn = MutableLiveData<String>(null)
    val github = MutableLiveData<String>(null)
    val behance = MutableLiveData<String>(null)
    val link1 = MutableLiveData<String>(null)
    val link2 = MutableLiveData<String>(null)

    private val _personalDetailsPageResult = MutableLiveData<Boolean>()
    val personalDetailsPageResult: LiveData<Boolean> get() = _personalDetailsPageResult

    private val _technicalDetailsPageResult = MutableLiveData<Boolean>()
    val technicalDetailsPageResult: LiveData<Boolean> get() = _technicalDetailsPageResult

    private val _socialDetailsPageResult = MutableLiveData<Boolean>()
    val socialDetailsPageResult: LiveData<Boolean> get() = _socialDetailsPageResult

    private val _kycDetailsPageResult = MutableLiveData<Boolean>()
    val kycDetailsPageResult: LiveData<Boolean> get() = _kycDetailsPageResult

    private val _currentStep = MutableLiveData(SurveyStep.PERSONAL_DETAILS)
    val currentStep: LiveData<SurveyStep> = _currentStep

    fun setCurrentStep(step: SurveyStep) {
        _currentStep.value = step
    }

    fun setUserSelfie(bitmap: String?) {
        _userSelfie.value = bitmap!!
    }

    fun setUserCollegeID(bitmap: String?) {
        _userCollegeID.value = bitmap!!
    }

    fun resetErrorMessagePersonalDetails() {
        _errorMessagePersonalDetails.value = null
    }
    fun resetPersonalDetailsPageResult() {
        _personalDetailsPageResult.value = false
    }

    fun resetErrorMessageTechnicalDetails() {
        _errorMessageTechncialDetails.value = null
    }
    fun resetTechnicalDetailsPageResult() {
        _technicalDetailsPageResult.value = false
    }

    fun resetErrorMessageSocialDetails() {
        _errorMessageSocialDetails.value = null
    }
    fun resetSocialDetailsPageResult() {
        _socialDetailsPageResult.value = false
    }

    fun resetErrorMessageKYCDetails() {
        _errorMessageKYCDetails.value = null
    }
    fun resetKYCDetailsPageResult() {
        _kycDetailsPageResult.value = false
    }

    fun setLinksCount(count: Int) {
        _linksCount.value = count
    }


    fun validateInputsOnPersonalDetailsPage() {
        Log.d("SurveyViewModel", "validateInputsOnPersonalDetailsPage called")
        val nameValue=name.value?.trim()
        val admission_numValue=admission_num.value?.trim()
        val phone_numValue=phone_num.value?.trim()
        Log.d("SurveyViewModel", "$nameValue $admission_numValue $phone_numValue")

        if (nameValue.isNullOrEmpty()) {
            _errorMessagePersonalDetails.value = "Name can't be empty"
            return
        }

        if (admission_numValue.isNullOrEmpty()) {
            _errorMessagePersonalDetails.value = "Admission Number can't be empty"
            return
        }

        if (phone_numValue.isNullOrEmpty()) {
            _errorMessagePersonalDetails.value = "Phone Number can't be empty"
            return
        }

        if (phone_numValue.length!=10) {
            _errorMessagePersonalDetails.value = "Enter a valid Phone Number"
            return
        }


        _personalDetailsPageResult.value = true

    }

    fun validateInputsOnTechnicalDetailsPage() {

        if (interestedDomains.value!!.trim().isNullOrEmpty()) {
            _errorMessageTechncialDetails.value = "Fill atleast one interested domain"
            return
        }
        if (knownLanguages.value!!.trim().isNullOrEmpty()) {
            _errorMessageTechncialDetails.value = "Fill atleast one known language"
            return
        }

        _technicalDetailsPageResult.value = true

    }

    fun validateInputsOnSocialDetailsPage() {
        _socialDetailsPageResult.value = true
    }

    fun validateInputsOnKYCDetailsPage() {
        Log.d("validateInputsOnKYCDetailsPage","fun called")
        Log.d("validateInputsOnKYCDetailsPage","selfie: ${_userSelfie.value}")
        Log.d("validateInputsOnKYCDetailsPage","collegeid:${_userCollegeID.value}")

        if (_userSelfie.value.isNullOrEmpty()){
            _errorMessageKYCDetails.value = "Your selfie is required"
            return
        }
        if (_userCollegeID.value.isNullOrEmpty()){
            _errorMessageKYCDetails.value = "Your college ID is required"
            return
        }
        _kycDetailsPageResult.value = true
    }
}
