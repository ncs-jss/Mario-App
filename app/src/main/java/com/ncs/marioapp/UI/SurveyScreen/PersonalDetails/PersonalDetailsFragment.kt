package com.ncs.marioapp.UI.SurveyScreen.PersonalDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.ncs.marioapp.Domain.HelperClasses.PrefManager
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.isNull
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.marioapp.Domain.Utility.GlobalUtils
import com.ncs.marioapp.R
import com.ncs.marioapp.UI.SurveyScreen.BottomSheet
import com.ncs.marioapp.UI.SurveyScreen.SurveyViewModel
import com.ncs.marioapp.databinding.FragmentPersonalDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PersonalDetailsFragment : Fragment(), BottomSheet.SendText {

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
                val userSurvey= PrefManager.getUserProfileForCache()!!
                userSurvey.name=surveyViewModel.name.value!!
                userSurvey.admission_number=surveyViewModel.admission_num.value!!
                userSurvey.branch=surveyViewModel.branch.value!!
                userSurvey.year=when(surveyViewModel.year.value!!){
                    "I Year"->1
                    "II Year"->2
                    "III Year"->3
                    "IV Year"->4
                    else->0
                }
                userSurvey.admitted_to=surveyViewModel.admittedTo.value!!
                PrefManager.setUserProfileForCache(userSurvey)
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
        if (PrefManager.getUserProfile()!!.admission_number!=""){
            val editText=binding.admissionNumEt
            editText.isEnabled = false
            editText.isFocusable = false
            editText.isFocusableInTouchMode = false
            editText.setOnClickListener {
                util.showSnackbar(binding.root,"Can't edit admission number now",2000)
            }
            val editText1=binding.collegeOrUniversity
            editText1.isEnabled = false
            editText1.isFocusable = false
            editText1.isFocusableInTouchMode = false
            editText1.setOnClickListener {
                util.showSnackbar(binding.root,"Can't edit your college now",2000)
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
        surveyViewModel.admittedTo.observe(viewLifecycleOwner, Observer {
            if (!it.isNull) {
                binding.collegeOrUniversity.text = it
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
        binding.collegeOrUniversity.setOnClickThrottleBounceListener {
            val list= listOf("COLLEGE","UNIVERSITY")
            var index:Int=-1
            if (binding.collegeOrUniversity.text!="Admitted to"){
                index=list.indexOf(binding.collegeOrUniversity.text.toString())
            }
            val priorityBottomSheet =
                BottomSheet(list, "Admitted to", this,index)
            priorityBottomSheet.show(requireFragmentManager(), "Admitted to")
        }
        binding.btnNext.setOnClickThrottleBounceListener {
            surveyViewModel.name.value = binding.nameEt.text.toString()
            surveyViewModel.admission_num.value = binding.admissionNumEt.text.toString()
            surveyViewModel.branch.value = binding.branchEt.text.toString()
            surveyViewModel.year.value = binding.yearEt.text.toString()
            surveyViewModel.admittedTo.value = binding.collegeOrUniversity.text.toString()
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

    override fun stringtext(text: String, type: String,currentSelected: Int) {
        if (type=="Select Branch"){
            binding.branchEt.text = text
        }
        if (type=="Select Year"){
            binding.yearEt.text = text
        }
        if (type=="Admitted to"){
            binding.collegeOrUniversity.text = text
        }
    }

}