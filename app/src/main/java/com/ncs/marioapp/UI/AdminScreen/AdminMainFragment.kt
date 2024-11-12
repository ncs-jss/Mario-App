package com.ncs.marioapp.UI.AdminScreen

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.ncs.marioapp.Domain.Models.Events.ScanTicketBody
import com.ncs.marioapp.Domain.Models.ServerResult
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.gone
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.visible
import com.ncs.marioapp.Domain.Utility.GlobalUtils
import com.ncs.marioapp.R
import com.ncs.marioapp.UI.MainScreen.MainActivity
import com.ncs.marioapp.databinding.FragmentAdminMainBinding
import dagger.hilt.android.AndroidEntryPoint
import me.shouheng.utils.app.ActivityUtils.overridePendingTransition

@AndroidEntryPoint
class AdminMainFragment : Fragment() {

    private var _binding: FragmentAdminMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AdminViewModel by viewModels()
    lateinit var dialog: Dialog
    private val util: GlobalUtils.EasyElements by lazy {
        GlobalUtils.EasyElements(requireContext())
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminMainBinding.inflate(inflater, container, false)
        bindObeservers()
        setUpViews()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun setUpViews(){
        binding.btnMarkAttendance.setOnClickThrottleBounceListener{
            ticketScannerLauncher.launch(
                ScanOptions().setPrompt("Scan Ticket QR to mark attendance").setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            )
        }
        binding.btnAuthorizeQR.setOnClickThrottleBounceListener {
            validateQRScannerLauncher.launch(
                ScanOptions().setPrompt("Scan sticker QR to validate").setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            )
        }
        binding.btnGiftCoins.setOnClickThrottleBounceListener {
            giftCoinsScannerLauncher.launch(
                ScanOptions().setPrompt("Scan profile QR to gift coins").setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            )
        }
        binding.btnCreateStory.setOnClickThrottleBounceListener {
            findNavController().navigate(R.id.action_adminMainFragment_to_storyFragment)
        }
        binding.btnBackToMario.setOnClickThrottleBounceListener {
            startActivity(Intent(requireActivity(), MainActivity::class.java))

        }


    }


    private fun bindObeservers() {

        viewModel.errorMessage.observe(requireActivity()) {
            showError(it)
            binding.progressBar.gone()
        }

        viewModel.progressState.observe(requireActivity()) {
            if (it) {
                binding.progressBar.visible()
            } else {
                binding.progressBar.gone()
            }
        }

        viewModel.validateScannedQR.observe(requireActivity()){result ->
            when(result){
                is ServerResult.Failure ->{
                    binding.progressBar.gone()
                    showError(result.message)
                }
                ServerResult.Progress -> {
                    binding.progressBar.visible()
                }
                is ServerResult.Success ->{
                    binding.progressBar.gone()
                    Toast.makeText(requireContext(),result.data, Toast.LENGTH_SHORT).show()
                }
            }
        }

    }


    private fun showError(message: String?) {
        util.showSnackbar(binding.root,message!!,2000)
    }

    private val ticketScannerLauncher = registerForActivityResult<ScanOptions, ScanIntentResult>(
        ScanContract()
    ) { result ->

        if (result.contents == null) {
            Toast.makeText(requireContext(), "Scan Cancelled", Toast.LENGTH_SHORT).show()
        } else {
            viewModel.scanTicket(ScanTicketBody(result.contents))
        }
    }

    private val validateQRScannerLauncher = registerForActivityResult<ScanOptions, ScanIntentResult>(
        ScanContract()
    ) { result ->

        if (result.contents == null) {
            Toast.makeText(requireContext(), "Scan Cancelled", Toast.LENGTH_SHORT).show()
        } else {
            viewModel.validateScannedQR(result.contents)
        }
    }

    private val giftCoinsScannerLauncher = registerForActivityResult<ScanOptions, ScanIntentResult>(
        ScanContract()
    ) { result ->

        if (result.contents == null) {
            Toast.makeText(requireContext(), "Scan Cancelled", Toast.LENGTH_SHORT).show()
        } else {
            val bottomSheet=GiftCoinsBottomSheet(result.contents)
            bottomSheet.show(parentFragmentManager,"GiftCoinsBottomSheet")
        }
    }
}