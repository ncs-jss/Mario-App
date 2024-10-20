package com.ncs.mario.UI.MainScreen.Score

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ncs.mario.Domain.HelperClasses.PrefManager
import com.ncs.mario.Domain.Models.Events.ParticipatedEvent
import com.ncs.mario.Domain.Models.ServerResult
import com.ncs.mario.Domain.Utility.GlobalUtils
import com.ncs.mario.databinding.FragmentScoreBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScoreFragment : Fragment() {

    companion object {
        fun newInstance() = ScoreFragment()
    }
    private var _binding: FragmentScoreBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ScoreViewModel by viewModels()
    private lateinit var pastEventAdapter: PastEventAdapter

    private val util: GlobalUtils.EasyElements by lazy {
        GlobalUtils.EasyElements(requireContext())
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScoreBinding.inflate(inflater, container, false)
        viewModel.getMyEvents()
        val profile = PrefManager.getUserProfile()
        binding.score.text = profile?.points.toString()
        binding.nameScore.text=profile?.name.toString()
        if(profile?.points!!>100){
            binding.level.text="Level: Intermediate"
        }
        else if(profile.points>400){
            binding.level.text="Level: Pro"
        }
        pastEventAdapter = PastEventAdapter()
        binding.pastEventRecyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            adapter = pastEventAdapter

        }
        pastEventAdapter.submitList(getList())
        bindObserver()
        return binding.root
    }

    private fun bindObserver() {

        viewModel.getEventsResponse.observe(viewLifecycleOwner){
            when(it){
                is ServerResult.Failure ->{
                    util.showSnackbar(binding.root,it.exception.message.toString(),20000)
                }
                ServerResult.Progress -> {

                }
                is ServerResult.Success -> {
                    if(it.data.success){
                        pastEventAdapter.submitList(it.data.events)
                    }
                    else{
                        util.showSnackbar(binding.root,it.data.message,20000)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null

    }
    fun getList():List<ParticipatedEvent>{
        return listOf(
            ParticipatedEvent(
                createdAt = 1697500800000, // Example timestamp
                points = 20,
                enrolled = listOf("user1@example.com", "user2@example.com"),
                time = "2024-10-13T05:44:38.319+00:00",
                _id = "event_id_1",
                image = "https://media.licdn.com/dms/image/v2/C510BAQG3f7TtHfwMUg/company-logo_200_200/company-logo_200_200/0/1630569260174?e=2147483647&v=beta&t=RJjQWcooDe3F4Xyafd26XNbfJcdHT-jlu8ezUII0oYQ",
                domain = listOf("Education", "Technology"),
                title = "CodePad",
                description = "Join us for a day of tech talks and networking.",
                registrationLink = "https://example.com/register1",
                venue = "Computer Center, AB-1",
                enrolledCount = 2
            ),
            ParticipatedEvent(
                createdAt = 1697587200000, // Example timestamp
                points = 25,
                enrolled = listOf("user3@example.com", "user4@example.com"),
                time = "2024-10-13T05:44:38.319+00:00",
                _id = "event_id_2",
                image = "https://media.licdn.com/dms/image/v2/C510BAQG3f7TtHfwMUg/company-logo_200_200/company-logo_200_200/0/1630569260174?e=2147483647&v=beta&t=RJjQWcooDe3F4Xyafd26XNbfJcdHT-jlu8ezUII0oYQ",
                domain = listOf("Health", "Wellness"),
                title = "Health and Wellness Workshop",
                description = "Learn about healthy living and nutrition.",
                registrationLink = "https://example.com/register2",
                venue = "Online",
                enrolledCount = 2
            ),
            ParticipatedEvent(
                createdAt = 1697673600000, // Example timestamp
                points = 15,
                enrolled = listOf("user5@example.com"),
                time = "2024-10-13T05:44:38.319+00:00",
                _id = "event_id_3",
                image = "https://media.licdn.com/dms/image/v2/C510BAQG3f7TtHfwMUg/company-logo_200_200/company-logo_200_200/0/1630569260174?e=2147483647&v=beta&t=RJjQWcooDe3F4Xyafd26XNbfJcdHT-jlu8ezUII0oYQ",
                domain = listOf("Arts", "Culture"),
                title = "Android Workshop",
                description = "Explore contemporary art from local artists.",
                registrationLink = null,
                venue = "Online",
                enrolledCount = 1
            )
        )
    }
}