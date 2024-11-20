package com.ncs.marioapp.UI.SurveyScreen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.ncs.marioapp.Domain.Api.ProfileApiService
import com.ncs.marioapp.Domain.HelperClasses.PrefManager
import com.ncs.marioapp.Domain.HelperClasses.WorkerUtil.startProfileUploadWorkflow
import com.ncs.marioapp.Domain.Models.CreateProfileBody
import com.ncs.marioapp.Domain.Models.ImageBody
import com.ncs.marioapp.Domain.Models.ImageUploadResult
import com.ncs.marioapp.Domain.Models.ServerResponse
import com.ncs.marioapp.Domain.Models.UpdateProfileBody
import com.ncs.marioapp.Domain.Models.WorkerFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class SurveyViewModel @Inject constructor(val profileApiService: ProfileApiService) : ViewModel() {
    enum class SurveyStep {
        PERSONAL_DETAILS,
        TECHNICAL_DETAILS,
        SOCIAL_DETAILS,
        KYC_DETAILS
    }


    private val _finalProgressState = MutableLiveData<Boolean>(false)
    val finalProgressState: LiveData<Boolean> get() = _finalProgressState


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

//    private val _userImageUploadToken = MutableLiveData<String?>(null)
//    val userImageUploadToken:LiveData<String?> get() = _userImageUploadToken
//
//    private val _userCollegeIdToken = MutableLiveData<String?>(null)
//    val userCollegeIdToken: LiveData<String?> get() = _userSelfie

    private val _userSelfie = MutableLiveData<String>(null)
    val userSelfie: LiveData<String?> get() = _userSelfie

    private val _userCollegeID = MutableLiveData<String?>(null)
    val userCollegeID: LiveData<String?> get() = _userCollegeID

    private val _listOfWorkIDs = MutableLiveData<List<String>>(null)
    val listOfWorkIDs: LiveData<List<String>> get() = _listOfWorkIDs

    val name = MutableLiveData<String>(null)
    val admission_num = MutableLiveData<String>(null)
    val admitted_to = MutableLiveData<String>(null)
    val branch = MutableLiveData<String>(null)
    val year = MutableLiveData<String>(null)
    val admittedTo = MutableLiveData<String>(null)

    val selectedDomains = MutableLiveData<List<String>>(emptyList())
    private var othersText: String? = null


    val linkedIn = MutableLiveData<String>(null)
    val github = MutableLiveData<String>(null)
    val behance = MutableLiveData<String>(null)
    val link1 = MutableLiveData<String>(null)
    val link2 = MutableLiveData<String>(null)

    private val _personalDetailsPageResult = MutableLiveData<Boolean>()
    val personalDetailsPageResult: LiveData<Boolean> get() = _personalDetailsPageResult

    private val _workerResult = MutableLiveData<Boolean>()
    val workerResult: LiveData<Boolean> get() = _workerResult

    val technicalDetailsPageResult = MutableLiveData<Boolean>()


    private val _socialDetailsPageResult = MutableLiveData<Boolean>()
    val socialDetailsPageResult: LiveData<Boolean> get() = _socialDetailsPageResult

    private val _kycDetailsPageResult = MutableLiveData<Boolean>()
    val kycDetailsPageResult: LiveData<Boolean> get() = _kycDetailsPageResult

    private val _profileCreateResult = MutableLiveData<Boolean>(false)
    val profileCreateResult: LiveData<Boolean> get() = _profileCreateResult

    private val _currentStep = MutableLiveData(SurveyStep.PERSONAL_DETAILS)
    val currentStep: LiveData<SurveyStep> = _currentStep

    init {
        _userSelfie.value = PrefManager.getUserDPCacheData()
        _userCollegeID.value = PrefManager.getCollegeIDCacheData()
    }

    fun setName(_name: String) {
        name.value = _name
    }

    fun setAdmissionNum(_admission_num: String) {
        admission_num.value = _admission_num
    }

    fun setBranch(_branch: String) {
        branch.value = _branch
    }

    fun setAdmittedTo(_admittedTo: String) {
        admittedTo.value = _admittedTo
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
        val admitted_to_value=admittedTo.value?.trim()
        if (nameValue.isNullOrEmpty()) {
            _errorMessagePersonalDetails.value = "Name can't be empty"
            return
        }

        if (admission_numValue.isNullOrEmpty()) {
            _errorMessagePersonalDetails.value = "Admission Number can't be empty"
            return
        }
        if (admitted_to_value.isNullOrEmpty()|| admitted_to_value=="JSSATEN | JSS UNIVERSITY") {
            _errorMessagePersonalDetails.value = "Select Institute"
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

        if (admitted_to_value.isNullOrEmpty() || admitted_to_value=="Admitted To"){
            _errorMessagePersonalDetails.value = "Select your college or university"
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

    fun validateSelfie(context: Context){
        _finalProgressState.value=true
        if (_userSelfie.value.isNullOrEmpty()){
            _errorMessageKYCDetails.value = "Your selfie is required"
            return
        }
        uploadUserImage(uri = Uri.parse(userSelfie.value!!), context = context)
    }

    fun validateCollegeID(context: Context){
        _finalProgressState.value=true
        if (_userCollegeID.value.isNullOrEmpty()){
            _errorMessageKYCDetails.value = "Your college ID is required"
            return
        }
        uploadCollegeIDImage(uri = Uri.parse(userCollegeID.value), context = context)
    }

    fun validateInputsOnKYCDetailsPage(context: Context) {
        Log.d("validateInputsOnKYCDetailsPage","fun called")
        Log.d("validateInputsOnKYCDetailsPage","selfie: ${_userSelfie.value}")
        Log.d("validateInputsOnKYCDetailsPage","collegeid:${_userCollegeID.value}")

        if (userSelfie.value.isNullOrEmpty()){
            _errorMessageKYCDetails.value = "Your selfie is required"
            return
        }
        if (userCollegeID.value.isNullOrEmpty()){
            _errorMessageKYCDetails.value = "Your College ID is required"
            return
        }



        requestCreateOrUpdateProfile(context){
            _listOfWorkIDs.value=it
        }
    }

    fun uploadProfileWorkflow(
        userImageUri: Uri,
        collegeIdUri: Uri,
        createProfilePayload: CreateProfileBody,
        isUpdate: Boolean,
        context: Context,
        callback: (List<String>)->Unit
    ) {
        startProfileUploadWorkflow(userImageUri, collegeIdUri, createProfilePayload, isUpdate, context){
            callback(it)
        }
    }


    fun requestCreateOrUpdateProfile(context: Context, callback: (List<String>)->Unit) {
        _kycDetailsPageResult.value = false
        _finalProgressState.value = true

        viewModelScope.launch {
            try {
                val response = profileApiService.getKYCHeader()
                if (response.isSuccessful) {
                    val status = response.body()?.get("is_approved")?.asString
                    if (status == "REJECT") {
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
                            year = userYear,
                            admitted_to = admittedTo.value!!.trim(),
                            photo_token = "",
                            id_card_token = ""
                        )
                        _workerResult.value=true
                        PrefManager.saveWorkerFlow(
                            WorkerFlow(
                                userImageUri = userSelfie.value!!,
                                collegeIdUri = userCollegeID.value!!,
                                createProfilePayload=payload,
                                isUpdate = true,
                            )
                        )
                        uploadProfileWorkflow(
                            userImageUri = Uri.parse(userSelfie.value!!),
                            collegeIdUri = Uri.parse(userCollegeID.value),
                            createProfilePayload = payload,
                            isUpdate = true,
                            context = context
                        ){
                            callback(it)
                        }
                    }
                } else {
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
                        year = userYear,
                        admitted_to = admittedTo.value!!.trim(),
                        photo_token = "",
                        id_card_token = ""
                    )
                    _workerResult.value=true
                    PrefManager.saveWorkerFlow(
                        WorkerFlow(
                            userImageUri = userSelfie.value!!,
                            collegeIdUri = userCollegeID.value!!,
                            createProfilePayload=payload,
                            isUpdate = false,
                        )
                    )
                    uploadProfileWorkflow(
                        userImageUri = Uri.parse(userSelfie.value!!),
                        collegeIdUri = Uri.parse(userCollegeID.value),
                        createProfilePayload = payload,
                        isUpdate = false,
                        context = context
                    ){
                        callback(it)
                    }
                }

            } catch (e: SocketTimeoutException) {
                Log.e("signupResult", "SocketTimeoutException occurred", e)
            } catch (e: IOException) {
                Log.d("signupResult", e.message.toString())
                _kycDetailsPageResult.value = false
                _errorMessageKYCDetails.value = "Network error. Please check your connection."
            } catch (e: Exception) {
                Log.e("signupResult", "Unexpected exception occurred", e)
            } finally {
                Log.d("signupResult", "API call completed")
            }
        }
    }

    fun uploadUserProfile(){
        viewModelScope.launch {
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
                        year = userYear,
                        admitted_to = admittedTo.value!!.trim(),
                        photo_token = PrefManager.getUserSelfieToken()!!,
                        id_card_token = PrefManager.getUserCollegeIDToken()!!
                    )
                    val response = profileApiService.createUserProfile(payload = payload)
                    if (response.isSuccessful) {
                        _errorMessageKYCDetails.value = "Profile Created"
                        _kycDetailsPageResult.value = true
                    }
                    else {
                        val errorResponse = response.errorBody()?.string()
                        val loginResponse = Gson().fromJson(errorResponse, ServerResponse::class.java)
                        Log.d("signupResult", "Profile Create Failed: ${loginResponse.message}")
                        _errorMessageKYCDetails.value = loginResponse.message
                        _kycDetailsPageResult.value = false
                    }

            } catch (e: IOException) {
                Log.d("signupResult",e.message.toString())
                _kycDetailsPageResult.value = false
                _errorMessageKYCDetails.value = "Network error. Please check your connection."
            }
            catch (e: Exception) {
                _kycDetailsPageResult.value = false

            } finally {
                _finalProgressState.postValue(false)
            }
        }
    }

    fun updateProfile(){
        viewModelScope.launch {
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
                    admitted_to = admittedTo.value!!.trim(),
                    name = name.value!!.trim(),
                    socials = mapOf(
                        "GitHub" to github.value!!.trim(),
                        "LinkedIn" to linkedIn.value!!.trim(),
                        "Behance" to behance.value!!.trim(),
                    ),
                    year = userYear,
                    photo_token = PrefManager.getUserSelfieToken()!!,
                    id_card_token = PrefManager.getUserCollegeIDToken()!!
                )

                val response = profileApiService.updateUserProfile(payload = payload)
                if (response.isSuccessful) {
                    Log.d("signupResult", "Profile Update: ${response.body()}")
                    _errorMessageKYCDetails.value = "Re-requested"
//                    requestKycStatusChange()
                } else {
                    val errorResponse = response.errorBody()?.string()
                    val loginResponse = Gson().fromJson(errorResponse, ServerResponse::class.java)
                    Log.d("signupResult", "Profile Update Failed: ${loginResponse.message}")
                    _errorMessageKYCDetails.value = loginResponse.message
                    _kycDetailsPageResult.value = false
                    _finalProgressState.value=false
                }
            } catch (e: IOException) {
                Log.d("signupResult",e.message.toString())
                _kycDetailsPageResult.value = false
                _errorMessageKYCDetails.value = "Network error. Please check your connection."
            }
            catch (e: Exception) {
                _kycDetailsPageResult.value = false
                _finalProgressState.value=false
            } finally {
                _finalProgressState.value=false
            }
        }
    }

    private fun requestKycStatusChange(context: Context, callback: (List<String>) -> Unit) {
        _kycDetailsPageResult.value = false
        _finalProgressState.value=true
        viewModelScope.launch {
            try {
                val response = profileApiService.requestKYCToPending()
                if (response.isSuccessful) {
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
                        year = userYear,
                        admitted_to = admittedTo.value!!.trim(),
                        photo_token = "",
                        id_card_token = ""
                    )
                    _workerResult.value=true
                    uploadProfileWorkflow(
                        userImageUri = Uri.parse(userSelfie.value!!),
                        collegeIdUri = Uri.parse(userCollegeID.value),
                        createProfilePayload = payload,
                        isUpdate = true,
                        context = context
                    ){
                        callback(it)
                    }
                } else {
                    val errorResponse = response.errorBody()?.string()
                    _errorMessageKYCDetails.value = "Something went wrong."
                }
            }
            catch (e: SocketTimeoutException) {
                Log.e("signupResult", "SocketTimeoutException occurred", e)
            }
            catch (e: IOException) {
                Log.d("signupResult",e.message.toString())
                _kycDetailsPageResult.value = false
                _errorMessageKYCDetails.value = "Network error. Please check your connection."
            } catch (e: Exception) {
                Log.e("signupResult", "Unexpected exception occurred", e)
            } finally {
                Log.d("signupResult", "API call completed")
            }
        }
    }

    fun uploadUserImage(uri: Uri, context: Context) {
        viewModelScope.launch {
            _finalProgressState.value=true
            try {
                val bitmap = getBitmapFromUri(uri, context)
                val base64Image = bitmapToBase64WithMimeType(bitmap)

                val response = profileApiService.uploadUserPicture(payload = ImageBody(base64Image))
                if (response.isSuccessful) {
                    Log.d("signupResult", "User Image Upload: ${response.body()}")
                    val responseResult = response.body()
                    val uploadRes = Gson().fromJson(responseResult, ImageUploadResult::class.java)
//                    _userImageUploadToken.postValue(uploadRes.photo_token)
                    uploadRes.photo_token?.let { PrefManager.setUserSelfieToken(it) }
                    _errorMessageKYCDetails.value = "Your selfie was uploaded"

//                    uploadCollegeIDImage(uri = Uri.parse(userCollegeID.value), context = context)
                } else {
                    val errorResponse = response.errorBody()?.string()
                    val loginResponse = Gson().fromJson(errorResponse, ImageUploadResult::class.java)
                    Log.d("signupResult", "User Image Upload Failed: ${loginResponse.message}")
                    _errorMessageKYCDetails.value = loginResponse.message
                    _kycDetailsPageResult.value = true
                }
            }
            catch (e: IOException) {
                Log.d("signupResult",e.message.toString())
                _kycDetailsPageResult.value = false
                _errorMessageKYCDetails.value = "Network error. Please check your connection."
            }
            catch (e: Exception) {
                _kycDetailsPageResult.value = false
            }
            finally {
                 _finalProgressState.value=false
            }
        }
    }

    fun uploadCollegeIDImage(uri: Uri, context: Context) {
        viewModelScope.launch {
            _finalProgressState.value=true
            try {
                val bitmap = getBitmapFromUri(uri, context)
                val base64Image = bitmapToBase64WithMimeType(bitmap)

                val response = profileApiService.addCollegeIDPicture(payload = ImageBody(base64Image))
                if (response.isSuccessful) {
                    Log.d("signupResult", "CollegeID Image Upload: ${response.body()}")
                    val responseResult = response.body()
                    val uploadRes = Gson().fromJson(responseResult, ImageUploadResult::class.java)
//                    _userCollegeIdToken.postValue(uploadRes.photo_token)
                    uploadRes.id_card_token?.let { PrefManager.setUserCollegeIDToken(it) }
                    _errorMessageKYCDetails.value = "Your CollegeID was uploaded"
                    Log.d("signupResult", "CollegeID Image Upload Success")
//                    _profileCreateResult.postValue(true)
//                    _kycDetailsPageResult.value = true
                } else {
                    val errorResponse = response.errorBody()?.string()
                    val loginResponse = Gson().fromJson(errorResponse, ServerResponse::class.java)
                    Log.d("signupResult", "CollegeID Image Upload Failed: ${loginResponse.message}")

                    _errorMessageKYCDetails.value = loginResponse.message
                    _kycDetailsPageResult.value = false
                }
            }catch (e: IOException) {
                Log.d("signupResult",e.message.toString())
                _kycDetailsPageResult.value = false
                _errorMessageKYCDetails.value = "Network error. Please check your connection."
            }
            catch (e: Exception) {
                Log.d("signupResult",e.message.toString())
                _kycDetailsPageResult.value = false
            } finally {
                _finalProgressState.value=false
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

