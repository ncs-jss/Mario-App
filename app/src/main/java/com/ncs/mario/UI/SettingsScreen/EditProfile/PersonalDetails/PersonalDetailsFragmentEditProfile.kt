package com.ncs.mario.UI.SettingsScreen.EditProfile.PersonalDetails

import android.content.Intent
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
import com.ncs.mario.Domain.Utility.ExtensionsUtil.isNull
import com.ncs.mario.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.mario.Domain.Utility.GlobalUtils
import com.ncs.mario.R
import com.ncs.mario.UI.SettingsScreen.EditProfile.EditProfileViewModel
import com.ncs.mario.UI.SettingsScreen.SettingsActivity
import com.ncs.mario.UI.SurveyScreen.BottomSheet
import com.ncs.mario.databinding.FragmentPersonalDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PersonalDetailsFragmentEditProfile : Fragment(), BottomSheet.SendText {

    lateinit var binding: FragmentPersonalDetailsBinding
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
                val userSurvey= PrefManager.getUserSurvey()!!
                userSurvey.name=surveyViewModel.name.value!!
                userSurvey.admissionNum=surveyViewModel.admission_num.value!!
                userSurvey.branch=surveyViewModel.branch.value!!
                userSurvey.year=surveyViewModel.year.value!!
                PrefManager.setUserSurvey(userSurvey)
                Log.d("usercheck","${PrefManager.getUserSurvey()}")
                findNavController().navigate(R.id.action_fragment_personal_details_ep_to_fragment_technical_ep)
                surveyViewModel.resetPersonalDetailsPageResult()
                surveyViewModel.resetErrorMessagePersonalDetails()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        surveyViewModel.setCurrentStep(EditProfileViewModel.SurveyStep.PERSONAL_DETAILS)
        restoreFields()
        if (PrefManager.getUserProfile()!!.admission_number!=""){
            val editText=binding.admissionNumEt
            editText.isEnabled = false
            editText.isFocusable = false
            editText.isFocusableInTouchMode = false
            editText.setOnClickListener {
                util.showSnackbar(binding.root,"Can't edit admission number now",2000)
            }
        }
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
        surveyViewModel.branch.observe(viewLifecycleOwner, Observer {
            if (!it.isNull) {
                binding.branchEt.text = it
            }
        })
        surveyViewModel.year.observe(viewLifecycleOwner, Observer {
            if (!it.isNull) {
                binding.yearEt.text = it
            }
        })
    }


    private fun setUpViews(){
        binding.branchEt.setOnClickThrottleBounceListener {
            val list= listOf("CSE","CSE-AIML","CSDS","ECE","EEE","EE","IT","ME","CE")
            var index:Int=-1
            if (binding.branchEt.text!="Branch"){
                index=list.indexOf(binding.branchEt.text.toString())
            }
            val priorityBottomSheet =
                BottomSheet(list, "Select Branch", this,index)
            priorityBottomSheet.show(requireFragmentManager(), "Select Branch")
        }
        binding.yearEt.setOnClickThrottleBounceListener {
            val list= listOf("I Year","II Year","III Year","IV Year")
            var index:Int=-1
            if (binding.yearEt.text!="Year"){
                index=list.indexOf(binding.yearEt.text.toString())
            }
            val priorityBottomSheet =
                BottomSheet(list, "Select Year", this,index)
            priorityBottomSheet.show(requireFragmentManager(), "Select Year")
        }
        binding.btnNext.setOnClickThrottleBounceListener {
            surveyViewModel.name.value = binding.nameEt.text.toString()
            surveyViewModel.admission_num.value = binding.admissionNumEt.text.toString()
            surveyViewModel.branch.value = binding.branchEt.text.toString()
            surveyViewModel.year.value = binding.yearEt.text.toString()
            surveyViewModel.validateInputsOnPersonalDetailsPage()
        }
    }

    private fun onBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finish()
            requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
        }
    }

    override fun stringtext(text: String, type: String,currentSelected: Int) {
        if (type=="Select Branch"){
            binding.branchEt.text = text
        }
        if (type=="Select Year"){
            binding.yearEt.text = text
        }
    }

}