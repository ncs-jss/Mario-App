package com.ncs.marioapp.UI.SettingsScreen.EditProfile.TechnicalDetails

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.ncs.marioapp.Domain.HelperClasses.PrefManager
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.marioapp.Domain.Utility.GlobalUtils
import com.ncs.marioapp.R
import com.ncs.marioapp.UI.SettingsScreen.EditProfile.EditProfileViewModel
import com.ncs.marioapp.databinding.FragmentTechnicalBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TechnicalFragmentEditProfile : Fragment() {

    lateinit var binding: FragmentTechnicalBinding
    private val surveyViewModel: EditProfileViewModel by activityViewModels()
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
                val userSurvey=PrefManager.getUserSurvey()!!
                val domains=surveyViewModel.selectedDomains.value!!.toMutableList()
                if (domains.contains("Others")){
                    domains.remove("Others")
                    domains.add(surveyViewModel.getOthersText()!!)
                }
                userSurvey.domains=domains
                PrefManager.setUserSurvey(userSurvey)
                Log.d("usercheck","${PrefManager.getUserSurvey()}")
                findNavController().navigate(R.id.action_fragment_technical_ep_to_fragment_social_links_ep)
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
        surveyViewModel.setCurrentStep(EditProfileViewModel.SurveyStep.TECHNICAL_DETAILS)
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
        findNavController().navigate(R.id.action_fragment_technical_ep_to_fragment_personal_details_ep)
    }
}
