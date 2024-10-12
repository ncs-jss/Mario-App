package com.ncs.mario.UI.AuthScreen.ForgotPassword

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.ncs.mario.Domain.Utility.ExtensionsUtil.gone
import com.ncs.mario.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.mario.Domain.Utility.ExtensionsUtil.visible
import com.ncs.mario.Domain.Utility.GlobalUtils
import com.ncs.mario.R
import com.ncs.mario.databinding.FragmentForgotPasswordBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {

    lateinit var binding: FragmentForgotPasswordBinding
    private val viewModel: ForgotPasswordViewModel by activityViewModels()
    private val util: GlobalUtils.EasyElements by lazy {
        GlobalUtils.EasyElements(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
        setUpViews()
    }

    private fun setUpViews(){
        binding.btnToLogin.setOnClickThrottleBounceListener {
            findNavController().navigate(R.id.action_fragment_forgot_password_to_fragment_login)
        }
        binding.emailEt.addTextChangedListener {
            viewModel.email.value = it.toString()
        }
        binding.btnContinue.setOnClickThrottleBounceListener {
            closeKeyboard()
            viewModel.validateEmail()
        }
    }

    private fun observeData(){

        viewModel.forgotPassFragmentResult.observe(viewLifecycleOwner, Observer {
            if (it){
                findNavController().navigate(R.id.action_fragment_forgot_password_to_fragment_o_t_p)
            }
        })

        viewModel.progressStateForgotPassFragment.observe(viewLifecycleOwner, Observer { isLoading ->
            if (isLoading) {
                binding.progressBar.visible()
                binding.btnContinue.text="Hold on..."
                binding.btnContinue.isEnabled=false
            } else {
                binding.progressBar.gone()
                binding.btnContinue.text="Continue"
                binding.btnContinue.isEnabled=true
            }
        })

        viewModel.errorMessageForgotPassFragment.observe(viewLifecycleOwner, Observer { message ->
            util.showSnackbar(binding.root,message!!,2000)
        })
    }

    fun closeKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = requireActivity().currentFocus ?: View(requireContext())
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        onBackPress()
    }

    private fun onBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_fragment_forgot_password_to_fragment_login)
        }
    }

}