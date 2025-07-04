package com.ncs.marioapp.UI.SettingsScreen.NCSApps

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import br.tiagohm.markdownview.css.InternalStyleSheet
import br.tiagohm.markdownview.css.styles.Github
import com.ncs.marioapp.Domain.Models.NCSApps
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.gone
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.visible
import com.ncs.marioapp.R
import com.ncs.marioapp.UI.MainScreen.Home.Adapters.EventsAdapter
import com.ncs.marioapp.UI.MainScreen.Home.Adapters.NCSAppsAdapter
import com.ncs.marioapp.databinding.ActivityNcsappsBinding
import com.ncs.marioapp.databinding.ActivityNewChangesBinding


class NCSAppsActivity : AppCompatActivity(), NCSAppsAdapter.Callback {

    private lateinit var binding: ActivityNcsappsBinding
    private lateinit var ncsAppsAdapter: NCSAppsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNcsappsBinding.inflate(layoutInflater)
        onBackPressedDispatcher.addCallback(this) {
            finish()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
        }

        setUpViews()
        setContentView(binding.root)
        enableEdgeToEdge()
        WindowCompat.getInsetsController(window, window.decorView)?.isAppearanceLightStatusBars = false
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
    }

    private fun setUpViews() {
        binding.actionBar.btnHam.setImageResource(R.drawable.ic_back_arrow)
        binding.actionBar.btnHam.setOnClickListener {
            onBackPressed()
        }
        binding.actionBar.titleTv.text = "NCS Apps"
        binding.actionBar.score.gone()

        val ncsApps = listOf(
            NCSApps(
                appIcon = resources.getDrawable(R.drawable.mario_logo),
                appName = "Mario : Everything NCS",
                appRating = 4.5,
                appDescription = "Mario: Unlock Your Potential with Nibble! \uD83D\uDE80\n" +
                        "\n" +
                        "Mario is your gateway to Nibble’s exclusive invite-only community, where dedication meets opportunity. Track your engagement, level up through events, and unlock special perks. Stay updated with real-time scores, effortless event registration, and priority recruitment access. With Mario, every effort counts—join, grow, and get recognized. Download now and start your journey!",
                appUrl = "https://play.google.com/store/apps/details?id=com.ncs.marioapp"),

            NCSApps(
                appIcon = resources.getDrawable(R.drawable.o2logo),
                appName = "Oxygen : Projects & Teamwork",
                appRating = 4.5,
                appDescription = "Oxygen: The Ultimate Task & Project Management Tool! \uD83D\uDE80\n" +
                        "\n" +
                        "Streamline your workflow with Oxygen, the all-in-one collaboration app by Nibble Computer Society. Manage tasks seamlessly with agile support, Kanban boards, and real-time updates. Organize, track, and analyze projects with advanced filtering, roadmap views, and insightful dashboards. Stay productive with customizable workflows and dark mode—download now and boost your team’s efficiency! \uD83D\uDD25",
                appUrl = "https://play.google.com/store/apps/details?id=com.ncs.o2"),

            NCSApps(
                appIcon = resources.getDrawable(R.drawable.boidslogo),
                appName = "Boids 2D : Flock Simulation",
                appRating = 5.0,
                appDescription = "Boids 2D: Flock Simulation – Create, Observe, Evolve! \uD83D\uDD4A\uFE0F✨\n" +
                        "\n" +
                        "Dive into the mesmerizing world of motion with Boids 2D, where you control the dance of life. Spawn boids and hunters, add walls for complex challenges, and fine-tune simulations with Vertex Mode. Experience Reynold’s classic algorithm in action, bringing lifelike flocking behavior to your fingertips. Shape, experiment, and watch stunning emergent patterns unfold—download now and start your simulation journey! \uD83D\uDE80",
                appUrl = "https://play.google.com/store/apps/details?id=com.bitpolarity.boids"),

            )

        setUpRV(ncsApps)
    }

    private fun setUpRV(ncsApps: List<NCSApps>){
        val recyclerView = binding.ncsAppsRV
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        ncsAppsAdapter = NCSAppsAdapter(ncsApps, this)
        recyclerView.adapter = ncsAppsAdapter
    }

    override fun onClick(ncsApps: NCSApps) {
        openUrl(ncsApps.appUrl)
    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

}