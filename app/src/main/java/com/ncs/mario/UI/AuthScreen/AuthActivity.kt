package com.ncs.mario.UI.AuthScreen

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ncs.mario.R
import com.ncs.mario.UI.AuthScreen.ForgotPassword.ForgotPasswordViewModel
import com.ncs.mario.UI.SurveyScreen.SurveyViewModel
import com.ncs.mario.databinding.ActivityAuthBinding
import com.ncs.mario.databinding.ActivityStartScreenBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {
    val binding: ActivityAuthBinding by lazy {
        ActivityAuthBinding.inflate(layoutInflater)
    }
    private val viewModel: ForgotPasswordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}