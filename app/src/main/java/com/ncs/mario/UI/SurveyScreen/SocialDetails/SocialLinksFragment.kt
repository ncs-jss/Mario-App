package com.ncs.mario.UI.SurveyScreen.SocialDetails

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.ncs.mario.Domain.HelperClasses.PrefManager
import com.ncs.mario.Domain.Utility.ExtensionsUtil.gone
import com.ncs.mario.Domain.Utility.ExtensionsUtil.isNull
import com.ncs.mario.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.mario.Domain.Utility.ExtensionsUtil.visible
import com.ncs.mario.Domain.Utility.GlobalUtils
import com.ncs.mario.R
import com.ncs.mario.UI.SurveyScreen.SurveyViewModel
import com.ncs.mario.databinding.FragmentSocialLinksBinding
import com.ncs.mario.databinding.FragmentTechnicalBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SocialLinksFragment : Fragment() {

    lateinit var binding: FragmentSocialLinksBinding
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
        binding = FragmentSocialLinksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
        setUpViews()
    }

    private fun observeData(){
        surveyViewModel.errorMessageSocialDetails.observe(viewLifecycleOwner, Observer { message ->
            if (message != null) {
                util.showSnackbar(binding.root,message!!,2000)
                surveyViewModel.resetErrorMessageSocialDetails()
            }
        })
        surveyViewModel.socialDetailsPageResult.observe(viewLifecycleOwner, Observer { result ->
            if (result){
                val userSurvey= PrefManager.getUserProfileForCache()!!
                val links= listOf(surveyViewModel.linkedIn.value!!,surveyViewModel.github.value!!,surveyViewModel.behance.value!!,surveyViewModel.link1.value!!,surveyViewModel.link2.value!!)
                userSurvey.socials.LinkedIn=links[0]
                userSurvey.socials.GitHub=links[1]
                userSurvey.socials.Behance=links[2]
                PrefManager.setUserProfileForCache(userSurvey)
                findNavController().navigate(R.id.action_fragment_social_links_to_fragment_k_y_c_validation)
                surveyViewModel.resetSocialDetailsPageResult()
                surveyViewModel.resetErrorMessageSocialDetails()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        surveyViewModel.setCurrentStep(SurveyViewModel.SurveyStep.SOCIAL_DETAILS)
        restoreFields()
    }

    private fun restoreFields(){
        surveyViewModel.linksCount.observe(viewLifecycleOwner){
            when(it){
                0->{
                    binding.link1Et.gone()
                    binding.link2Et.gone()
                }
                1->{
                    binding.link1Et.visible()
                    binding.link2Et.gone()
                }
                2->{
                    binding.link1Et.visible()
                    binding.link2Et.visible()
                }
            }
        }
        surveyViewModel.linkedIn.observe(viewLifecycleOwner, Observer {
            if (!it.isNull) {
                binding.linkedInEt.setText(it)
            }
        })
        surveyViewModel.github.observe(viewLifecycleOwner, Observer {
            if (!it.isNull) {
                binding.githubEt.setText(it)
            }
        })
        surveyViewModel.behance.observe(viewLifecycleOwner, Observer {
            if (!it.isNull) {
                binding.behanceEt.setText(it)
            }
        })
        surveyViewModel.link1.observe(viewLifecycleOwner, Observer {
            if (!it.isNull) {
                binding.link1Et.setText(it)
            }
        })
        surveyViewModel.link2.observe(viewLifecycleOwner, Observer {
            if (!it.isNull) {
                binding.link2Et.setText(it)
            }
        })
    }


    private fun setUpViews(){
        binding.btnAddMoreLinks.setOnClickThrottleBounceListener {
            if (surveyViewModel.linksCount.value!! < 2) {
                surveyViewModel.setLinksCount(surveyViewModel.linksCount.value?.plus(1) ?: 1)
            }
        }
        binding.btnBack.setOnClickThrottleBounceListener {
            moveToPrevious()
        }
        binding.btnNext.setOnClickThrottleBounceListener {
            surveyViewModel.linkedIn.value = binding.linkedInEt.text.toString()
            surveyViewModel.github.value = binding.githubEt.text.toString()
            surveyViewModel.behance.value = binding.behanceEt.text.toString()
            surveyViewModel.link1.value = binding.link1Et.text.toString()
            surveyViewModel.link2.value = binding.link2Et.text.toString()
            surveyViewModel.validateInputsOnSocialDetailsPage()
        }
    }

    private fun onBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            moveToPrevious()
        }
    }

    fun moveToPrevious(){
        findNavController().navigate(R.id.action_fragment_social_links_to_fragment_technical)
    }

}