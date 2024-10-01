package com.ncs.mario.Domain.Presentation.QR

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.ncs.mario.Domain.Presentation.QR.ui.qrscan.QRScanFragment
import com.ncs.mario.R
import com.ncs.mario.databinding.ActivityMainBinding
import com.ncs.mario.databinding.ActivityQRScanBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QrScanActivity : AppCompatActivity() {

    private val binding:ActivityQRScanBinding by lazy {
        ActivityQRScanBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        loadFragment(QRScanFragment())
    }
    override fun onBackPressed() {

        super.onBackPressed()
    }
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }
}