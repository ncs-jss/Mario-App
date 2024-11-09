package com.ncs.mario.UI.BanScreen

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import com.ncs.mario.Domain.HelperClasses.PrefManager
import com.ncs.mario.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.mario.UI.StartScreen.StartScreen
import com.ncs.mario.databinding.ActivityBanBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BanActivity : AppCompatActivity() {

    private val binding: ActivityBanBinding by lazy {
        ActivityBanBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setUpViews()
    }

    fun setUpViews(){
        binding.logout.setOnClickThrottleBounceListener{
            PrefManager.clearPrefs()
            startActivity(Intent(this, StartScreen::class.java))
            finish()
        }
    }

}