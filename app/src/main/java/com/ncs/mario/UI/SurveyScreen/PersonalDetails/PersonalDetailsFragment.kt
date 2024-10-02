package com.ncs.mario.UI.SurveyScreen.PersonalDetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.widget.addTextChangedListener
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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PersonalDetailsFragment : Fragment() {

    lateinit var binding: FragmentPersonalDetailsBinding
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
        binding = FragmentPersonalDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
        setUpViews()
    }

    private fun observeData(){
        surveyViewModel.errorMessagePersonalDetails.observe(viewLifecycleOwner, Observer { message ->
            if (message != null) {
                util.showSnackbar(binding.root,message!!,2000)
                surveyViewModel.resetErrorMessagePersonalDetails()
            }
        })
        surveyViewModel.personalDetailsPageResult.observe(viewLifecycleOwner, Observer { result ->
            if (result){
                findNavController().navigate(R.id.action_fragment_personal_details_to_fragment_technical)
                surveyViewModel.resetPersonalDetailsPageResult()
                surveyViewModel.resetErrorMessagePersonalDetails()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        surveyViewModel.setCurrentStep(SurveyViewModel.SurveyStep.PERSONAL_DETAILS)
        restoreFields()
    }

    private fun restoreFields(){
        surveyViewModel.name.observe(viewLifecycleOwner, Observer {
            if (!it.isNull) {
                binding.nameEt.setText(it)
            }
        })
        surveyViewModel.admission_num.observe(viewLifecycleOwner, Observer {
            if (!it.isNull) {
                binding.admissionNumEt.setText(it)
            }
        })
        surveyViewModel.phone_num.observe(viewLifecycleOwner, Observer {
            if (!it.isNull) {
                binding.phoneNumEt.setText(it)
            }
        })
        surveyViewModel.bio.observe(viewLifecycleOwner, Observer {
            if (!it.isNull) {
                binding.bioEt.setText(it)
            }
        })
    }


    private fun setUpViews(){
        binding.btnNext.setOnClickThrottleBounceListener {
            surveyViewModel.name.value = binding.nameEt.text.toString()
            surveyViewModel.admission_num.value = binding.admissionNumEt.text.toString()
            surveyViewModel.phone_num.value = binding.phoneNumEt.text.toString()
            surveyViewModel.bio.value = binding.bioEt.text.toString()
            surveyViewModel.validateInputsOnPersonalDetailsPage()
        }
    }

    private fun onBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (backPressedTime + 2000 > System.currentTimeMillis()) {
                requireActivity().finish()
            } else {
                util.showSnackbar(binding.root,"Press back again to exit",2000)
            }
            backPressedTime = System.currentTimeMillis()
        }
    }

}