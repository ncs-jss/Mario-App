package com.ncs.marioapp.UI.AuthScreen.OTPVerified

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import com.ncs.marioapp.Domain.Utility.GlobalUtils
import com.ncs.marioapp.UI.SurveyScreen.SurveyActivity
import com.ncs.marioapp.databinding.FragmentOTPVerifiedBinding

class OTPVerifiedFragment : Fragment() {

    lateinit var binding: FragmentOTPVerifiedBinding
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
        binding = FragmentOTPVerifiedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
    }

    private fun setUpViews(){

        binding.btnContinue.setOnClickListener {
            startActivity(Intent(requireContext(),SurveyActivity::class.java))
            requireActivity().finish()
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