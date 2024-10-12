package com.ncs.mario.UI.SurveyScreen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.ncs.mario.Domain.Api.ProfileApiService
import com.ncs.mario.Domain.HelperClasses.PrefManager
import com.ncs.mario.Domain.Models.CreateProfileBody
import com.ncs.mario.Domain.Models.ImageBody
import com.ncs.mario.Domain.Models.LoginBody
import com.ncs.mario.Domain.Models.ServerResponse
import com.ncs.mario.Domain.Models.SetFCMTokenBody
import com.ncs.mario.Domain.Models.UpdateProfileBody
import com.ncs.mario.Domain.Utility.ExtensionsUtil.isNull
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class SurveyViewModel @Inject constructor(val profileApiService: ProfileApiService) : ViewModel() {
    enum class SurveyStep {
        PERSONAL_DETAILS,
        TECHNICAL_DETAILS,
        SOCIAL_DETAILS,
        KYC_DETAILS
    }

    private val _progressState = MutableLiveData<Boolean>(false)
    val progressState: LiveData<Boolean> get() = _progressState

    private val _progressStateImageUpload = MutableLiveData<Boolean>(false)
    val progressStateImageUpload: LiveData<Boolean> get() = _progressStateImageUpload

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

    private val _profileCreateResult = MutableLiveData<Boolean>()
    val profileCreateResult: LiveData<Boolean> get() = _profileCreateResult

    private val _currentStep = MutableLiveData(SurveyStep.PERSONAL_DETAILS)
    val currentStep: LiveData<SurveyStep> = _currentStep

    fun setName(_name: String) {
        name.value = _name
    }

    fun setAdmissionNum(_admission_num: String) {
        admission_num.value = _admission_num
    }

    fun setBranch(_branch: String) {
        branch.value = _branch
    }

    fun setYear(_year: String) {
        if (_year!="0") {
            when (_year) {
                "1" -> year.value = "I Year"
                "2" -> year.value = "II Year"
                "3" -> year.value = "III Year"
                "4" -> year.value = "IV Year"
            }
            year.value = _year
        }
    }

    fun setLinkedIn(_linkedIn: String) {
        linkedIn.value = _linkedIn
    }

    fun setGithub(_github: String) {
        github.value = _github
    }

    fun setBehance(_behance: String) {
        behance.value = _behance
    }

    fun setSelectedDomains(_selectedDomains: List<String>, otherText:String){
        val list=_selectedDomains.toMutableList()
        if (list.contains("Other")){
            list.remove("Other")
            list.add("Others")
            othersText = otherText
        }
        selectedDomains.value = list
    }

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
        if (PrefManager.getUserProfile()!!.name!=""){
            updateProfile()
        }
        else {
            uploadUserProfile()
        }
    }

    fun uploadUserProfile(){
        viewModelScope.launch {
            _progressState.postValue(true)
            try {
                var userYear = 1
                when(year.value){
                    "I Year"-> userYear=1
                    "II Year"->userYear=2
                    "III Year"->userYear=3
                    "IV Year"->userYear=4
                }
                val domains=selectedDomains.value!!.toMutableList()
                if (domains.contains("Others")){
                    domains.remove("Others")
                    domains.add("Other")
                }
                val payload=CreateProfileBody(
                    FCM_token = "fcmtoken",
                    admission_number = admission_num.value!!.trim(),
                    branch = branch.value!!.trim(),
                    domain = domains,
                    other_domain = othersText!!,
                    name = name.value!!.trim(),
                    socials = mapOf(
                        "GitHub" to github.value!!.trim(),
                        "LinkedIn" to linkedIn.value!!.trim(),
                        "Behance" to behance.value!!.trim(),
                    ),
                    year = userYear
                )
                val response = profileApiService.createUserProfile(payload = payload)
                if (response.isSuccessful) {
                    Log.d("signupResult", "Profile Create: ${response.body()}")
                    _errorMessageKYCDetails.value = "Profile Created"
                    _profileCreateResult.value = true

                } else {
                    val errorResponse = response.errorBody()?.string()
                    val loginResponse = Gson().fromJson(errorResponse, ServerResponse::class.java)
                    Log.d("signupResult", "Profile Create Failed: ${loginResponse.message}")
                    _errorMessageKYCDetails.value = loginResponse.message
                    _kycDetailsPageResult.value = false
                    _profileCreateResult.value = false
                }
            } catch (e: Exception) {
                _kycDetailsPageResult.value = false
                _profileCreateResult.value = false
            } finally {
                _progressState.postValue(false)
            }
        }
    }

    fun updateProfile(){
        viewModelScope.launch {
            _progressState.postValue(true)
            try {
                var userYear = 1
                when(year.value){
                    "I Year"-> userYear=1
                    "II Year"->userYear=2
                    "III Year"->userYear=3
                    "IV Year"->userYear=4
                }
                val domains=selectedDomains.value!!.toMutableList()
                if (domains.contains("Others")){
                    domains.remove("Others")
                    domains.add("Other")
                }
                val payload=UpdateProfileBody(
                    branch = branch.value!!.trim(),
                    domain = domains,
                    other_domain = othersText!!,
                    name = name.value!!.trim(),
                    socials = mapOf(
                        "GitHub" to github.value!!.trim(),
                        "LinkedIn" to linkedIn.value!!.trim(),
                        "Behance" to behance.value!!.trim(),
                    ),
                    year = userYear
                )
                val response = profileApiService.updateUserProfile(payload = payload)
                if (response.isSuccessful) {
                    Log.d("signupResult", "Profile Update: ${response.body()}")
                    _errorMessageKYCDetails.value = "Profile Updated"
                    _profileCreateResult.value = true

                } else {
                    val errorResponse = response.errorBody()?.string()
                    val loginResponse = Gson().fromJson(errorResponse, ServerResponse::class.java)
                    Log.d("signupResult", "Profile Update Failed: ${loginResponse.message}")
                    _errorMessageKYCDetails.value = loginResponse.message
                    _kycDetailsPageResult.value = false
                    _profileCreateResult.value = false
                }
            } catch (e: Exception) {
                _kycDetailsPageResult.value = false
                _profileCreateResult.value = false
            } finally {
                _progressState.postValue(false)
            }
        }
    }

    fun uploadUserImage(uri: Uri, context: Context) {
        viewModelScope.launch {
            _progressStateImageUpload.postValue(true)
            try {
                val bitmap = getBitmapFromUri(uri, context)
                val base64Image = bitmapToBase64WithMimeType(bitmap)

                val response = profileApiService.uploadUserPicture(payload = ImageBody(base64Image))
                if (response.isSuccessful) {
                    Log.d("signupResult", "User Image Upload: ${response.body()}")
                    _errorMessageKYCDetails.value = "Your selfie was uploaded"
                    uploadCollegeIDImage(uri = Uri.parse(userCollegeID.value), context = context)
                } else {
                    val errorResponse = response.errorBody()?.string()
                    val loginResponse = Gson().fromJson(errorResponse, ServerResponse::class.java)
                    Log.d("signupResult", "User Image Upload Failed: ${loginResponse.message}")
                    _errorMessageKYCDetails.value = loginResponse.message
                    _kycDetailsPageResult.value = false
                }
            } catch (e: Exception) {
                _kycDetailsPageResult.value = false
            } finally {
                _progressStateImageUpload.postValue(false)
            }
        }
    }

    fun uploadCollegeIDImage(uri: Uri, context: Context) {
        viewModelScope.launch {
            _progressStateImageUpload.postValue(true)
            try {
                val bitmap = getBitmapFromUri(uri, context)
                val base64Image = bitmapToBase64WithMimeType(bitmap)

                val response = profileApiService.addCollegeIDPicture(payload = ImageBody(base64Image))
                if (response.isSuccessful) {
                    Log.d("signupResult", "CollegeID Image Upload: ${response.body()}")
                    _errorMessageKYCDetails.value = "Your College ID was uploaded"
                    _kycDetailsPageResult.value = true
                } else {
                    val errorResponse = response.errorBody()?.string()
                    val loginResponse = Gson().fromJson(errorResponse, ServerResponse::class.java)
                    Log.d("signupResult", "CollegeID Image Upload Failed: ${loginResponse.message}")
                    _errorMessageKYCDetails.value = loginResponse.message
                    _kycDetailsPageResult.value = false
                }
            } catch (e: Exception) {
                _kycDetailsPageResult.value = false
            } finally {
                _progressStateImageUpload.postValue(false)
            }
        }
    }


    private fun getBitmapFromUri(uri: Uri, context: Context): Bitmap {
        val inputStream = context.contentResolver.openInputStream(uri)
        return BitmapFactory.decodeStream(inputStream)
    }

    private fun bitmapToBase64WithMimeType(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        val base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT)

        return "data:image/jpeg;base64,$base64Image"
    }

}
