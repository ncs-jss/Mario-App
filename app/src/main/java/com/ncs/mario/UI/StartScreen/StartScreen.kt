package com.ncs.mario.UI.StartScreen

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ncs.mario.Domain.HelperClasses.PrefManager
import com.ncs.mario.Domain.Utility.ExtensionsUtil
import com.ncs.mario.UI.AuthScreen.AuthActivity
import com.ncs.mario.UI.MainScreen.MainActivity
import com.ncs.mario.UI.SurveyScreen.SurveyActivity
import com.ncs.mario.UI.SurveyScreen.SurveyViewModel
import com.ncs.mario.databinding.ActivityStartScreenBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class StartScreen : AppCompatActivity() {
    val binding: ActivityStartScreenBinding by lazy {
        ActivityStartScreenBinding.inflate(layoutInflater)
    }
    private val viewModel:StartScreenViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        if (PrefManager.getToken()==""){
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }
        else{
            viewModel.fetchUserDetails()
            observeViewModel()
        }
    }
    fun observeViewModel(){
        viewModel.userDetailsResult.observe(this){
            if (it){
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            else{
                PrefManager.setShowProfileCompletionAlert(true)
                startActivity(Intent(this, SurveyActivity::class.java))
                finish()
            }
        }
    }
}