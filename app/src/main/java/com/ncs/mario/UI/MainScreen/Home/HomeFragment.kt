package com.ncs.mario.UI.MainScreen.Home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.ncs.mario.Domain.Models.EVENTS.Event
import com.ncs.mario.Domain.Models.EVENTS.PollItem
import com.ncs.mario.R
import com.ncs.mario.databinding.FragmentHomeBinding

class HomeFragment : Fragment(), EventsAdapter.Callback {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var currentPosition = Int.MAX_VALUE / 2
    private val delayMillis: Long = 2000
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var adapter: PostAdapter
    lateinit var eventsAdapter: EventsAdapter

    private lateinit var bannerAdapter: BannerAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater,container,false)
        initializeAdapters()
        setupBannerRecyclerView()
        startAutoScroll()
        return binding.root

    }

    private fun initializeAdapters() {
        bannerAdapter = BannerAdapter(listOf(1,2,3,4))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val samplePosts = listOf(
            ListItem.Post(
                title = "Event 1",
                description = "A great event!",
                logo = "https://gratisography.com/wp-content/uploads/2024/03/gratisography-vr-glasses-1170x780.jpg"
            ),
            ListItem.Poll(
                title = "Poll: Favorite Language?",
                pollItem = PollItem(
                    question = "What's your favorite programming language?",
                    options = listOf(
                        PollItem.Option(text = "Kotlin", votes = 10),
                        PollItem.Option(text = "Java", votes = 5),
                        PollItem.Option(text = "Python", votes = 8),
                        PollItem.Option(text = "Swift", votes = 3)
                    )
                )
            ),
            ListItem.Post(
                title = "Event 2",
                description = "Another awesome event!",
                logo = "https://gratisography.com/wp-content/uploads/2024/03/gratisography-vr-glasses-1170x780.jpg"
            )
        )

        // Initialize the RecyclerView
        adapter = PostAdapter(samplePosts)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        setEventsRecyclerView(mutableListOf(1,2,3,4))
    }

    private fun setEventsRecyclerView(events:MutableList<Int>){
        val recyclerView = binding.EventsRecyclerView
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager
        eventsAdapter= EventsAdapter(events, this)
        recyclerView.adapter = eventsAdapter
    }

    private fun setupBannerRecyclerView() {
        binding.bannerRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = bannerAdapter
            scrollToPosition(currentPosition)
            LinearSnapHelper().attachToRecyclerView(this)
            addOnScrollListener(autoScrollListener)
        }
    }
    private val autoScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            when (newState) {
                RecyclerView.SCROLL_STATE_IDLE -> startAutoScroll()
                else -> stopAutoScroll()
            }
        }
    }
    private fun startAutoScroll() {
        handler.postDelayed(autoScrollRunnable, delayMillis)
    }

    private fun stopAutoScroll() {
        handler.removeCallbacks(autoScrollRunnable)
    }
    private val autoScrollRunnable = object : Runnable {
        override fun run() {
            currentPosition++
            binding.bannerRecyclerView.smoothScrollToPosition(currentPosition)
            handler.postDelayed(this, delayMillis)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        stopAutoScroll()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        stopAutoScroll()
    }
    override fun onResume() {
        super.onResume()
        startAutoScroll()
    }

    override fun onClick(event: Event) {

    }


}