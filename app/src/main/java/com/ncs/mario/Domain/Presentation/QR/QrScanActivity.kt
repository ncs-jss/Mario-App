package com.ncs.mario.Domain.Presentation.QR

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ncs.mario.Domain.Presentation.QR.ui.qrscan.QRScanFragment
import com.ncs.mario.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QrScanActivity : AppCompatActivity() {

    private val binding:ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}