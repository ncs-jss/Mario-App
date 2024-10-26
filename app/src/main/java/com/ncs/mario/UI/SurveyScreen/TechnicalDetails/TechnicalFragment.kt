package com.ncs.mario.UI.SurveyScreen.TechnicalDetails

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.ncs.mario.Domain.HelperClasses.PrefManager
import com.ncs.mario.Domain.Utility.ExtensionsUtil.isNull
import com.ncs.mario.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.mario.Domain.Utility.ExtensionsUtil.slideDownAndVisible
import com.ncs.mario.Domain.Utility.ExtensionsUtil.slideUpAndGone
import com.ncs.mario.Domain.Utility.GlobalUtils
import com.ncs.mario.R
import com.ncs.mario.UI.SurveyScreen.SurveyViewModel
import com.ncs.mario.databinding.FragmentPersonalDetailsBinding
import com.ncs.mario.databinding.FragmentTechnicalBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TechnicalFragment : Fragment() {

    lateinit var binding: FragmentTechnicalBinding
    private val surveyViewModel: SurveyViewModel by activityViewModels()
    private val util: GlobalUtils.EasyElements by lazy {
        GlobalUtils.EasyElements(requireActivity())
    }

    private var backPressedTime: Long = 0

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        onBackPress()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTechnicalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
        setUpViews()
    }

    private fun observeData() {
        surveyViewModel.errorMessageTechncialDetails.observe(viewLifecycleOwner, Observer { message ->
            if (message != null) {
                util.showSnackbar(binding.root, message, 2000)
                surveyViewModel.resetErrorMessageTechnicalDetails()
            }
        })
        surveyViewModel.technicalDetailsPageResult.observe(viewLifecycleOwner, Observer { result ->
            if (result) {
                val userSurvey=PrefManager.getUserProfileForCache()!!
                val domains=surveyViewModel.selectedDomains.value!!.toMutableList()
                if (domains.contains("Others")){
                    domains.remove("Others")
                    domains.add("Other")
                }
                userSurvey.domain=domains
                userSurvey.other_domain=surveyViewModel.getOthersText()!!
                PrefManager.setUserProfileForCache(userSurvey)
                findNavController().navigate(R.id.action_fragment_technical_to_fragment_social_links)
                surveyViewModel.resetTechnicalDetailsPageResult()
                surveyViewModel.resetErrorMessageTechnicalDetails()
            }
        })

        surveyViewModel.selectedDomains.observe(viewLifecycleOwner, Observer { domains ->
            restoreFields(domains)
        })
    }

    override fun onResume() {
        super.onResume()
        surveyViewModel.setCurrentStep(SurveyViewModel.SurveyStep.TECHNICAL_DETAILS)
        restoreFields(surveyViewModel.selectedDomains.value ?: listOf())
    }

    private fun setUpViews() {
        binding.btnBack.setOnClickThrottleBounceListener {
            moveToPrevious()
        }
        binding.btnNext.setOnClickThrottleBounceListener {
            val othersText = binding.specifyEt.text?.toString()?.trim()
            surveyViewModel.setOthersText(othersText)
            surveyViewModel.validateInputsOnTechnicalDetailsPage()
        }
        setupCheckboxListeners()
    }

    private fun setupCheckboxListeners() {
        val checkboxes = listOf(
            binding.checkboxProgramming,
            binding.checkboxDesign,
            binding.checkboxDevelopment,
            binding.checkboxAndroid,
            binding.checkboxdevops,
            binding.checkboxaiml,
            binding.checkboxothers
        )

        for (checkbox in checkboxes) {
            checkbox.setOnClickListener {
                val selectedCount = checkboxes.count { it.isChecked }
                if (selectedCount > 3) {
                    util.showSnackbar(binding.root, "You can select up to 3 domains", 2000)
                    checkbox.isChecked = false
                } else {
                    updateDomains(checkbox)
                }
            }
        }

        binding.checkboxothers.setOnCheckedChangeListener { _, isChecked ->
            binding.specifyEt.visibility = if (isChecked) View.VISIBLE else View.GONE
            if (!isChecked) {
                binding.specifyEt.text?.clear()
            }
        }
    }

    private fun updateDomains(checkbox: CheckBox) {
        val domain = when (checkbox.id) {
            R.id.checkboxProgramming -> "Programming"
            R.id.checkboxDesign -> "Design"
            R.id.checkboxDevelopment -> "Development"
            R.id.checkboxAndroid -> "Android"
            R.id.checkboxdevops -> "DevOps"
            R.id.checkboxaiml -> "AI/ML"
            R.id.checkboxothers -> "Others"
            else -> return
        }

        if (checkbox.isChecked) {
            surveyViewModel.addDomain(domain)
        } else {
            surveyViewModel.removeDomain(domain)
        }
    }

    private fun restoreFields(selectedDomains: List<String>) {
        binding.checkboxProgramming.isChecked = selectedDomains.contains("Programming")
        binding.checkboxDesign.isChecked = selectedDomains.contains("Design")
        binding.checkboxDevelopment.isChecked = selectedDomains.contains("Development")
        binding.checkboxAndroid.isChecked = selectedDomains.contains("Android")
        binding.checkboxdevops.isChecked = selectedDomains.contains("DevOps")
        binding.checkboxaiml.isChecked = selectedDomains.contains("AI/ML")
        binding.checkboxothers.isChecked = selectedDomains.contains("Others")
        binding.specifyEt.setText(surveyViewModel.getOthersText())

        binding.specifyEt.visibility = if (binding.checkboxothers.isChecked) View.VISIBLE else View.GONE
    }

    private fun onBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            moveToPrevious()
        }
    }

    private fun moveToPrevious() {
        findNavController().navigate(R.id.action_fragment_technical_to_fragment_personal_details)
    }
}
