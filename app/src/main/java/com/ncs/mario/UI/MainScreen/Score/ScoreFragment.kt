package com.ncs.mario.UI.MainScreen.Score

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ncs.mario.Domain.HelperClasses.PrefManager
import com.ncs.mario.Domain.Models.Events.ParticipatedEvent
import com.ncs.mario.Domain.Models.ServerResult
import com.ncs.mario.Domain.Utility.ExtensionsUtil.gone
import com.ncs.mario.Domain.Utility.ExtensionsUtil.visible
import com.ncs.mario.Domain.Utility.GlobalUtils
import com.ncs.mario.UI.MainScreen.MainActivity
import com.ncs.mario.UI.MainScreen.MainViewModel
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
        activityBinding.binding.actionbar.titleTv.text="Mario Score"
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
        binding.pastEventRecyclerView.gone()
        activityViewModel.fetchCriticalInfo()
        viewModel.getMyEvents()
        pastEventAdapter = PastEventAdapter()
        binding.pastEventRecyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            adapter = pastEventAdapter

        }
        pastEventAdapter.submitList(getList())
        bindObserver()
    }

    private fun bindObserver() {

        activityViewModel.getMyProfileResponse.observe(viewLifecycleOwner){
            binding.score.text = it?.points.toString()
            if(it?.points!!>100 && it.points<400){
                binding.level.text="Level: Intermediate"
            }
            else if(it.points>400){
                binding.level.text="Level: Pro"
            }
        }

        viewModel.progressState.observe(viewLifecycleOwner) {
            if (it) {
                activityBinding.binding.linearProgressIndicator.visible()
            } else {
                activityBinding.binding.linearProgressIndicator.gone()
            }
        }

        viewModel.getEventsResponse.observe(viewLifecycleOwner){
            when(it){
                is ServerResult.Failure ->{
                    util.showSnackbar(binding.root,it.exception.message.toString(),20000)
                }
                ServerResult.Progress -> {

                }
                is ServerResult.Success -> {
                    if(it.data.success){
                        binding.shimmerLayout.apply {
                            stopShimmer()
                            visibility = View.GONE
                        }
                        binding.pastEventRecyclerView.visible()
                        pastEventAdapter.submitList(it.data.events)
                        if (binding.swiperefresh.isRefreshing){
                            binding.swiperefresh.isRefreshing = false
                        }
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
        return listOf()
    }
}