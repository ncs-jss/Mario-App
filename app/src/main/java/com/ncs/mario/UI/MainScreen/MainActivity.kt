package com.ncs.mario.UI.MainScreen

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ncs.mario.R
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.ncs.mario.Domain.Models.ServerResult
import com.ncs.mario.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.mario.Domain.Utility.GlobalUtils
import com.ncs.mario.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

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