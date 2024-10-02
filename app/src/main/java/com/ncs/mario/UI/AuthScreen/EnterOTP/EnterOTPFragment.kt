package com.ncs.mario.UI.AuthScreen.EnterOTP


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.ncs.mario.Domain.Utility.ExtensionsUtil.gone
import com.ncs.mario.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.mario.Domain.Utility.ExtensionsUtil.visible
import com.ncs.mario.Domain.Utility.GlobalUtils
import com.ncs.mario.R
import com.ncs.mario.databinding.FragmentEnterOTPBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EnterOTPFragment : Fragment() {

    private lateinit var binding: FragmentEnterOTPBinding
    private val viewModel: EnterOTPViewModel by viewModels()
    private val util: GlobalUtils.EasyElements by lazy {
        GlobalUtils.EasyElements(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEnterOTPBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        setUpObservers()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        disableBackPress()
    }

    private fun setUpViews() {
        val otpEditTexts = listOf(binding.otpEt1, binding.otpEt2, binding.otpEt3, binding.otpEt4, binding.otpEt5, binding.otpEt6)

        otpEditTexts.forEachIndexed { index, editText ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.length == 1 && index < otpEditTexts.size - 1) {
                        otpEditTexts[index + 1].requestFocus()
                    } else if (index == otpEditTexts.size - 1) {
                        hideKeyboard()
                    }
                }
                override fun afterTextChanged(s: Editable?) {}
            })

            editText.setOnKeyListener { v, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                    if (editText.text.isEmpty() && index > 0) {
                        otpEditTexts[index - 1].text.clear()
                        otpEditTexts[index - 1].requestFocus()
                    }
                }
                false
            }
        }

        binding.btnContinue.setOnClickThrottleBounceListener {
            val otp = "${binding.otpEt1.text.trim()}${binding.otpEt2.text.trim()}${binding.otpEt3.text.trim()}${binding.otpEt4.text.trim()}${binding.otpEt5.text.trim()}${binding.otpEt6.text.trim()}"
            viewModel.validateAndPerformOTPVerification(otp)
        }

        binding.btnResendOTP.setOnClickThrottleBounceListener {
            viewModel.startTimer()
            viewModel.setIsResendVisible(false)
            clearOTPFields()
        }
    }


    private fun setUpObservers() {
        viewModel.isResendVisible.observe(viewLifecycleOwner, Observer {
            binding.otpTimer.visibility = if (it) View.GONE else View.VISIBLE
            binding.btnResendOTP.visibility = if (it) View.VISIBLE else View.GONE
        })
        viewModel.timerText.observe(viewLifecycleOwner, Observer {
            binding.otpTimer.text = it
        })
        viewModel.errorMessage.observe(viewLifecycleOwner, Observer { error ->
            error?.let {
                util.showSnackbar(binding.root, error, 2000)
                clearOTPFields()
            }
        })

        viewModel.otpResult.observe(viewLifecycleOwner, Observer { result ->
            if (result) {
                findNavController().navigate(R.id.action_fragment_enter_o_t_p_to_fragment_o_t_p_verified)
            }
        })
    }

    private fun clearOTPFields() {
        binding.otpEt1.text.clear()
        binding.otpEt2.text.clear()
        binding.otpEt3.text.clear()
        binding.otpEt4.text.clear()
        binding.otpEt5.text.clear()
        binding.otpEt6.text.clear()

        binding.otpEt1.requestFocus()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(InputMethodManager::class.java)
        imm?.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    private fun disableBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner){
            util.showSnackbar(binding.root,"Invalid! Can't go back",2000)
        }
    }
}
