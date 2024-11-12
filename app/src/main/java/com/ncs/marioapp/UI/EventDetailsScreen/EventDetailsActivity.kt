package com.ncs.marioapp.UI.EventDetailsScreen

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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

        val event = intent.getParcelableExtra<Event>("event_data")

        viewModel.setEvent(event!!)

    }
}