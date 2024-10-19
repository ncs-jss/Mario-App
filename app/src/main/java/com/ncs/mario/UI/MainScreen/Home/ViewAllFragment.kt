package com.ncs.mario.UI.MainScreen.Home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ncs.mario.Domain.Models.EVENTS.Event
import com.ncs.mario.Domain.Models.EVENTS.PollItem
import com.ncs.mario.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.mario.R
import com.ncs.mario.UI.MainScreen.Home.Adapters.EventsAdapter
import com.ncs.mario.UI.MainScreen.Home.Adapters.ListItem
import com.ncs.mario.UI.MainScreen.Home.Adapters.PostAdapter
import com.ncs.mario.UI.MainScreen.MainActivity
import com.ncs.mario.databinding.FragmentViewAllBinding


class ViewAllFragment : Fragment(), EventsAdapter.Callback {

    lateinit var binding: FragmentViewAllBinding
    var type:String?=null
    private val activityBinding: MainActivity by lazy {
        (requireActivity() as MainActivity)
    }
    lateinit var eventsAdapter: EventsAdapter
    private lateinit var adapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentViewAllBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        activityBinding.binding.actionbar.score.visibility=View.GONE
        activityBinding.binding.actionbar.btnHam.setImageResource(R.drawable.ic_back_arrow)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val _type=requireArguments().getString("type")
        type=_type
        setUpViews()
    }

    private fun setUpViews(){
        activityBinding.binding.actionbar.titleTv.text=type!!.capitalize()

        activityBinding.binding.actionbar.btnHam.setOnClickThrottleBounceListener{
            moveToPrevious()
        }

        if (type=="Posts"){
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
            setUpPostsRV(samplePosts)
        }
        else if (type=="Events"){
            setEventsRecyclerView(mutableListOf(1,2,3,4))
        }

    }

    private fun setEventsRecyclerView(events:MutableList<Int>){
        val recyclerView = binding.recyclerView
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        onBackPress()
    }

    private fun onBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            moveToPrevious()
        }
    }

    fun moveToPrevious(){
        findNavController().navigate(R.id.action_fragment_view_all_to_fragment_home)
    }

    override fun onClick(event: Event) {
    }

}