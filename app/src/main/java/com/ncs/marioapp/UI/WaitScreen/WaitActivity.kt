package com.ncs.marioapp.UI.WaitScreen

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.ncs.marioapp.Domain.HelperClasses.PrefManager
import com.ncs.marioapp.Domain.Models.WorkerFlow
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.gone
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.isNull
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.runDelayed
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.visible
import com.ncs.marioapp.Domain.Utility.GlobalUtils
import com.ncs.marioapp.UI.MainScreen.MainActivity
import com.ncs.marioapp.UI.StartScreen.StartScreen
import com.ncs.marioapp.UI.SurveyScreen.SurveyActivity
import com.ncs.marioapp.databinding.ActivityWaitBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class WaitActivity : AppCompatActivity() {

    private val binding: ActivityWaitBinding by lazy {
        ActivityWaitBinding.inflate(layoutInflater)
    }

    private val viewModel: WaitScreenViewModel by viewModels()
    private val util: GlobalUtils.EasyElements by lazy {
        GlobalUtils.EasyElements(this)
    }
    var opened_from:String?=null
    var user_image_worker_id:String?=null
    var college_image_worker_id:String?=null
    var profile_worker_id:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        opened_from=intent.getStringExtra("opened_from")
        val workManager = WorkManager.getInstance(applicationContext)

        if (!opened_from.isNull){
            if (opened_from=="startscreen"){
                viewModel.startKYCStatusCheck()
                observeViewModel()
            }
            else{
                user_image_worker_id=intent.getStringExtra("user_image_worker_id")
                college_image_worker_id=intent.getStringExtra("college_image_worker_id")
                profile_worker_id=intent.getStringExtra("profile_worker_id")

                binding.waitLayout.visible()
                binding.waitLayout2.visible()
                binding.rejectLayout.gone()

                workManager.getWorkInfoByIdLiveData(UUID.fromString(user_image_worker_id)).observe(this, Observer { workInfo ->
                    if (workInfo != null) {
                        when (workInfo.state) {
                            WorkInfo.State.RUNNING -> {
                                binding.progressView.visible()
                                binding.progressBar.progress=0
                                binding.progressTV.text="Completed : 0 %"
                                binding.loadingTV.text="Your profile is being created"
                            }
                            WorkInfo.State.SUCCEEDED -> {
                                binding.progressView.visible()
                                binding.progressBar.progress=30
                                binding.progressTV.text="Completed : 30 %"
                                binding.loadingTV.text="Your profile is being created"
                            }
                            WorkInfo.State.FAILED -> {
                                PrefManager.setAlertMessage("Something went wrong in creating your profile, please try again")
                                startActivity(Intent(this, SurveyActivity::class.java))
                                finish()
                            }
                            WorkInfo.State.CANCELLED -> {}
                            WorkInfo.State.ENQUEUED -> {}
                            WorkInfo.State.BLOCKED -> {}
                        }
                    }
                })

                workManager.getWorkInfoByIdLiveData(UUID.fromString(college_image_worker_id)).observe(this, Observer { workInfo ->
                    if (workInfo != null) {
                        when (workInfo.state) {
                            WorkInfo.State.RUNNING -> {
                                binding.progressView.visible()
                                binding.progressBar.progress=40
                                binding.progressTV.text="Completed : 40 %"
                                binding.loadingTV.text="Your profile is being created"
                            }
                            WorkInfo.State.SUCCEEDED -> {
                                binding.progressView.visible()
                                binding.progressBar.progress=60
                                binding.progressTV.text="Completed : 60 %"
                                binding.loadingTV.text="Your profile is being created"
                            }
                            WorkInfo.State.FAILED -> {
                                PrefManager.setAlertMessage("Something went wrong in creating your profile, please try again")
                                startActivity(Intent(this, SurveyActivity::class.java))
                                finish()
                            }
                            WorkInfo.State.CANCELLED -> {}
                            WorkInfo.State.ENQUEUED -> {}
                            WorkInfo.State.BLOCKED -> {}
                        }
                    }
                })

                workManager.getWorkInfoByIdLiveData(UUID.fromString(profile_worker_id)).observe(this, Observer { workInfo ->
                    if (workInfo != null) {
                        when (workInfo.state) {
                            WorkInfo.State.RUNNING -> {
                                binding.progressView.visible()
                                binding.progressBar.progress=70
                                binding.progressTV.text="Completed : 70 %"
                                binding.loadingTV.text="Your profile is being created"
                            }
                            WorkInfo.State.SUCCEEDED -> {
                                binding.progressView.visible()
                                binding.progressBar.progress=100
                                binding.progressTV.text="Completed : 100 %"
                                binding.loadingTV.text="You are being verified!"

                                runDelayed(1000){
                                    binding.progressView.gone()
                                }

                                viewModel.startKYCStatusCheck()
                                observeViewModel()
                                PrefManager.saveWorkerFlow(WorkerFlow())
                            }
                            WorkInfo.State.FAILED -> {
                                PrefManager.setAlertMessage("Something went wrong in creating your profile, please try again")
                                startActivity(Intent(this, SurveyActivity::class.java))
                                finish()
                            }
                            WorkInfo.State.CANCELLED -> {}
                            WorkInfo.State.ENQUEUED -> {}
                            WorkInfo.State.BLOCKED -> {}
                        }
                    }
                })
            }
        }
        setContentView(binding.root)
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