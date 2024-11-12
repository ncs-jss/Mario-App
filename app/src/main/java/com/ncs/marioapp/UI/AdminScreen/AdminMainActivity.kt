package com.ncs.marioapp.UI.AdminScreen

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
import com.ncs.marioapp.databinding.ActivityAdminMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminMainActivity : AppCompatActivity() {

    val binding: ActivityAdminMainBinding by lazy {
        ActivityAdminMainBinding.inflate(layoutInflater)
    }

    private val viewModel: AdminViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }



}