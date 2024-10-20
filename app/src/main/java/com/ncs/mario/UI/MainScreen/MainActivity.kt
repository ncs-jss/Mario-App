package com.ncs.mario.UI.MainScreen

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ncs.mario.R
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.ncs.mario.BuildConfig
import com.ncs.mario.Domain.HelperClasses.PrefManager
import com.ncs.mario.Domain.Models.ServerResult
import com.ncs.mario.Domain.Utility.ExtensionsUtil.load
import com.ncs.mario.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.mario.Domain.Utility.GlobalUtils
import com.ncs.mario.UI.MyRedemptionsScreen.MyRedemptionsActivity
import com.ncs.mario.UI.SettingsScreen.SettingsActivity
import com.ncs.mario.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream

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

        val qrImage = binding.drawerheaderfile.ivQrCode
        qrGenerator(qrImage)
        val currentUser=PrefManager.getUserProfile()!!
        binding.drawerheaderfile.profilePic.load(currentUser.photo.secure_url,this.getDrawable(R.drawable.profile_pic_placeholder)!!)
        binding.drawerheaderfile.name.text=currentUser.name
        binding.drawerheaderfile.email.text=PrefManager.getUserSignUpEmail()
        binding.drawerheaderfile.Usernumber.text=currentUser.admission_number

        binding.drawerheaderfile.versionCode.text="Version : ${BuildConfig.VERSION_NAME}"

        binding.drawerheaderfile.redemptions.setOnClickThrottleBounceListener {
            startActivity(Intent(this@MainActivity, MyRedemptionsActivity::class.java))
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
        }
        binding.drawerheaderfile.scanButton.setOnClickThrottleBounceListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            scannerLauncher.launch(
                ScanOptions().setPrompt("Scan to get Mario Points")
                    .setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            )
        }
        binding.actionbar.scoreTV.text = PrefManager.getUserProfile()?.points.toString()

        binding.drawerheaderfile.settings.setOnClickThrottleBounceListener {
            startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
        }

        bindObeservers()

        binding.actionbar.btnHam.setOnClickListener {
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
            when (menuItem.itemId) {
                R.id.fragment_home -> {
                    navController.navigate(R.id.fragment_home)
                    true
                }
                R.id.fragment_internship -> {
                    navController.navigate(R.id.fragment_internship)
                    true
                }
                R.id.fragment_score -> {
                    navController.navigate(R.id.fragment_score)
                    true
                }
                R.id.fragment_store -> {
                    navController.navigate(R.id.fragment_store)
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
        mainViewModel.myMarioScore.observe(this) { result ->
            when (result) {
                is ServerResult.Progress -> {
                    showLoading()
                }
                is ServerResult.Success -> {
                    val marioScore = result.data
                    updateScoreUI(marioScore)
                }
                is ServerResult.Failure -> {
                    showError(result.exception.message)
                }
            }
        }
        mainViewModel.validateScannedQR.observe(this){result ->
            when(result){
                is ServerResult.Failure ->{
                    showError(result.exception.message)
                }
                ServerResult.Progress -> {
                    showLoading()
                }
                is ServerResult.Success ->{
                    Toast.makeText(this,result.data,Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun showError(message: String?) {
        util.showSnackbar(binding.root,message!!,2000)
    }

    private fun updateScoreUI(marioScore: Int) {
        binding.actionbar.scoreTV.text = marioScore.toString()
    }

    private fun showLoading() {
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


    private val scannerLauncher = registerForActivityResult<ScanOptions, ScanIntentResult>(
        ScanContract()
    ) { result ->

        if (result.contents == null) {
            Toast.makeText(this, "Scan Cancelled", Toast.LENGTH_SHORT).show()
        } else {
            mainViewModel.validateScannedQR(result.contents)
        }
    }

}