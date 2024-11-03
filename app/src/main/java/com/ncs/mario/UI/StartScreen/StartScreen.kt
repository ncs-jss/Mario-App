package com.ncs.mario.UI.StartScreen

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
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
import com.ncs.mario.R
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
        startAnim()
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

    private fun startAnim() {
        val slideOutAnim = AnimationUtils.loadAnimation(this, R.anim.slide_out_right)
        val fadeInAnim = AnimationUtils.loadAnimation(this, R.anim.fadein)
        slideOutAnim.duration = 2000
        fadeInAnim.duration = 2500
        binding.blackCircle.startAnimation(slideOutAnim)
        binding.imageView2.startAnimation(fadeInAnim)
        slideOutAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {

                binding.blackCircle.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        fadeInAnim.setAnimationListener(object :Animation.AnimationListener{
            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                binding.imageView2.alpha = 1f
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
    }

    private fun runNormally(isPermissionGranted:Boolean){
        if (PrefManager.getToken()==""){
            runCircleAnimation(AuthActivity::class.java)
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
        viewModel.errorMessage.observe(this) {
            if (!it.isNullOrEmpty()) {
                if (it=="User not found!"){
                    PrefManager.setShowProfileCompletionAlert(true)
                   runCircleAnimation(SurveyActivity::class.java)
                }
                else {
                    util.showActionSnackbar(binding.root, it, 200000, "Retry") {
                        observeViewModel()
                    }
                }

            }
        }
        lifecycleScope.launch {
            try {
                val userDetailsDeferred = async { viewModel.fetchUserDetails() }
                val kycTokenDeferred = async { viewModel.fetchUserKYCHeaderToken() }

                val userDetails = userDetailsDeferred.await()
                val kycStatus = kycTokenDeferred.await()

                handleUserDetails(userDetails!!)
                handleKYCStatus(kycStatus)
            } catch (e: Exception) {

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
            runCircleAnimation(SurveyActivity::class.java)
        }
    }

    private fun handleKYCStatus(kycStatus: String?) {
        if (!kycStatus.isNullOrEmpty()) {
            when (kycStatus) {
                "ACCEPT" -> {
                    runCircleAnimation(MainActivity::class.java)
                }
                "PENDING", "REJECT" -> {
                    runCircleAnimation(WaitActivity::class.java)
                }

            }
        }
    }
    private fun runCircleAnimation(targetActivity: Class<*>) {
        val animation = AnimationUtils.loadAnimation(this, R.anim.circle_flash)
        binding.circleView.startAnimation(animation)
        Handler().postDelayed({
        },200)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                binding.circleView.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animation) {
                startActivity(Intent(this@StartScreen, targetActivity))
                finish()
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })

    }

}