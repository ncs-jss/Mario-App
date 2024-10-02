package com.ncs.mario.UI.AuthScreen.SignUp

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.ncs.mario.Domain.Utility.ExtensionsUtil.setOnClickSingleTimeBounceListener
import com.ncs.mario.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.mario.Domain.Utility.GlobalUtils
import com.ncs.mario.R
import com.ncs.mario.UI.AuthScreen.Login.LoginViewModel
import com.ncs.mario.databinding.FragmentSignUpBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    lateinit var binding: FragmentSignUpBinding
    private val viewModel: SignUpViewModel by viewModels()
    private val util: GlobalUtils.EasyElements by lazy {
        GlobalUtils.EasyElements(requireActivity())
    }
    private var backPressedTime: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        observeViewModel()
    }

    private fun setUpViews(){
        binding.btnToLogin.setOnClickThrottleBounceListener {
            findNavController().navigate(R.id.action_fragment_sign_up_to_fragment_login)
        }
        binding.emailEt.addTextChangedListener {
            viewModel.email.value = it.toString()
        }
        binding.passwordEt.addTextChangedListener {
            viewModel.password.value = it.toString()
        }
        binding.confirmPasswordEt.addTextChangedListener {
            viewModel.confirmPassword.value = it.toString()
        }
        binding.btnContinue.setOnClickThrottleBounceListener {
            closeKeyboard()
            viewModel.validateAndSignup()
        }
    }

    fun closeKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = requireActivity().currentFocus ?: View(requireContext())
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }


    private fun observeViewModel() {

        viewModel.errorMessage.observe(viewLifecycleOwner, Observer { message ->
            util.showSnackbar(binding.root,message!!,2000)
        })

        viewModel.signupResult.observe(viewLifecycleOwner, Observer { isSuccess ->
            if (isSuccess) {
                findNavController().navigate(R.id.action_fragment_sign_up_to_fragment_enter_o_t_p)
            } else {
                util.showSnackbar(binding.root, "Sign up failed", 2000)
            }
        })
    }

}