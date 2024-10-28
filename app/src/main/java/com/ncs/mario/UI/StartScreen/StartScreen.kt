package com.ncs.mario.UI.StartScreen

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.ncs.mario.Domain.HelperClasses.PrefManager
import com.ncs.mario.Domain.Models.User
import com.ncs.mario.Domain.Utility.GlobalUtils
import com.ncs.mario.UI.AuthScreen.AuthActivity
import com.ncs.mario.UI.MainScreen.MainActivity
import com.ncs.mario.UI.SurveyScreen.SurveyActivity
import com.ncs.mario.UI.WaitScreen.WaitActivity
import com.ncs.mario.databinding.ActivityStartScreenBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StartScreen : AppCompatActivity() {
    val binding: ActivityStartScreenBinding by lazy {
        ActivityStartScreenBinding.inflate(layoutInflater)
    }
    private val util: GlobalUtils.EasyElements by lazy {
        GlobalUtils.EasyElements(this)
    }
    private val viewModel: StartScreenViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        handleDynamicLink(intent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                runNormally(true)
            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                util.twoBtnDialog(
                    title = "Notification Permission required",
                    msg = "Notification permission is required for better functioning of the app",
                    positiveBtnText = "OK",
                    positive = {
                        requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    },
                    negativeBtnText = "Cancel",
                    negative = {
                        runNormally(false)
                    })
            } else {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        else{
            runNormally(true)
        }
    }

    private fun runNormally(isPermissionGranted:Boolean){
        if (PrefManager.getToken()==""){
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }
        else{
            observeViewModel()
        }
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            runNormally(true)
        } else {
            util.twoBtnDialog(
                title = "Notification Permission required",
                msg = "You can always allow permissions from the App's settings.",
                positiveBtnText = "Take me there",
                positive = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", this@StartScreen.packageName, null)
                    }
                    startActivity(intent)
                },
                negativeBtnText = "Cancel",
                negative = {
                    runNormally(false)
                }
            )
        }
    }

    private fun handleDynamicLink(intent: Intent?) {
        val dynamicLinkTask = FirebaseDynamicLinks.getInstance().getDynamicLink(intent!!)
        dynamicLinkTask.addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val dynamicLink = task.result
                if (dynamicLink != null) {
                    handleDeepLink(dynamicLink.link!!)
                }
            } else {
                Log.e("DynamicLink", "Error getting dynamic link", task.exception)
            }
        }
    }

    private fun handleDeepLink(uri: Uri) {
        Log.d("shareLinkTest", uri.toString())
        val pathSegments = uri.pathSegments
        Log.d("shareLinkTest", pathSegments.toString())
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            try {
                val userDetailsDeferred = async { viewModel.fetchUserDetails() }
                val kycTokenDeferred = async { viewModel.fetchUserKYCHeaderToken() }

                val userDetails = userDetailsDeferred.await()
                val kycStatus = kycTokenDeferred.await()

                handleUserDetails(userDetails!!)
                handleKYCStatus(kycStatus)
            } catch (e: Exception) {
                util.showActionSnackbar(binding.root, "Something went wrong.", 200000, "Retry") {
                    observeViewModel()
                }
            }
        }
    }

    private fun handleUserDetails(user: User) {
        PrefManager.setUserProfile(user.profile)
        if (user.profile.name.isNotEmpty()) {
            PrefManager.setUserProfileForCache(user.profile)
        }
        if (user.profile.photo.secure_url.isNotEmpty() && user.profile.id_card.secure_url.isNotEmpty()) {
            PrefManager.setShowProfileCompletionAlert(false)
        }
        else {
            PrefManager.setShowProfileCompletionAlert(true)
            startActivity(Intent(this, SurveyActivity::class.java))
            finish()
        }
    }

    private fun handleKYCStatus(kycStatus: String?) {
        if (!kycStatus.isNullOrEmpty()) {
            when (kycStatus) {
                "ACCEPT" -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                "PENDING", "REJECT" -> {
                    startActivity(Intent(this, WaitActivity::class.java))
                    finish()
                }
            }
        }
    }

}