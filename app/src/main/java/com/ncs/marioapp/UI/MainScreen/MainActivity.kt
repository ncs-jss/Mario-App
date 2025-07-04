package com.ncs.marioapp.UI.MainScreen

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.ncs.marioapp.BuildConfig
import com.ncs.marioapp.R
import com.ncs.marioapp.databinding.ActivityMainBinding
import com.ncs.marioapp.Domain.HelperClasses.PrefManager
import com.ncs.marioapp.Domain.Models.ServerResult
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.gone
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.isNull
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.load
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.visible
import com.ncs.marioapp.Domain.Utility.GlobalUtils
import com.ncs.marioapp.UI.AdminScreen.AdminMainActivity
import com.ncs.marioapp.UI.MyRedemptionsScreen.MyRedemptionsActivity
import com.ncs.marioapp.UI.SettingsScreen.SettingsActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val util: GlobalUtils.EasyElements by lazy {
        GlobalUtils.EasyElements(this)
    }

    private val mainViewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        enableEdgeToEdge()

        WindowCompat.getInsetsController(window, window.decorView)?.isAppearanceLightStatusBars = false

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, 0, systemBars.right, 0)
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.navigationView) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(0, systemBars.top, 0, systemBars.bottom)
            insets
        }

        val qrImage = binding.drawerheaderfile.ivQrCode
        qrGenerator(qrImage)

        mainViewModel.userBitmap.observe(this){
            if (it.isNull){
                binding.drawerheaderfile.profilePic.setImageDrawable(resources.getDrawable(R.drawable.profile_pic_placeholder))
            }
            else{
                binding.drawerheaderfile.profilePic.setImageBitmap(it)
            }
        }

        mainViewModel.cachedUserProfile.observe(this){
            val currentUser=it
//            binding.drawerheaderfile.profilePic.load(currentUser.photo.secure_url,this.getDrawable(R.drawable.profile_pic_placeholder)!!)
            binding.drawerheaderfile.name.text=currentUser.name
            binding.drawerheaderfile.email.text=PrefManager.getUserSignUpEmail()
            binding.drawerheaderfile.Usernumber.text=currentUser.admission_number
            if (currentUser.role==1){
                binding.drawerheaderfile.adminPanel.visible()
            }
            else{
                binding.drawerheaderfile.adminPanel.gone()
            }
        }

        binding.drawerheaderfile.adminPanel.setOnClickThrottleBounceListener {
            startActivity(Intent(this@MainActivity, AdminMainActivity::class.java))
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
        }

        binding.drawerheaderfile.versionCode.text="Version : ${BuildConfig.VERSION_NAME}"

        binding.drawerheaderfile.redemptions.setOnClickThrottleBounceListener {
            startActivity(Intent(this@MainActivity, MyRedemptionsActivity::class.java))
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
        }
        binding.drawerheaderfile.scanButton.setOnClickThrottleBounceListener {
            startActivity(Intent(this@MainActivity, QRScannerActivity::class.java))
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
        }

        binding.drawerheaderfile.settings.setOnClickThrottleBounceListener {
            startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
        }

        bindObeservers()

        binding.actionbar.btnHam.setOnClickThrottleBounceListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        val bottomNav = binding.bottomNavigationView
        bottomNav.setupWithNavController(navController)

        bottomNav.setOnItemSelectedListener { menuItem ->
            val currentDestination = navController.currentDestination?.id
            when (menuItem.itemId) {
                R.id.fragment_home -> {
                    if (currentDestination != R.id.fragment_home) {
                        navController.navigate(R.id.fragment_home)
                    }
                    true
                }
                R.id.fragment_events -> {
                    if (currentDestination != R.id.fragment_events) {
                        navController.navigate(R.id.fragment_events)
                    }
                    true
                }
                R.id.fragment_internship -> {
                    if (currentDestination != R.id.fragment_internship) {
                        navController.navigate(R.id.fragment_internship)
                    }
                    true
                }
                R.id.fragment_score -> {
                    if (currentDestination != R.id.fragment_score) {
                        navController.navigate(R.id.fragment_score)
                    }
                    true
                }
                R.id.fragment_store -> {
                    if (currentDestination != R.id.fragment_store) {
                        navController.navigate(R.id.fragment_store)
                    }
                    true
                }
                else -> {
                    false
                }
            }
        }

    }

    private fun qrGenerator(qrImage: ImageView) {
        val size = 400
        val bits = QRCodeWriter().encode(PrefManager.getUserID(), BarcodeFormat.QR_CODE, size, size)
        val bitmap = createBitmap(size, size, Bitmap.Config.RGB_565).also {
            for (x in 0 until size) {
                for (y in 0 until size) {
                    it.setPixel(x, y, if (bits[x, y]) Color.BLACK else Color.WHITE)
                }
            }
        }
        qrImage.setImageBitmap(bitmap)
    }

    private fun bindObeservers() {

        mainViewModel.errorMessage.observe(this) {
            showError(it)
        }

        mainViewModel.progressState.observe(this) {
            if (it) {
                binding.linearProgressIndicator.visible()
            } else {
                binding.linearProgressIndicator.gone()
            }
        }

        mainViewModel.userCoins.observe(this) { result ->
            updateScoreUI(result)
        }
        mainViewModel.validateScannedQR.observe(this){result ->
            when(result){
                is ServerResult.Failure ->{
                    showError(result.message)
                }
                ServerResult.Progress -> {
                    showLoading()
                }
                is ServerResult.Success ->{
//                    Toast.makeText(this@MainActivity,result.data,Toast.LENGTH_SHORT).show()
                }

                null -> {}
            }
        }
    }

    private fun showError(message: String?) {
        util.showSnackbar(binding.root,message!!,2000)
    }

    private fun updateScoreUI(marioScore: Int) {
        binding.actionbar.coinsTV.text = marioScore.toString()
    }

    private fun showLoading() {
        binding.linearProgressIndicator.visible()
    }


    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }



    override fun onResume() {
        super.onResume()
        mainViewModel.fetchUserDetails()
        lifecycleScope.launch(Dispatchers.IO) {
            val cachedBitmap = PrefManager.getUserProfileImage(this@MainActivity)

            if (cachedBitmap == null) {
                Log.d("ImageCheckProfile", "Profile Image is null, loading now")

                val secureUrl = PrefManager.getUserProfile()?.photo?.secure_url
                if (!secureUrl.isNullOrEmpty()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        ExtensionsUtil.getRotatedBitmap(
                            this@MainActivity,
                            secureUrl,
                            resources.getDrawable(R.drawable.profile_pic_placeholder, theme)
                        ) { bitmap ->
                            CoroutineScope(Dispatchers.IO).launch {
                                if (bitmap != null) {
                                    Log.d("ImageCheckProfile", "Profile Image successfully loaded")
                                    PrefManager.setUserProfileImage(this@MainActivity, bitmap)
                                    mainViewModel.setUserProfileBitmap(bitmap)

                                } else {
                                    Log.e("ImageCheckProfile", "Failed to load profile image")
                                }
                            }
                        }
                    }
                } else {
                    Log.e("ImageCheckProfile", "Secure URL is null or empty")
                }
            } else {
                mainViewModel.setUserProfileBitmap(cachedBitmap)
                Log.d("ImageCheckProfile", "Profile Image was present in cache")
            }
        }

    }

}