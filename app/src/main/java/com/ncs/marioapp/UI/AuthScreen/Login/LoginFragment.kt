package com.ncs.marioapp.UI.AuthScreen.Login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.ncs.marioapp.Domain.HelperClasses.PrefManager
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.gone
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.visible
import com.ncs.marioapp.Domain.Utility.GlobalUtils
import com.ncs.marioapp.R
import com.ncs.marioapp.UI.StartScreen.StartScreen
import com.ncs.marioapp.UI.SurveyScreen.SurveyActivity
import com.ncs.marioapp.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private val util: GlobalUtils.EasyElements by lazy {
        GlobalUtils.EasyElements(requireActivity())
    }
    private var backPressedTime: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
        setUpViews()
    }

    private fun setUpViews(){

        binding.forgotPassword.setOnClickThrottleBounceListener {
            findNavController().navigate(R.id.action_fragment_login_to_fragment_forgot_password)
        }

        binding.btntosignUp.setOnClickThrottleBounceListener {
            findNavController().navigate(R.id.action_fragment_login_to_fragment_sign_up)
        }
        binding.emailEt.addTextChangedListener {
            viewModel.email.value = it.toString()
        }
        binding.passwordEt.addTextChangedListener {
            viewModel.password.value = it.toString()
        }
        binding.btnContinue.setOnClickThrottleBounceListener {
            closeKeyboard()
            viewModel.login()
        }
    }

    private fun observeData(){

        viewModel.loginResult.observe(viewLifecycleOwner, Observer {
            if (it){
                PrefManager.setLoginStatus(true)
                startActivity(Intent(requireContext(), StartScreen::class.java))
                requireActivity().finish()
            }
        })

        viewModel.progressState.observe(viewLifecycleOwner, Observer { isLoading ->
            if (isLoading) {
                binding.progressBar.visible()
                binding.btnContinue.text="Hold on..."
                binding.btnContinue.isEnabled=false
            } else {
                binding.progressBar.gone()
                binding.btnContinue.text="Login"
                binding.btnContinue.isEnabled=true
            }
        })

        viewModel.errorMessage.observe(viewLifecycleOwner, Observer { message ->
            if (message=="User not found!"){
                PrefManager.setShowProfileCompletionAlert(true)
                startActivity(Intent(requireContext(), SurveyActivity::class.java))
                requireActivity().finish()
            }
            else{
                util.showSnackbar(binding.root,message!!,2000)
            }
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
            if (backPressedTime + 2000 > System.currentTimeMillis()) {
                requireActivity().finish()
            } else {
                util.showSnackbar(binding.root,"Press back again to exit",2000)
            }
            backPressedTime = System.currentTimeMillis()
        }
    }


}