package com.ncs.mario.UI.MainScreen

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ncs.mario.R
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.ncs.mario.UI.MainScreen.Home.HomeFragment
import com.ncs.mario.UI.MainScreen.Internship.InternshipFragment
import com.ncs.mario.UI.MainScreen.QR.QrScanActivity
import com.ncs.mario.UI.MainScreen.Score.ScoreFragment
import com.ncs.mario.UI.MainScreen.Store.StoreFragment
import com.ncs.mario.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.mario.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        binding.actionbar.btnHam.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNavigationView.itemIconTintList= null

        binding.bottomNavigationView.menu.clear()

        binding.bottomNavigationView.menu.add(Menu.NONE, R.id.homeFragment, Menu.NONE, "Home").setIcon(R.drawable.home_selected)
        binding.bottomNavigationView.menu.add(Menu.NONE, R.id.internshipsFragment, Menu.NONE, "Internships").setIcon(R.drawable.internship)
        binding.bottomNavigationView.menu.add(Menu.NONE, R.id.scoreFragment, Menu.NONE, "Score").setIcon(R.drawable.token)
        binding.bottomNavigationView.menu.add(Menu.NONE, R.id.storeFragment, Menu.NONE, "Store").setIcon(R.drawable.store)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.internshipsFragment -> {
                    binding.actionbar.titleTv.text = "Internships"
                }
                R.id.scoreFragment -> {
                    binding.actionbar.titleTv.text = "Score"
                }
                R.id.storeFragment -> {
                    binding.actionbar.titleTv.text = "Store"
                }
                else -> {
                    binding.actionbar.titleTv.text = "Mario"                }
            }
        }
        binding.bottomNavigationView.setupWithNavController(navController)

        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_profile -> {
                    true
                }
                R.id.nav_settings -> {
                    true
                }
                R.id.nav_logout -> {
                    true
                }
                else -> false
            }
        }
        binding.actionbar.scanQr.setOnClickThrottleBounceListener{
            startActivity(Intent(this, QrScanActivity::class.java))
        }
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
}