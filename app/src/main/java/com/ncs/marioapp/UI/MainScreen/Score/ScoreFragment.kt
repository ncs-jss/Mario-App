package com.ncs.marioapp.UI.MainScreen.Score

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ncs.marioapp.Domain.HelperClasses.PrefManager
import com.ncs.marioapp.Domain.Models.Events.ParticipatedEvent
import com.ncs.marioapp.Domain.Models.ServerResult
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.gone
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.visible
import com.ncs.marioapp.Domain.Utility.GlobalUtils
import com.ncs.marioapp.R
import com.ncs.marioapp.UI.MainScreen.MainActivity
import com.ncs.marioapp.UI.MainScreen.MainViewModel
import com.ncs.marioapp.databinding.FragmentScoreBinding
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
    private lateinit var enrolledEventAdapter: PastEventAdapter

    private val util: GlobalUtils.EasyElements by lazy {
        GlobalUtils.EasyElements(requireActivity())
    }

    private val activityBinding: MainActivity by lazy {
        (requireActivity() as MainActivity)
    }

    private val activityViewModel : MainViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        activityBinding.binding.actionbar.score.visibility=View.VISIBLE
        activityBinding.binding.actionbar.btnHam.setImageResource(R.drawable.ham)
        activityBinding.binding.actionbar.titleTv.text="Mario Score"
        activityBinding.binding.actionbar.btnHam.setOnClickListener {
            if (activityBinding.binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                activityBinding.binding.drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                activityBinding.binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.swiperefresh.setOnRefreshListener {
            viewModel.getMyEvents()
            activityViewModel.fetchCriticalInfo()
        }

        binding.shimmerLayout.apply {
            startShimmer()
            visibility = View.VISIBLE
        }
        binding.shimmerLayoutPoints.apply {
            startShimmer()
            visibility = View.VISIBLE
        }
        binding.shimmerCoinsLayout.apply {
            startShimmer()
            visibility = View.VISIBLE
        }
        binding.shimmerProgressLayout.apply {
            startShimmer()
            visibility = View.VISIBLE
        }

        binding.pointsView.gone()
        binding.progresslayout.gone()
        binding.coinsView.gone()
        binding.pastEventLayout.gone()
        binding.enrolledEventsLayout.gone()

        binding.marioScoreInfoButton.setOnClickThrottleBounceListener{
            val bottomSheet=MarioCoinsInfoBottomSheet(points = PrefManager.getMyPoints())
            bottomSheet.show(parentFragmentManager,"marioCoinsInfoBottomSheet")
        }

        binding.coinTransactionHistory.setOnClickListener {
            findNavController().navigate(R.id.action_fragment_score_to_fragment_coin_transactions)
        }

        activityViewModel.fetchCriticalInfo()
        viewModel.getMyEvents()
        pastEventAdapter = PastEventAdapter()
        binding.pastEventRecyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            adapter = pastEventAdapter

        }
        pastEventAdapter.submitList(getList())

        enrolledEventAdapter = PastEventAdapter()
        binding.enrolledEventRecyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            adapter = enrolledEventAdapter

        }
        enrolledEventAdapter.submitList(getList())
        bindObserver()
    }

    private fun bindObserver() {

        activityViewModel.userCoins.observe(viewLifecycleOwner){
            binding.coins.text = it?.toString()
            binding.shimmerCoinsLayout.apply {
                stopShimmer()
                visibility = View.GONE
            }
            binding.coinsView.visible()
        }

        activityViewModel.userPoints.observe(viewLifecycleOwner){
            PrefManager.setMyPoints(it)
            binding.points.text = it?.toString()
            if (it<100){
                binding.level.text="Level: Noobie"
                binding.noobieIMG.setImageResource(R.drawable.outline_check_circle_outline_24)
                binding.intermediateIMG.setImageResource(R.drawable.twotone_circle_24)
                binding.pro.setImageResource(R.drawable.twotone_circle_24)
                binding.afterNobbie.setBackgroundColor(getColor(requireContext(),R.color.neutral200))
                binding.afterIntermediate.setBackgroundColor(getColor(requireContext(), R.color.neutral200))
            }
            else if(it!!>100 && it<400){
                binding.level.text="Level: Intermediate"
                binding.noobieIMG.setImageResource(R.drawable.outline_check_circle_outline_24)
                binding.intermediateIMG.setImageResource(R.drawable.outline_check_circle_outline_24)
                binding.afterNobbie.setBackgroundColor(getColor(requireContext(),R.color.appblue))
                binding.pro.setImageResource(R.drawable.twotone_circle_24)
                binding.afterIntermediate.setBackgroundColor(getColor(requireContext(), R.color.neutral200))
            }
            else if(it>400){
                binding.level.text="Level: Pro"
                binding.noobieIMG.setImageResource(R.drawable.outline_check_circle_outline_24)
                binding.intermediateIMG.setImageResource(R.drawable.outline_check_circle_outline_24)
                binding.pro.setImageResource(R.drawable.outline_check_circle_outline_24)
                binding.afterNobbie.setBackgroundColor(getColor(requireContext(), R.color.appblue))
                binding.afterIntermediate.setBackgroundColor(getColor(requireContext(), R.color.appblue))
            }
            binding.shimmerLayoutPoints.apply {
                stopShimmer()
                visibility = View.GONE
            }
            binding.shimmerProgressLayout.apply {
                stopShimmer()
                visibility = View.GONE
            }
            binding.pointsView.visible()
            binding.progresslayout.visible()
        }

        viewModel.progressState.observe(viewLifecycleOwner) {
            if (it) {
                activityBinding.binding.linearProgressIndicator.visible()
            } else {
                activityBinding.binding.linearProgressIndicator.gone()
            }
        }

        viewModel.getEventsResponse.observe(viewLifecycleOwner){ result ->
            when(result){
                is ServerResult.Failure ->{
                    util.showSnackbar(binding.root,result.exception.message.toString(),20000)
                }
                ServerResult.Progress -> {

                }
                is ServerResult.Success -> {
                    if(result.data.success){
                        binding.shimmerLayout.apply {
                            stopShimmer()
                            visibility = View.GONE
                        }
                        binding.enrolledEventsLayout.visible()
                        binding.pastEventLayout.visible()

                        val allEvents=result.data.events

                        val enrolledEvents = allEvents.filter { !it.attended }.sortedByDescending { it.createdAt }
                        val pastEvents = allEvents.filter { it.attended }.sortedByDescending { it.createdAt }

                        if (enrolledEvents.isEmpty()){
                            binding.enrolledEventsLayout.gone()
                        }

                        if (pastEvents.isEmpty()){
                            binding.pastEventLayout.gone()
                        }

                        pastEventAdapter.submitList(pastEvents)

                        enrolledEventAdapter.submitList(enrolledEvents)

                        Log.d("checkEvnets", result.data.events.toString())
                        if (binding.swiperefresh.isRefreshing){
                            binding.swiperefresh.isRefreshing = false
                        }
                    }
                    else{
                        util.showSnackbar(binding.root,result.data.message,20000)
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
        return listOf()
    }
}