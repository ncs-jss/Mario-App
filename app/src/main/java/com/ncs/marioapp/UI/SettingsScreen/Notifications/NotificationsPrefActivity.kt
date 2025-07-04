package com.ncs.marioapp.UI.SettingsScreen.Notifications

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.gone
import com.ncs.marioapp.R
import com.ncs.marioapp.databinding.ActivityNotificationsPrefBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationsPrefActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationsPrefBinding
    private lateinit var viewModel: NotificationsPrefViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNotificationsPrefBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        WindowCompat.getInsetsController(window, window.decorView)?.isAppearanceLightStatusBars = false

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
        viewModel = ViewModelProvider(this).get(NotificationsPrefViewModel::class.java)
        setUpViews()
        observeViewModel()
    }

    private fun setUpViews() {

        binding.actionBar.btnHam.setImageResource(R.drawable.ic_back_arrow)
        binding.actionBar.btnHam.setOnClickListener {
            onBackPressed()
        }
        binding.actionBar.titleTv.text = "App Notifications"
        binding.actionBar.score.gone()

        binding.allNotificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setAllNotifications(isChecked)
        }

        binding.postNotificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setPostNotification(isChecked)
        }

        binding.pollNotificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setPollNotification(isChecked)
        }

        binding.eventNotificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setEventNotification(isChecked)
        }

        binding.merchNotificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setMerchNotification(isChecked)
        }
    }

    private fun observeViewModel() {
        viewModel.postNotification.observe(this) { isChecked ->
            binding.postNotificationsSwitch.isChecked = isChecked
        }

        viewModel.pollNotification.observe(this) { isChecked ->
            binding.pollNotificationsSwitch.isChecked = isChecked
        }

        viewModel.eventNotification.observe(this) { isChecked ->
            binding.eventNotificationsSwitch.isChecked = isChecked
        }

        viewModel.merchNotification.observe(this) { isChecked ->
            binding.merchNotificationsSwitch.isChecked = isChecked
        }

        viewModel.allNotifications.observe(this) { isChecked ->
            binding.allNotificationsSwitch.setOnCheckedChangeListener(null)
            binding.allNotificationsSwitch.isChecked = isChecked
            binding.allNotificationsSwitch.setOnCheckedChangeListener { _, newIsChecked ->
                viewModel.setAllNotifications(newIsChecked)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        finish()
    }
}
