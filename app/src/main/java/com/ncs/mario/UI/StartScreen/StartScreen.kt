package com.ncs.mario.UI.StartScreen

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
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
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.messaging.FirebaseMessaging
import com.ncs.mario.BuildConfig
import com.ncs.mario.Domain.HelperClasses.PrefManager
import com.ncs.mario.Domain.HelperClasses.RemoteConfigHelper
import com.ncs.mario.Domain.Models.User
import com.ncs.mario.Domain.Utility.ExtensionsUtil.toast
import com.ncs.mario.Domain.Utility.GlobalUtils
import com.ncs.mario.R
import com.ncs.mario.UI.AdminScreen.AdminMainActivity
import com.ncs.mario.UI.AuthScreen.AuthActivity
import com.ncs.mario.UI.MainScreen.MainActivity
import com.ncs.mario.UI.SurveyScreen.SurveyActivity
import com.ncs.mario.UI.WaitScreen.WaitActivity
import com.ncs.mario.databinding.ActivityStartScreenBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

@AndroidEntryPoint
class StartScreen : AppCompatActivity() {
    val binding: ActivityStartScreenBinding by lazy {
        ActivityStartScreenBinding.inflate(layoutInflater)
    }
    private val util: GlobalUtils.EasyElements by lazy {
        GlobalUtils.EasyElements(this)
    }
    private val viewModel: StartScreenViewModel by viewModels()

    private val updateType = AppUpdateType.IMMEDIATE
    private val APP_UPDATE_REQUEST_CODE=101
    private lateinit var appUpdateManager: AppUpdateManager
    private lateinit var remoteConfigHelper: RemoteConfigHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        remoteConfigHelper = RemoteConfigHelper(this)
        appUpdateManager= AppUpdateManagerFactory.create(applicationContext)
        handleDynamicLink(intent)
        startAnim()

        FirebaseMessaging.getInstance().subscribeToTopic("mario-general")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FCM", "Subscribed to topic successfully")
                } else {
                    Log.d("FCM", "Failed to subscribe to topic")
                }
            }

        if (updateType==AppUpdateType.IMMEDIATE){
            appUpdateManager.registerListener(installStateUpdatedListener)
            checkforAppUpdates()
            initializeProcesses()
        }

    }

    private fun  checkforAppUpdates(){
        appUpdateManager.appUpdateInfo.addOnSuccessListener {info->
            val isUpdateAvailable=info.updateAvailability()== UpdateAvailability.UPDATE_AVAILABLE
            val isUpdateAllowed=when(updateType){
                AppUpdateType.IMMEDIATE-> info.isImmediateUpdateAllowed
                else-> false
            }
            if (isUpdateAvailable && isUpdateAllowed){
                appUpdateManager.startUpdateFlowForResult(
                    info,
                    updateType,
                    this,
                    APP_UPDATE_REQUEST_CODE
                )
            }
        }
    }

    private fun initializeProcesses(){
        remoteConfigHelper.fetchRemoteConfig {
            if (BuildConfig.VERSION_CODE>=it.toInt()){

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
            else{
                util.twoBtnDialogNonCancellable("Update Available",
                    "Hooray! A new version on NCS Mario has been released on playstore, please update your app to continue using forward",
                    positiveBtnText = "Update", positive = {
                        //TODO: Open playstore
                    }, negativeBtnText = "Cancel", negative = {
                        finishAffinity()
                    })
            }
        }
    }

    private val installStateUpdatedListener = InstallStateUpdatedListener{state->
        if (state.installStatus() == InstallStatus.DOWNLOADED){
            toast("Download Successful")
            lifecycleScope.launch {
                delay(5.seconds)
                appUpdateManager.completeUpdate()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==APP_UPDATE_REQUEST_CODE){
            if (resultCode!= RESULT_OK){
                finish()
                toast("Something went wrong while updating the app")
            }
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
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }
        else{
            bindObservers()
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

    private fun bindObservers(){
        viewModel.errorMessage.observe(this) {
            if (!it.isNullOrEmpty()) {
                if (it=="User not found!"){
                    PrefManager.setShowProfileCompletionAlert(true)
                    startActivity(Intent(this, SurveyActivity::class.java))
                    finish()
                }
                else {
                    util.showActionSnackbar(binding.root, it, 200000, "Retry") {
                        observeViewModel()
                    }
                }
            }
        }
    }

    private fun observeViewModel() {

        lifecycleScope.launch {
            try {
                val userDetailsDeferred = async { viewModel.fetchUserDetails() }
                val kycTokenDeferred = async { viewModel.fetchUserKYCHeaderToken() }

                val userDetails = userDetailsDeferred.await()
                val kycStatus = kycTokenDeferred.await()

                handleUserDetails(userDetails!!)
                handleKYCStatus(kycStatus, userDetails.profile.role)
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
            startActivity(Intent(this, SurveyActivity::class.java))
            finish()
        }
    }

    private fun handleKYCStatus(kycStatus: String?, role:Int) {
        if (!kycStatus.isNullOrEmpty()) {
            when (kycStatus) {
                "ACCEPT" -> {
                    if (role==1){
                        startActivity(Intent(this, AdminMainActivity::class.java))
                        finish()
                    }
                    else {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                }
                "PENDING", "REJECT" -> {
                    startActivity(Intent(this, WaitActivity::class.java))
                    finish()
                }

            }
        }
    }

}