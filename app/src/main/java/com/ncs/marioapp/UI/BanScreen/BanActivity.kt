package com.ncs.marioapp.UI.BanScreen

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ncs.marioapp.Domain.HelperClasses.PrefManager
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.marioapp.UI.StartScreen.StartScreen
import com.ncs.marioapp.databinding.ActivityBanBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BanActivity : AppCompatActivity() {

    private val binding: ActivityBanBinding by lazy {
        ActivityBanBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
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