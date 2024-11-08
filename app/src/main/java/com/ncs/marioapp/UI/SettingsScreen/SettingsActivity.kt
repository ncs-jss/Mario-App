package com.ncs.marioapp.UI.SettingsScreen

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ncs.marioapp.BuildConfig
import com.ncs.marioapp.Domain.HelperClasses.PrefManager
import com.ncs.marioapp.Domain.Utility.Codes
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.gone
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.toast
import com.ncs.marioapp.Domain.Utility.GlobalUtils
import com.ncs.marioapp.R
import com.ncs.marioapp.UI.SettingsScreen.EditProfile.EditProfileActivity
import com.ncs.marioapp.UI.SettingsScreen.Feedback.FeedbackActivity
import com.ncs.marioapp.UI.SettingsScreen.NewChanges.NewChanges
import com.ncs.marioapp.UI.SettingsScreen.Notifications.NotificationsPrefActivity
import com.ncs.marioapp.UI.StartScreen.StartScreen
import com.ncs.marioapp.databinding.ActivitySettingsBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SettingsActivity : AppCompatActivity(), settingAdater.onSettingClick {

    private lateinit var binding: ActivitySettingsBinding
    private val util: GlobalUtils.EasyElements by lazy {
        GlobalUtils.EasyElements(this@SettingsActivity)
    }
    private val TAG = "SettingsActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setUpViews()
        setUpRecyclerView()
        setContentView(binding.root)
    }

    private fun setUpRecyclerView() {

        val items = listOf(

            settingTitle("profile"),
            settingOption("Edit Profile", R.drawable.round_edit_24, ""),

            settingTitle("Notifications"),
            settingOption("App Notifications", R.drawable.baseline_notifications_active_24, ""),

            settingTitle("what's new"),
            settingOption("What's New", R.drawable.baseline_info_24, BuildConfig.VERSION_NAME),

            settingTitle("Report & Feedback"),
            settingOption("Feedback", R.drawable.baseline_feedback_24, ""),

            settingTitle("Account"),
            settingOption("Log Out", R.drawable.logout, ""),
        )


        val recyclerView = binding.settingRecycler
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        val adapter = settingAdater(items, this@SettingsActivity)
        recyclerView.adapter = adapter
    }

    private fun setUpViews() {
        binding.actionbar.btnHam.setImageResource(R.drawable.ic_back_arrow)
        binding.actionbar.btnHam.setOnClickListener {
            onBackPressed()
        }
        binding.actionbar.titleTv.text = "Settings"
        binding.actionbar.score.gone()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        finish()
    }

    override fun onClick(position: Int) {
        if (Codes.STRINGS.clickedSetting == "Edit Profile") {

            startActivity(Intent(this, EditProfileActivity::class.java))
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)

        } else if (Codes.STRINGS.clickedSetting == "What's New") {

            startActivity(Intent(this, NewChanges::class.java))
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)

        } else if (Codes.STRINGS.clickedSetting == "Log Out") {

            util.dialog("Log out", "You really want to log out?", "Yes", "No",
                {
                    PrefManager.clearPrefs()
                    val intent = Intent(this, StartScreen::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
                    toast("Logged out")
                }, {})

        }
        else if (Codes.STRINGS.clickedSetting == "App Notifications") {

            startActivity(Intent(this, NotificationsPrefActivity::class.java))
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)

        }

        else if (Codes.STRINGS.clickedSetting == "Feedback") {

            startActivity(Intent(this, FeedbackActivity::class.java))
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)

        }

    }

}