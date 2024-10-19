package com.ncs.mario.UI.SettingsScreen.Notifications

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ncs.mario.R
import com.ncs.mario.Domain.Utility.ExtensionsUtil.gone
import com.ncs.mario.Domain.Utility.GlobalUtils
import com.ncs.mario.databinding.ActivityNotificationsPrefBinding
import dagger.hilt.android.AndroidEntryPoint



@AndroidEntryPoint
class NotificationsPrefActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationsPrefBinding
    private val util: GlobalUtils.EasyElements by lazy {
        GlobalUtils.EasyElements(this@NotificationsPrefActivity)
    }
    private val TAG = "SettingsActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNotificationsPrefBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpViews()
    }

    private fun setUpViews() {
        binding.actionBar.btnHam.setImageResource(R.drawable.ic_back_arrow)
        binding.actionBar.btnHam.setOnClickListener {
            onBackPressed()
        }
        binding.actionBar.titleTv.text = "App Notifications"
        binding.actionBar.score.gone()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        finish()
    }

}