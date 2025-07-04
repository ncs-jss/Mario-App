package com.ncs.marioapp.UI.SurveyScreen


import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.ncs.marioapp.Domain.HelperClasses.PrefManager
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.isNull
import com.ncs.marioapp.Domain.Utility.GlobalUtils
import com.ncs.marioapp.R
import com.ncs.marioapp.databinding.ActivitySurveyBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SurveyActivity : AppCompatActivity() {
    private val binding: ActivitySurveyBinding by lazy {
        ActivitySurveyBinding.inflate(layoutInflater)
    }

    private val surveyViewModel: SurveyViewModel by viewModels()
    private val util: GlobalUtils.EasyElements by lazy {
        GlobalUtils.EasyElements(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
       surveyViewModel.setFcmTokenToPref()
        if (PrefManager.getShowProfileCompletionAlert()){
            PrefManager.setShowProfileCompletionAlert(false)
            util.showSnackbar(binding.root, "Please complete your profile", 2000)
            setInitials()
        }

        if (!PrefManager.getAlertMessage().isNull){
            util.showSnackbar(binding.root, PrefManager.getAlertMessage()!!, 2000)
            PrefManager.setAlertMessage(null)
        }

        binding.personalDetails.title.text="Personal Details"
        binding.technicalDetails.title.text="Technical Details"
        binding.socialDetails.title.text="Social Links"
        binding.kycDetails.title.text="KYC Details"

        surveyViewModel.currentStep.observe(this, Observer { step ->
            updateSurveyProgress(step)
        })
    }

    fun setInitials(){
        val currentUserProfile=PrefManager.getUserProfileForCache()!!
        surveyViewModel.apply {
            setName(currentUserProfile.name)
            setAdmissionNum(currentUserProfile.admission_number)
            setBranch(currentUserProfile.branch)
            setYear(when(currentUserProfile.year){
                1->"I Year"
                2->"II Year"
                3->"III Year"
                4->"IV Year"
                else->"0"
            })
            setAdmittedTo(if(currentUserProfile.admitted_to=="") "COLLEGE" else currentUserProfile.admitted_to)
            setLinkedIn(currentUserProfile.socials.LinkedIn)
            setGithub(currentUserProfile.socials.GitHub)
            setBehance(currentUserProfile.socials.Behance)
            setSelectedDomains(currentUserProfile.domain, currentUserProfile.other_domain)
        }
    }

    private fun updateSurveyProgress(step: SurveyViewModel.SurveyStep) {
        binding.personalDetails.circle.setColorFilter(getColor(R.color.edit_text_hint))
        binding.technicalDetails.circle.setColorFilter(getColor(R.color.edit_text_hint))
        binding.socialDetails.circle.setColorFilter(getColor(R.color.edit_text_hint))
        binding.kycDetails.circle.setColorFilter(getColor(R.color.edit_text_hint))

        when (step) {
            SurveyViewModel.SurveyStep.PERSONAL_DETAILS -> {
                binding.personalDetails.circle.setColorFilter(getColor(R.color.yellow))
                binding.afterPersonalDetails.setBackgroundColor(getColor(R.color.edit_text_hint))
                binding.afterTechnicalDetails.setBackgroundColor(getColor(R.color.edit_text_hint))
                binding.afterSocialDetails.setBackgroundColor(getColor(R.color.edit_text_hint))
            }
            SurveyViewModel.SurveyStep.TECHNICAL_DETAILS -> {
                binding.personalDetails.circle.setColorFilter(getColor(R.color.appblue))
                binding.afterPersonalDetails.setBackgroundColor(getColor(R.color.appblue))
                binding.technicalDetails.circle.setColorFilter(getColor(R.color.yellow))
                binding.afterTechnicalDetails.setBackgroundColor(getColor(R.color.edit_text_hint))
                binding.afterSocialDetails.setBackgroundColor(getColor(R.color.edit_text_hint))
            }
            SurveyViewModel.SurveyStep.SOCIAL_DETAILS -> {
                binding.personalDetails.circle.setColorFilter(getColor(R.color.appblue))
                binding.afterPersonalDetails.setBackgroundColor(getColor(R.color.appblue))
                binding.technicalDetails.circle.setColorFilter(getColor(R.color.appblue))
                binding.afterTechnicalDetails.setBackgroundColor(getColor(R.color.appblue))
                binding.socialDetails.circle.setColorFilter(getColor(R.color.yellow))
                binding.afterSocialDetails.setBackgroundColor(getColor(R.color.edit_text_hint))
            }
            SurveyViewModel.SurveyStep.KYC_DETAILS -> {
                binding.personalDetails.circle.setColorFilter(getColor(R.color.appblue))
                binding.afterPersonalDetails.setBackgroundColor(getColor(R.color.appblue))
                binding.technicalDetails.circle.setColorFilter(getColor(R.color.appblue))
                binding.afterTechnicalDetails.setBackgroundColor(getColor(R.color.appblue))
                binding.socialDetails.circle.setColorFilter(getColor(R.color.appblue))
                binding.afterSocialDetails.setBackgroundColor(getColor(R.color.appblue))
                binding.kycDetails.circle.setColorFilter(getColor(R.color.yellow))
            }
        }
    }
}
