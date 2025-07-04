package com.ncs.marioapp.UI.SettingsScreen.EditProfile

import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.ncs.marioapp.Domain.HelperClasses.PrefManager
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.gone
import com.ncs.marioapp.Domain.Utility.GlobalUtils
import com.ncs.marioapp.R
import com.ncs.marioapp.databinding.ActivityEditProfileBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProfileActivity : AppCompatActivity() {
    private val binding: ActivityEditProfileBinding by lazy {
        ActivityEditProfileBinding.inflate(layoutInflater)
    }

    private val surveyViewModel: EditProfileViewModel by viewModels()
    private val util: GlobalUtils.EasyElements by lazy {
        GlobalUtils.EasyElements(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        enableEdgeToEdge()
        WindowCompat.getInsetsController(window, window.decorView)?.isAppearanceLightStatusBars = false
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
        onBackPressedDispatcher.addCallback(this) {
            finish()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
        }
        setInitials()

        setUpViews()

        binding.personalDetails.title.text="Personal Details"
        binding.technicalDetails.title.text="Technical Details"
        binding.socialDetails.title.text="Social Links"

        surveyViewModel.currentStep.observe(this, Observer { step ->
            updateSurveyProgress(step)
        })
    }

    private fun setUpViews() {
        binding.actionbar.btnHam.setImageResource(R.drawable.ic_back_arrow)
        binding.actionbar.btnHam.setOnClickListener {
            onBackPressed()
        }
        binding.actionbar.titleTv.text = "Edit Profile"
        binding.actionbar.score.gone()
    }

    fun setInitials(){
        val currentUserProfile=PrefManager.getUserProfile()!!
        surveyViewModel.apply {
            setName(currentUserProfile.name)
            setAdmissionNum(currentUserProfile.admission_number)
            setBranch(currentUserProfile.branch)
            setAdmittedTo(currentUserProfile.admitted_to)
            setYear(currentUserProfile.year.toString())
            setLinkedIn(currentUserProfile.socials.LinkedIn)
            setGithub(currentUserProfile.socials.GitHub)
            setBehance(currentUserProfile.socials.Behance)
            setAdmittedTo(if(currentUserProfile.admitted_to=="") "COLLEGE" else currentUserProfile.admitted_to)
            setSelectedDomains(currentUserProfile.domain, currentUserProfile.other_domain)
        }
    }

    private fun updateSurveyProgress(step: EditProfileViewModel.SurveyStep) {
        binding.personalDetails.circle.setColorFilter(getColor(R.color.edit_text_hint))
        binding.technicalDetails.circle.setColorFilter(getColor(R.color.edit_text_hint))
        binding.socialDetails.circle.setColorFilter(getColor(R.color.edit_text_hint))

        when (step) {
            EditProfileViewModel.SurveyStep.PERSONAL_DETAILS -> {
                binding.personalDetails.circle.setColorFilter(getColor(R.color.yellow))
                binding.afterPersonalDetails.setBackgroundColor(getColor(R.color.edit_text_hint))
                binding.afterTechnicalDetails.setBackgroundColor(getColor(R.color.edit_text_hint))
            }
            EditProfileViewModel.SurveyStep.TECHNICAL_DETAILS -> {
                binding.personalDetails.circle.setColorFilter(getColor(R.color.appblue))
                binding.afterPersonalDetails.setBackgroundColor(getColor(R.color.appblue))
                binding.technicalDetails.circle.setColorFilter(getColor(R.color.yellow))
                binding.afterTechnicalDetails.setBackgroundColor(getColor(R.color.edit_text_hint))
            }
            EditProfileViewModel.SurveyStep.SOCIAL_DETAILS -> {
                binding.personalDetails.circle.setColorFilter(getColor(R.color.appblue))
                binding.afterPersonalDetails.setBackgroundColor(getColor(R.color.appblue))
                binding.technicalDetails.circle.setColorFilter(getColor(R.color.appblue))
                binding.afterTechnicalDetails.setBackgroundColor(getColor(R.color.appblue))
                binding.socialDetails.circle.setColorFilter(getColor(R.color.yellow))
            }
            EditProfileViewModel.SurveyStep.KYC_DETAILS -> {
                binding.personalDetails.circle.setColorFilter(getColor(R.color.appblue))
                binding.afterPersonalDetails.setBackgroundColor(getColor(R.color.appblue))
                binding.technicalDetails.circle.setColorFilter(getColor(R.color.appblue))
                binding.afterTechnicalDetails.setBackgroundColor(getColor(R.color.appblue))
                binding.socialDetails.circle.setColorFilter(getColor(R.color.appblue))
            }
        }
    }
}
