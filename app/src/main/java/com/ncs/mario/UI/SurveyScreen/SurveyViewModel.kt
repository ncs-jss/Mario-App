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

    val errorMessageTechncialDetails = MutableLiveData<String?>()

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
    val branch = MutableLiveData<String>(null)
    val year = MutableLiveData<String>(null)

    val selectedDomains = MutableLiveData<List<String>>(emptyList())
    private var othersText: String? = null


    val linkedIn = MutableLiveData<String>(null)
    val github = MutableLiveData<String>(null)
    val behance = MutableLiveData<String>(null)
    val link1 = MutableLiveData<String>(null)
    val link2 = MutableLiveData<String>(null)

    private val _personalDetailsPageResult = MutableLiveData<Boolean>()
    val personalDetailsPageResult: LiveData<Boolean> get() = _personalDetailsPageResult

    val technicalDetailsPageResult = MutableLiveData<Boolean>()


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
        errorMessageTechncialDetails.value = null
    }

    fun resetTechnicalDetailsPageResult() {
        technicalDetailsPageResult.value = false
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

    fun setOthersText(text: String?) {
        othersText = text
    }

    fun getOthersText(): String? {
        return othersText
    }

    fun validateInputsOnPersonalDetailsPage() {
        Log.d("SurveyViewModel", "validateInputsOnPersonalDetailsPage called")
        val nameValue=name.value?.trim()
        val admission_numValue=admission_num.value?.trim()
        val branch_value=branch.value?.trim()
        val year_value=year.value?.trim()
        if (nameValue.isNullOrEmpty()) {
            _errorMessagePersonalDetails.value = "Name can't be empty"
            return
        }

        if (admission_numValue.isNullOrEmpty()) {
            _errorMessagePersonalDetails.value = "Admission Number can't be empty"
            return
        }

        if (branch_value.isNullOrEmpty() || branch_value=="Branch"){
            _errorMessagePersonalDetails.value = "Select your branch"
            return
        }

        if (year_value.isNullOrEmpty() || year_value=="Year"){
            _errorMessagePersonalDetails.value = "Select your year"
            return
        }

        _personalDetailsPageResult.value = true

    }

    fun addDomain(domain: String) {
        val currentList = selectedDomains.value.orEmpty().toMutableList()
        if (currentList.size < 3) {
            currentList.add(domain)
            selectedDomains.value = currentList
        }
    }

    fun removeDomain(domain: String) {
        val currentList = selectedDomains.value.orEmpty().toMutableList()
        currentList.remove(domain)
        selectedDomains.value = currentList
    }

    fun validateInputsOnTechnicalDetailsPage() {
        if (selectedDomains.value.isNullOrEmpty()) {
            errorMessageTechncialDetails.value = "Fill at least one interested domain"
            return
        }

        if (selectedDomains.value!!.contains("Others") && getOthersText().isNullOrBlank()) {
            errorMessageTechncialDetails.value = "Please specify correctly"
            return
        }

        technicalDetailsPageResult.value = true
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
