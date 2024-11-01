package com.ncs.mario.UI.AdminScreen

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.ncs.mario.Domain.Models.Admin.GiftCoinsPostBody
import com.ncs.mario.Domain.Models.Events.ScanTicketBody
import com.ncs.mario.Domain.Models.ServerResult
import com.ncs.mario.Domain.Utility.ExtensionsUtil.gone
import com.ncs.mario.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.mario.Domain.Utility.ExtensionsUtil.showProgressDialog
import com.ncs.mario.Domain.Utility.ExtensionsUtil.visible
import com.ncs.mario.Domain.Utility.GlobalUtils
import com.ncs.mario.R
import com.ncs.mario.UI.MainScreen.MainActivity
import com.ncs.mario.UI.MainScreen.MainViewModel
import com.ncs.mario.databinding.ActivityAdminMainBinding
import com.ncs.mario.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminMainActivity : AppCompatActivity() {

    val binding: ActivityAdminMainBinding by lazy {
        ActivityAdminMainBinding.inflate(layoutInflater)
    }

    private val viewModel: AdminViewModel by viewModels()
    lateinit var dialog: Dialog
    private val util: GlobalUtils.EasyElements by lazy {
        GlobalUtils.EasyElements(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        bindObeservers()
        setUpViews()
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
        binding.btnBackToMario.setOnClickThrottleBounceListener {
            startActivity(Intent(this@AdminMainActivity, MainActivity::class.java))
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
        }
    }

    private fun bindObeservers() {

        viewModel.errorMessage.observe(this) {
            showError(it)
            binding.progressBar.gone()
        }

        viewModel.progressState.observe(this) {
            if (it) {
                binding.progressBar.visible()
            } else {
                binding.progressBar.gone()
            }
        }

        viewModel.validateScannedQR.observe(this){result ->
            when(result){
                is ServerResult.Failure ->{
                    binding.progressBar.gone()
                    showError(result.exception.message)
                }
                ServerResult.Progress -> {
                    binding.progressBar.visible()
                }
                is ServerResult.Success ->{
                    binding.progressBar.gone()
                    Toast.makeText(this,result.data,Toast.LENGTH_SHORT).show()
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
            Toast.makeText(this, "Scan Cancelled", Toast.LENGTH_SHORT).show()
        } else {
            viewModel.scanTicket(ScanTicketBody(result.contents))
        }
    }

    private val validateQRScannerLauncher = registerForActivityResult<ScanOptions, ScanIntentResult>(
        ScanContract()
    ) { result ->

        if (result.contents == null) {
            Toast.makeText(this, "Scan Cancelled", Toast.LENGTH_SHORT).show()
        } else {
            viewModel.validateScannedQR(result.contents)
        }
    }

    private val giftCoinsScannerLauncher = registerForActivityResult<ScanOptions, ScanIntentResult>(
        ScanContract()
    ) { result ->

        if (result.contents == null) {
            Toast.makeText(this, "Scan Cancelled", Toast.LENGTH_SHORT).show()
        } else {
            val bottomSheet=GiftCoinsBottomSheet(result.contents)
            bottomSheet.show(supportFragmentManager,"GiftCoinsBottomSheet")
        }
    }



}