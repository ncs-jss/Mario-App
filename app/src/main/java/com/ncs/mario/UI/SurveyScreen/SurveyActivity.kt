package com.ncs.mario.UI.SurveyScreen


import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.ncs.mario.R
import com.ncs.mario.databinding.ActivitySurveyBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SurveyActivity : AppCompatActivity() {
    private val binding: ActivitySurveyBinding by lazy {
        ActivitySurveyBinding.inflate(layoutInflater)
    }

    private val surveyViewModel: SurveyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.personalDetails.title.text="Personal Details"
        binding.technicalDetails.title.text="Technical Details"
        binding.socialDetails.title.text="Social Links"
        binding.kycDetails.title.text="KYC Details"

        surveyViewModel.currentStep.observe(this, Observer { step ->
            updateSurveyProgress(step)
        })
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
                binding.personalDetails.circle.setColorFilter(getColor(R.color.green))
                binding.afterPersonalDetails.setBackgroundColor(getColor(R.color.green))
                binding.technicalDetails.circle.setColorFilter(getColor(R.color.yellow))
                binding.afterTechnicalDetails.setBackgroundColor(getColor(R.color.edit_text_hint))
                binding.afterSocialDetails.setBackgroundColor(getColor(R.color.edit_text_hint))
            }
            SurveyViewModel.SurveyStep.SOCIAL_DETAILS -> {
                binding.personalDetails.circle.setColorFilter(getColor(R.color.green))
                binding.afterPersonalDetails.setBackgroundColor(getColor(R.color.green))
                binding.technicalDetails.circle.setColorFilter(getColor(R.color.green))
                binding.afterTechnicalDetails.setBackgroundColor(getColor(R.color.green))
                binding.socialDetails.circle.setColorFilter(getColor(R.color.yellow))
                binding.afterSocialDetails.setBackgroundColor(getColor(R.color.edit_text_hint))
            }
            SurveyViewModel.SurveyStep.KYC_DETAILS -> {
                binding.personalDetails.circle.setColorFilter(getColor(R.color.green))
                binding.afterPersonalDetails.setBackgroundColor(getColor(R.color.green))
                binding.technicalDetails.circle.setColorFilter(getColor(R.color.green))
                binding.afterTechnicalDetails.setBackgroundColor(getColor(R.color.green))
                binding.socialDetails.circle.setColorFilter(getColor(R.color.green))
                binding.afterSocialDetails.setBackgroundColor(getColor(R.color.green))
                binding.kycDetails.circle.setColorFilter(getColor(R.color.yellow))
            }
        }
    }
}
