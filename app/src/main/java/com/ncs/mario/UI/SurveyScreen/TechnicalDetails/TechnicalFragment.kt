package com.ncs.mario.UI.SurveyScreen.TechnicalDetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.ncs.mario.Domain.Utility.ExtensionsUtil.isNull
import com.ncs.mario.Domain.Utility.ExtensionsUtil.setOnClickSingleTimeBounceListener
import com.ncs.mario.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
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

    private fun observeData(){
        surveyViewModel.errorMessageTechncialDetails.observe(viewLifecycleOwner, Observer { message ->
            if (message != null) {
                util.showSnackbar(binding.root,message!!,2000)
                surveyViewModel.resetErrorMessageTechnicalDetails()
            }
        })
        surveyViewModel.technicalDetailsPageResult.observe(viewLifecycleOwner, Observer { result ->
            if (result){
                findNavController().navigate(R.id.action_fragment_technical_to_fragment_social_links)
                surveyViewModel.resetTechnicalDetailsPageResult()
                surveyViewModel.resetErrorMessageTechnicalDetails()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        surveyViewModel.setCurrentStep(SurveyViewModel.SurveyStep.TECHNICAL_DETAILS)
        restoreFields()
    }

    private fun restoreFields(){
        surveyViewModel.interestedDomains.observe(viewLifecycleOwner, Observer {
            if (!it.isNull) {
                binding.interestedDomainsEt.setText(it)
            }
        })
        surveyViewModel.knownLanguages.observe(viewLifecycleOwner, Observer {
            if (!it.isNull) {
                binding.knownProgrammingLanguagesEt.setText(it)
            }
        })
        surveyViewModel.userInput.observe(viewLifecycleOwner, Observer {
            if (!it.isNull) {
                binding.customEt.setText(it)
            }
        })
    }


    private fun setUpViews(){
        binding.btnBack.setOnClickSingleTimeBounceListener {
            moveToPrevious()
        }
        binding.btnNext.setOnClickThrottleBounceListener {
            surveyViewModel.interestedDomains.value = binding.interestedDomainsEt.text.toString()
            surveyViewModel.knownLanguages.value = binding.knownProgrammingLanguagesEt.text.toString()
            surveyViewModel.userInput.value = binding.customEt.text.toString()
            surveyViewModel.validateInputsOnTechnicalDetailsPage()
        }
    }

    private fun onBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            moveToPrevious()
        }
    }

    fun moveToPrevious(){
        findNavController().navigate(R.id.action_fragment_technical_to_fragment_personal_details)
    }

}