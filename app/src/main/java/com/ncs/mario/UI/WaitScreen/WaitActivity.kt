package com.ncs.mario.UI.WaitScreen

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ncs.mario.Domain.HelperClasses.PrefManager
import com.ncs.mario.Domain.Utility.ExtensionsUtil.gone
import com.ncs.mario.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.mario.Domain.Utility.ExtensionsUtil.visible
import com.ncs.mario.Domain.Utility.GlobalUtils
import com.ncs.mario.UI.MainScreen.MainActivity
import com.ncs.mario.UI.StartScreen.StartScreen
import com.ncs.mario.UI.SurveyScreen.SurveyActivity
import com.ncs.mario.databinding.ActivityWaitBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WaitActivity : AppCompatActivity() {

    private val binding: ActivityWaitBinding by lazy {
        ActivityWaitBinding.inflate(layoutInflater)
    }

    private val viewModel: WaitScreenViewModel by viewModels()
    private val util: GlobalUtils.EasyElements by lazy {
        GlobalUtils.EasyElements(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        observeViewModel()
        setUpViews()
    }

    fun setUpViews(){
        binding.logout.setOnClickThrottleBounceListener{
            PrefManager.clearPrefs()
            startActivity(Intent(this, StartScreen::class.java))
            finish()
        }
        binding.resubmit.setOnClickThrottleBounceListener {
            PrefManager.setShowProfileCompletionAlert(true)
            startActivity(Intent(this,SurveyActivity::class.java))
            finish()
        }
    }

    fun observeViewModel(){
        viewModel.kycStatus.observe(this) { kycStatus ->
            if (kycStatus != null) {
                when(kycStatus){
                    "ACCEPT" ->{
                        util.showSnackbar(binding.root, "You were verified!!!", 2000)
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                    "REJECT"->{
                        binding.waitLayout.gone()
                        binding.waitLayout2.gone()
                        binding.rejectLayout.visible()
                    }
                    "PENDING"->{
                        binding.waitLayout.visible()
                        binding.waitLayout2.visible()
                        binding.rejectLayout.gone()
                    }
                }
            }
        }
        viewModel.errorMessage.observe(this) { errorMessage ->
            if (errorMessage != null) {
                util.showSnackbar(binding.root, errorMessage, 2000)
            }
        }
    }
}