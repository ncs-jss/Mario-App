package com.ncs.marioapp.UI.UpdateScreen

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ncs.marioapp.Domain.Models.Events.Event
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.marioapp.Domain.Utility.GlobalUtils
import com.ncs.marioapp.R
import com.ncs.marioapp.UI.EventDetailsScreen.EventDetailsViewModel
import com.ncs.marioapp.databinding.ActivityEventDetailsBinding
import com.ncs.marioapp.databinding.ActivityUpdateBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdateActivity : AppCompatActivity() {

    val binding: ActivityUpdateBinding by lazy {
        ActivityUpdateBinding.inflate(layoutInflater)
    }

    private var backPressedTime: Long = 0

    private val util: GlobalUtils.EasyElements by lazy {
        GlobalUtils.EasyElements(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnDownload.setOnClickThrottleBounceListener{
            openUrl("https://play.google.com/store/apps/details?id=com.ncs.marioapp")
        }
    }


    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    override fun onBackPressed() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - backPressedTime < 2000) {
            super.onBackPressed()
            finish()
        } else {
            util.showSnackbar(binding.root, "Press back again to exit", 2000)
            backPressedTime = currentTime
        }
    }


}