package com.ncs.mario.UI.StartScreen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ncs.mario.Domain.HelperClasses.PrefManager
import com.ncs.mario.Domain.Utility.ExtensionsUtil.isNull
import com.ncs.mario.Domain.Utility.GlobalUtils
import com.ncs.mario.UI.AuthScreen.AuthActivity
import com.ncs.mario.UI.MainScreen.MainActivity
import com.ncs.mario.UI.SurveyScreen.SurveyActivity
import com.ncs.mario.UI.WaitScreen.WaitActivity
import com.ncs.mario.databinding.ActivityStartScreenBinding
import dagger.hilt.android.AndroidEntryPoint

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
        enableEdgeToEdge()
        Log.d("token",PrefManager.getToken().toString())
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

        viewModel.userDetails.observe(this){
            if (it!=null){
                val user=it
                PrefManager.setUserProfile(user.profile)
                if (user.profile.photo.secure_url!="" &&  user.profile.id_card.secure_url!=""){
                    viewModel.getKYCStatus()
                }
                else{
                    PrefManager.setShowProfileCompletionAlert(true)
                    startActivity(Intent(this, SurveyActivity::class.java))
                    finish()
                }
            }
        }
        viewModel.kycStatus.observe(this){
            if (!it.isNull) {
                if (it) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    startActivity(Intent(this, WaitActivity::class.java))
                    finish()
                }
            }
        }
        viewModel.errorMessage.observe(this){
            if (it!=null){
                util.showActionSnackbar(binding.root,it,200000,"Retry"){
                    viewModel.fetchUserDetails()
                }
            }
        }
    }
}