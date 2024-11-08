package com.ncs.marioapp.UI.AuthScreen.ForgotPassword

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.gone
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.visible
import com.ncs.marioapp.Domain.Utility.GlobalUtils
import com.ncs.marioapp.R
import com.ncs.marioapp.databinding.FragmentSetPasswordBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetPasswordFragment : Fragment() {

    lateinit var binding: FragmentSetPasswordBinding
    private val viewModel: ForgotPasswordViewModel by activityViewModels()
    private val util: GlobalUtils.EasyElements by lazy {
        GlobalUtils.EasyElements(requireActivity())
    }
    private var backPressedTime: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSetPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        observeViewModel()
    }

    private fun setUpViews(){
        binding.passwordEt.addTextChangedListener {
            viewModel.password.value = it.toString()
        }
        binding.confirmPasswordEt.addTextChangedListener {
            viewModel.confirmPassword.value = it.toString()
        }
        binding.btnContinue.setOnClickThrottleBounceListener {
            closeKeyboard()
            viewModel.validateAndSetPassword()
        }
    }

    fun closeKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = requireActivity().currentFocus ?: View(requireContext())
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }


    private fun observeViewModel() {

        viewModel.progressStateSetPassFragment.observe(viewLifecycleOwner, Observer { isLoading ->
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

        viewModel.errorMessageSetPassFragment.observe(viewLifecycleOwner, Observer { message ->
            util.showSnackbar(binding.root,message!!,2000)
        })

        viewModel.setPassFragmentResult.observe(viewLifecycleOwner, Observer { isSuccess ->
            if (isSuccess) {
                findNavController().navigate(R.id.action_fragment_set_password_to_fragment_login)
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        disableBackPress()
    }

    private fun disableBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner){
            util.showSnackbar(binding.root,"Invalid! Can't go back",2000)
        }
    }

}