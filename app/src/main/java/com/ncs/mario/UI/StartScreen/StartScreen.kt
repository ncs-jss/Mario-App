package com.ncs.mario.UI.StartScreen

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.ncs.mario.Domain.Utility.ExtensionsUtil
import com.ncs.mario.UI.AuthScreen.AuthActivity
import com.ncs.mario.databinding.ActivityStartScreenBinding
import kotlinx.coroutines.delay


class StartScreen : AppCompatActivity() {
    val binding: ActivityStartScreenBinding by lazy {
        ActivityStartScreenBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ExtensionsUtil.runDelayed(3000){
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }
    }
}