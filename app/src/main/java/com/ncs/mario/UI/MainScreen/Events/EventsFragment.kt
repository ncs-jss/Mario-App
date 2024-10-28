package com.ncs.mario.UI.MainScreen.Events

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.GravityCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ncs.mario.Domain.Models.Events.Event
import com.ncs.mario.Domain.Models.Events.ParticipatedEvent
import com.ncs.mario.Domain.Models.ServerResult
import com.ncs.mario.Domain.Utility.ExtensionsUtil.gone
import com.ncs.mario.Domain.Utility.ExtensionsUtil.isNull
import com.ncs.mario.Domain.Utility.ExtensionsUtil.visible
import com.ncs.mario.Domain.Utility.GlobalUtils
import com.ncs.mario.R
import com.ncs.mario.UI.MainScreen.Home.Adapters.EventsAdapter
import com.ncs.mario.UI.MainScreen.Home.EventActionBottomSheet
import com.ncs.mario.UI.MainScreen.Home.HomeViewModel
import com.ncs.mario.UI.MainScreen.MainActivity
import com.ncs.mario.UI.MainScreen.MainViewModel
import com.ncs.mario.databinding.FragmentEventsBinding
import com.ncs.mario.databinding.TicketDialogBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class EventsFragment : Fragment(), EventsAdapter.Callback, EventActionBottomSheet.Callback {

    lateinit var binding: FragmentEventsBinding
    private val activityBinding: MainActivity by lazy {
        (requireActivity() as MainActivity)
    }
    lateinit var eventsAdapter: EventsAdapter
    private val viewModel: HomeViewModel by activityViewModels()
    private val util: GlobalUtils.EasyElements by lazy {
        GlobalUtils.EasyElements(requireActivity())
    }
    private val activityViewModel : MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEventsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        activityBinding.binding.actionbar.btnHam.setImageResource(R.drawable.ham)
        activityBinding.binding.actionbar.score.visible()
        activityBinding.binding.actionbar.titleTv.text="Events"

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
        binding.eventsShimmerLayout.apply {
            startShimmer()
            visibility = View.VISIBLE
        }
        binding.recyclerView.gone()

        binding.swiperefresh.setOnRefreshListener {
            binding.eventsShimmerLayout.apply {
                startShimmer()
                visibility = View.VISIBLE
            }
            binding.recyclerView.gone()
            activityViewModel.fetchCriticalInfo()

            CoroutineScope(Dispatchers.IO).launch {
                viewModel.getEvents()
                viewModel.getMyEvents()
            }
        }

        observeViewModel()
        setUpViews()
    }

    private fun setUpViews(){

    }

    private fun observeViewModel(){

        viewModel.enrollResult.observe(viewLifecycleOwner){
            if (it){
                activityViewModel.fetchCriticalInfo()
            }
        }

        viewModel.unenrollResult.observe(viewLifecycleOwner){
            if (it){
                activityViewModel.fetchCriticalInfo()
            }
        }

        viewModel.normalErrorMessage.observe(viewLifecycleOwner){
            if (!it.isNull) {
                util.showSnackbar(binding.root, it.toString(), 2000)
                viewModel.resetErrorMessage()
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner){
            if (!it.isNull) {
                util.showActionSnackbar(binding.root, it.toString(), 2000, "Retry", {
                    requireActivity().recreate()
                })
                viewModel.resetErrorMessage()
            }
        }

        viewModel.progressState.observe(viewLifecycleOwner) {
            if (it) {
                activityBinding.binding.linearProgressIndicator.visible()
            } else {
                activityBinding.binding.linearProgressIndicator.gone()
            }
        }

        viewModel.getEventsResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ServerResult.Failure -> {}
                ServerResult.Progress -> {}
                is ServerResult.Success -> {

                    if (binding.swiperefresh.isRefreshing){
                        binding.swiperefresh.isRefreshing = false
                    }

                    binding.eventsShimmerLayout.apply {
                        startShimmer()
                        visibility = View.GONE
                    }
                    binding.recyclerView.visible()

                    val events = result.data
                    setEventsRecyclerView(events)
                    viewModel.getMyEventsResponse.observe(viewLifecycleOwner) { result ->
                        when (result) {
                            is ServerResult.Failure -> {}
                            ServerResult.Progress -> {}
                            is ServerResult.Success -> {
                                addUserEvents(result.data)
                            }
                        }
                    }
                }
            }
        }

    }

    private fun addUserEvents(events:List<ParticipatedEvent>){
        if (this::eventsAdapter.isInitialized) {
            eventsAdapter.userEvents(events)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        onBackPress()
    }

    private fun setEventsRecyclerView(events:List<Event>){
        val recyclerView = binding.recyclerView
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        eventsAdapter= EventsAdapter(events, this)
        recyclerView.adapter = eventsAdapter
    }

    private fun onBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            moveToPrevious()
        }
    }

    fun moveToPrevious(){
        findNavController().navigate(R.id.action_fragment_events_to_fragment_home)
    }

    override fun onClick(event: Event, isEnrolled: Boolean) {
        if (isEnrolled){
            val bottomSheet = EventActionBottomSheet(event,"Unenroll", this)
            bottomSheet.show(childFragmentManager, bottomSheet.tag)
        }
        else{
            val bottomSheet = EventActionBottomSheet(event,"Enroll", this)
            bottomSheet.show(childFragmentManager, bottomSheet.tag)
        }
    }

    override fun onGetTicketClick(event: Event) {
        showTicketDialog(requireContext(), event)
    }

    fun showTicketDialog(context: Context, event: Event): Dialog {
        val binding = TicketDialogBinding.inflate(LayoutInflater.from(context))

        viewModel.resetTicketResult()
        viewModel.getTicket(event._id)
        viewModel.ticketResultBitmap.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.ticketShimmerLayout.apply {
                    stopShimmer()
                    visibility = View.GONE
                }
                binding.normalTicketLayout.visible()
                binding.qr.setImageBitmap(it)
            } else {
                binding.ticketShimmerLayout.apply {
                    startShimmer()
                    visibility = View.VISIBLE
                }
                binding.normalTicketLayout.gone()
            }
        }

        binding.title.text = event.title
        if (event.time.isNullOrEmpty()) {
            binding.time.text = "TBA"
            binding.date.text = "TBA"
        } else {
            val (formattedDate, formattedTime) = formatTimestamp(event.time.toLong())
            binding.time.text = formattedTime
            binding.date.text = formattedDate
        }
        binding.venue.text = event.venue

        val dialog = AlertDialog.Builder(context)
            .setView(binding.root)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.show()

        val width = context.resources.getDimensionPixelSize(R.dimen.dialog_width)
        val height = context.resources.getDimensionPixelSize(R.dimen.dialog_height)
        dialog.window?.setLayout(width, height)
        return dialog
    }

    fun formatTimestamp(timestamp: Long): Pair<String, String> {
        val date = Date(timestamp)
        val dateFormat = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val formattedDate = dateFormat.format(date)
        val formattedTime = timeFormat.format(date)
        return Pair(formattedDate, formattedTime)
    }

    override fun onEnroll(event: Event) {
        viewModel.enrollUser(event._id)
    }

    override fun onUnenroll(event: Event) {
        viewModel.unenrollUser(event._id)
    }

}