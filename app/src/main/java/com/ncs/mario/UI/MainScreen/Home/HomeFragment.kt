package com.ncs.mario.UI.MainScreen.Home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.ncs.mario.Domain.Models.EVENTS.Event
import com.ncs.mario.Domain.Models.EVENTS.PollItem
import com.ncs.mario.Domain.Utility.ExtensionsUtil.gone
import com.ncs.mario.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.mario.Domain.Utility.ExtensionsUtil.visible
import com.ncs.mario.R
import com.ncs.mario.UI.MainScreen.Home.Adapters.BannerAdapter
import com.ncs.mario.UI.MainScreen.Home.Adapters.EventsAdapter
import com.ncs.mario.UI.MainScreen.Home.Adapters.ListItem
import com.ncs.mario.UI.MainScreen.Home.Adapters.PostAdapter
import com.ncs.mario.UI.MainScreen.MainActivity
import com.ncs.mario.databinding.FragmentHomeBinding

class HomeFragment : Fragment(), EventsAdapter.Callback {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var currentPosition = Int.MAX_VALUE / 2
    private val delayMillis: Long = 2000
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var adapter: PostAdapter
    lateinit var eventsAdapter: EventsAdapter
    private val activityBinding: MainActivity by lazy {
        (requireActivity() as MainActivity)
    }

    private lateinit var bannerAdapter: BannerAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    private fun initializeAdapters() {
        bannerAdapter = BannerAdapter(listOf(1,2,3,4))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activityBinding.binding.actionbar.btnHam.setImageResource(R.drawable.ham)
        activityBinding.binding.actionbar.score.visible()
        activityBinding.binding.actionbar.titleTv.text="Mario"

        activityBinding.binding.actionbar.btnHam.setOnClickListener {
            if (activityBinding.binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                activityBinding.binding.drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                activityBinding.binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        initializeAdapters()
        startAutoScroll()
        setupBannerRecyclerView()
        setUpViews()

    }

    private fun setUpViews(){

        binding.viewAllEvents.setOnClickThrottleBounceListener {
            val bundle = Bundle()
            bundle.putString("type", "Events")
            findNavController().navigate(R.id.action_fragment_home_to_fragment_view_all, bundle)
        }

        binding.viewAllPosts.setOnClickThrottleBounceListener {
            val bundle = Bundle()
            bundle.putString("type", "Posts")
            findNavController().navigate(R.id.action_fragment_home_to_fragment_view_all, bundle)
        }

        activityBinding.binding.actionbar.score.setOnClickThrottleBounceListener {
            findNavController().navigate(R.id.fragment_score)
        }

        binding.linkedIN.setOnClickThrottleBounceListener{
            openUrl("https://www.linkedin.com/company/hackncs/mycompany/")
        }

        binding.instagram.setOnClickThrottleBounceListener{
            openUrl("https://www.instagram.com/hackncs/")
        }

        binding.facebook.setOnClickThrottleBounceListener{
            openUrl("https://www.facebook.com/nibblecomputersociety")
        }

        binding.whatsapp.setOnClickThrottleBounceListener{
            openUrl("https://chat.whatsapp.com/HkJHTJuNpNz5zJNBi7tKcl")
        }

        binding.github.setOnClickThrottleBounceListener{
            openUrl("https://github.com/ncs-jss")
        }

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

        setEventsRecyclerView(mutableListOf(1,2,3,4))

        setUpPostsRV(samplePosts)
    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
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

    private fun setEventsRecyclerView(events:MutableList<Int>){
        val recyclerView = binding.EventsRecyclerView
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager
        eventsAdapter= EventsAdapter(events, this)
        recyclerView.adapter = eventsAdapter
    }

    private fun setUpPostsRV(posts:List<ListItem>){
        adapter = PostAdapter(posts)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
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