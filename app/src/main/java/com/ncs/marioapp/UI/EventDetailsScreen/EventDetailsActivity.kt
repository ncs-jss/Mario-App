package com.ncs.marioapp.UI.EventDetailsScreen

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.ncs.marioapp.Domain.Models.Events.Event
import com.ncs.marioapp.databinding.ActivityEventDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EventDetailsActivity : AppCompatActivity() {

    val binding: ActivityEventDetailsBinding by lazy {
        ActivityEventDetailsBinding.inflate(layoutInflater)
    }

    private val viewModel: EventDetailsViewModel by viewModels()

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

        val event = intent.getParcelableExtra<Event>("event_data")

        val enrolledCount = intent.getStringExtra("enrolled_count") ?: "10 +"

        viewModel.setEvent(event!!)

        viewModel.setEnrolledCount(enrolledCount)

    }

}