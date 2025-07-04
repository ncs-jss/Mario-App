package com.ncs.marioapp.UI.EventDetailsScreen

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.firebase.Timestamp
import com.ncs.marioapp.Domain.HelperClasses.PrefManager
import com.ncs.marioapp.Domain.HelperClasses.Utils
import com.ncs.marioapp.Domain.HelperClasses.Utils.formatToFullDateWithTime
import com.ncs.marioapp.Domain.HelperClasses.Utils.getCurrentTimeFromTrueTime
import com.ncs.marioapp.Domain.Models.Admin.Round
import com.ncs.marioapp.Domain.Models.Events.Event
import com.ncs.marioapp.Domain.Models.Events.EventDetails.EventDetails
import com.ncs.marioapp.Domain.Models.Events.EventDetails.GridDetails
import com.ncs.marioapp.Domain.Models.Events.EventDetails.Mentor
import com.ncs.marioapp.Domain.Models.Events.EventDetails.Submission
import com.ncs.marioapp.Domain.Models.ServerResult
import com.ncs.marioapp.Domain.Utility.Codes
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.gone
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.isNull
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.load
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.runDelayed
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.visible
import com.ncs.marioapp.Domain.Utility.GlobalUtils
import com.ncs.marioapp.R
import com.ncs.marioapp.UI.EventDetailsScreen.Adapters.GridAdapter
import com.ncs.marioapp.UI.EventDetailsScreen.Adapters.GridSpacingItemDecoration
import com.ncs.marioapp.UI.EventDetailsScreen.Adapters.RequirementsAdapter
import com.ncs.marioapp.UI.EventDetailsScreen.Adapters.RoundAdapter
import com.ncs.marioapp.UI.EventDetailsScreen.Adapters.TeamAdapter
import com.ncs.marioapp.UI.EventDetailsScreen.Adapters.TopicsAdapter
import com.ncs.marioapp.UI.MainScreen.MainActivity
import com.ncs.marioapp.databinding.FragmentEventDetailsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Random

@AndroidEntryPoint
class EventDetailsFragment : Fragment(), TeamAdapter.TeamAdapterCallback,
    RoundAdapter.RoundAdapterCallback {

    private var _binding: FragmentEventDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EventDetailsViewModel by activityViewModels()
    private val util: GlobalUtils.EasyElements by lazy {
        GlobalUtils.EasyElements(requireActivity())
    }
    var adapter: RoundAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventDetailsBinding.inflate(inflater, container, false)
        binding.eventEnroll.setOnClickThrottleBounceListener {
            findNavController().navigate(R.id.action_fragment_event_details_to_eventQuestionnaireFragment)
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(0, systemBars.top, 0, systemBars.bottom)
            insets
        }
        observeViewModel()
        setUpViews()
    }


    private fun setUpViews() {

        binding.btnShare.setOnClickThrottleBounceListener {
            util.showSnackbar(binding.root, "Please wait, generating share link", 2000)
            ExtensionsUtil.generateEventShareLink(viewModel.getEvent()!!._id) { link ->
                if (link.isNull) {
                    util.showSnackbar(binding.root, "Something went wrong, try again later", 2000)
                } else {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "Hey there! \n\nCheck out this new event from NCS: $link"
                        )
                    }
                    startActivity(Intent.createChooser(shareIntent, "Share Event via"))
                }
            }
        }

        binding.backkk.setOnClickThrottleBounceListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

    }


    override fun onResume() {
        super.onResume()
        if (viewModel.eventDetails.value.isNull) {
            setUpEventDetails()
        }
    }

    private fun setUpEventDetails() {
        val currentDateTime = Utils.getTrueTime()
        if (currentDateTime != null) {
            viewModel.currentTime = currentDateTime
            Timber.tag("EventDetails").d("onViewCreated: " + viewModel.getEvent()!!._id)
            viewModel.getEventDetails(viewModel.getEvent()!!._id)

            /// Load the rounds for the event.
            viewModel.getAllRoundsForEvent(viewModel.getEvent()!!._id)
            viewModel.getAllSubmissionsForRounds(
                eventID = viewModel.getEvent()!!._id,
                userId = PrefManager.getUserID()!!
            )

        } else {
            GlobalUtils.EasyElements(requireContext())
                .showActionSnackbar(
                    binding.root,
                    "Oops, server error, retry mate..",
                    2000,
                    "Retry"
                ) {
                    setUpEventDetails()
                }
        }
        Timber.tag("TrueTime").d("$currentDateTime")
    }


    private var job: Job? = null
    private fun startUpdatingTimeTask() {
        job = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                val updatedTime = Utils.getTrueTime()
                updatedTime?.let {
                    withContext(Dispatchers.Main) {
                        adapter?.setUpdatedTime(updatedTime)
                    }
                }

                Timber.tag("EventDetails").d("onViewCreated: $updatedTime")
                delay(10000L)
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        onBackPress()
//    }
//
//
//    private fun onBackPress() {
//        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
//            startActivity(Intent(requireContext(), MainActivity::class.java))
//            requireActivity().finish()
//        }
//    }

    private fun observeViewModel() {



        viewModel.roundsListLiveData.observe(viewLifecycleOwner) { eventDetails ->
            when (eventDetails) {
                is ServerResult.Failure -> {}
                ServerResult.Progress -> {}
                is ServerResult.Success -> {
                    Timber.tag("EventDetails").d("onViewCreated: " + eventDetails.data)

                    viewModel.getSubmissionsForEvent.observe(viewLifecycleOwner) { submissionDetails ->
                        when (submissionDetails) {
                            is ServerResult.Failure -> {}
                            ServerResult.Progress -> {}
                            is ServerResult.Success -> {
                                setupRoundsRV(
                                    eventDetails.data.sortedBy { it.seriesNumber },
                                    submissionDetails.data
                                )
                            }
                        }
                    }
                }
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) {
            if (!it.isNull) {
                util.showActionSnackbar(binding.root, it.toString(), 4000, "Retry") {
                    viewModel.getEventDetails(viewModel.getEvent()!!._id)
                }
                viewModel.resetErrorMessage()
            }
        }

        viewModel.progressState.observe(viewLifecycleOwner) {
            if (it) {
                binding.bannerShimmerLayout.apply {
                    startShimmer()
                    visibility = View.VISIBLE
                }
                binding.eventLayout.gone()
            } else {
                ///
            }
        }

        viewModel.eventDetails.observe(viewLifecycleOwner) {
            if (!it.isNull) {
                setUpEventDetails(it)
            }
        }


    }

    private fun setUpEventDetails(eventDetails: EventDetails) {

        Timber.tag("eventDetailsCheck").d(eventDetails.toString())
        binding.eventTitle.text = eventDetails.title
        binding.bannerImage.load(
            eventDetails.banner,
            requireContext().getDrawable(R.drawable.placeholder_image)!!
        )

        setupImportantDetails(eventDetails)

        setupViewListeners(eventDetails.title)

        setReadMoreSpan(eventDetails)

        setUpRequiremntsRV(eventDetails.requirements)

        setUpTopicsRV(eventDetails.topics)

        setUpMentorsRV(eventDetails.mentors, binding.speakersRv, "mentors")

        setUpMentorsRV(eventDetails.contact, binding.pointOfContactRv, "contact")

        val event = viewModel.getEvent()!!
        getMyEvents(eventDetails, event)

        lifecycleScope.launch {
            withContext(Dispatchers.Main){
                viewModel.enrolledCount.observe(viewLifecycleOwner){count->
                    binding.enrolledCount.setText(count.toString())
                    Log.d("checkCount", count!!)
                }
            }
        }

        if (event.enrolled.isEmpty()) {
            binding.profilePic1.setImageResource(R.drawable.apphd)
            binding.profilePic2.setImageResource(R.drawable.logo)
            binding.profilePic3.setImageResource(R.drawable.ncs)
        } else if (event.enrolled.size == 1) {
            binding.profilePic1.setImageResource(R.drawable.apphd)
            binding.profilePic2.setImageResource(R.drawable.logo)
            binding.profilePic3.load(
                event.enrolled[0],
                requireContext().getDrawable(R.drawable.profile_pic_placeholder)!!
            )
        } else if (event.enrolled.size == 2) {
            binding.profilePic1.setImageResource(R.drawable.apphd)
            binding.profilePic2.load(
                event.enrolled[1],
                requireContext().getDrawable(R.drawable.profile_pic_placeholder)!!
            )
            binding.profilePic3.load(
                event.enrolled[0],
                requireContext().getDrawable(R.drawable.profile_pic_placeholder)!!
            )
        } else {
            binding.profilePic1.load(
                event.enrolled[2],
                requireContext().getDrawable(R.drawable.profile_pic_placeholder)!!
            )
            binding.profilePic2.load(
                event.enrolled[1],
                requireContext().getDrawable(R.drawable.profile_pic_placeholder)!!
            )
            binding.profilePic3.load(
                event.enrolled[0],
                requireContext().getDrawable(R.drawable.profile_pic_placeholder)!!
            )
        }


        val score = event.points
        val coins = (score / 5).coerceAtLeast(0)
        binding.cointCount.text = "+${coins}"
        binding.scoreCount.text = "+${score}"


        binding.bannerShimmerLayout.apply {
            stopShimmer()
            visibility = View.GONE
        }
        binding.eventLayout.visible()

    }

    private fun getMyEvents(eventDetails: EventDetails, event: Event) {

        val deadline = eventDetails.deadline
        var isEventOver: Boolean = false

        getCurrentTimeFromTrueTime()?.let { currentTimestamp ->
            val currentTimeMillis = currentTimestamp.time
            isEventOver = currentTimeMillis >= deadline
        } ?: run {
            isEventOver = false
            println("TrueTime is not initialized. Unable to check event status.")
        }

        Timber.tag("isEventOver").d(isEventOver.toString())

        viewModel.getMyEventsResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ServerResult.Failure -> {}
                ServerResult.Progress -> {}
                is ServerResult.Success -> {
                    if (result.data.none { it._id == event._id }) {

                        Timber.tag("Event").d("Not Enrolled")
                        viewModel.isUserEnrolled = 0

                        if (isEventOver) {
                            Timber.tag("Event").d("isEventOver is true")
                            binding.deadlineView.visible()
                            binding.eventEnroll.gone()
                            binding.alreadyEnrolled.gone()
                            binding.notEligible.gone()
                        } else {
                            Timber.tag("Event").d("isEventOver is false")
                            if (!event.isEligibile) {
                                Timber.tag("Event").d("isEligibile is false")
                                binding.notEligible.visible()
                                binding.eventEnroll.gone()
                                binding.alreadyEnrolled.gone()
                                binding.deadlineView.gone()
                            } else {
//                                Timber.tag("Event").d("isEligibile is true")
//                                binding.eventEnroll.visible()
//                                binding.notEligible.gone()
//                                binding.alreadyEnrolled.gone()
//                                binding.deadlineView.gone()

                                getCurrentTimeFromTrueTime()?.let { currentTimestamp ->
                                    val currentTimeMillis = currentTimestamp.time

//                                    val eventTimeStamp = viewModel.getEventStartTimestampValue()

                                    viewModel.eventStartTimeStamp.observe(viewLifecycleOwner){eventTimeStamp->
                                        if (eventTimeStamp == null) {
                                            Log.d("eventtttcheck","Event timestamp is null, enrollment is open.")
                                            binding.eventEnroll.visible()
                                            binding.notEligible.gone()
                                            binding.alreadyEnrolled.gone()
                                            binding.deadlineView.gone()
                                            binding.eventNotYetStartedView.gone()
                                            viewModel.eventEligibleInstitute.observe(viewLifecycleOwner) {eligibleInstitue->
                                                if (!eligibleInstitue.isNull) {
                                                    if (eligibleInstitue == "ALL") {
                                                        binding.instituteNotEligibleView.gone()
                                                    } else if (eligibleInstitue == PrefManager.getUserProfile()?.admitted_to) {
                                                        binding.instituteNotEligibleView.gone()
                                                    } else {
                                                        binding.eventEnroll.gone()
                                                        binding.instituteNotEligibleView.visible()
                                                        binding.eligibleInstitue.text =
                                                            eligibleInstitue
                                                    }
                                                }
                                            }
                                        } else {
                                            val eventTimeMillis = eventTimeStamp.toDate().time

                                            if (currentTimeMillis < eventTimeMillis) {
                                                Log.d("eventtttcheck","Event hasn't started yet.")
                                                val timeDiffMillis = eventTimeMillis - currentTimeMillis
                                                val hoursLeft = timeDiffMillis / (1000 * 60 * 60)
                                                val minutesLeft = (timeDiffMillis / (1000 * 60)) % 60

                                                binding.startTime.text = "$hoursLeft hrs $minutesLeft min"

                                                binding.eventNotYetStartedView.visible()
                                                binding.eventEnroll.gone()
                                                binding.notEligible.gone()
                                                binding.alreadyEnrolled.gone()
                                                binding.deadlineView.gone()
                                            } else {
                                                Log.d("eventtttcheck","Event has started or passed, enrollment is open.")
                                                binding.eventEnroll.visible()
                                                binding.notEligible.gone()
                                                binding.alreadyEnrolled.gone()
                                                binding.deadlineView.gone()
                                                binding.eventNotYetStartedView.gone()

                                                viewModel.eventEligibleInstitute.observe(viewLifecycleOwner) {eligibleInstitue->
                                                    if (!eligibleInstitue.isNull) {
                                                        if (eligibleInstitue == "ALL") {
                                                            binding.instituteNotEligibleView.gone()
                                                        } else if (eligibleInstitue == PrefManager.getUserProfile()?.admitted_to) {
                                                            binding.instituteNotEligibleView.gone()
                                                        } else {
                                                            binding.eventEnroll.gone()
                                                            binding.instituteNotEligibleView.visible()
                                                            binding.eligibleInstitue.text =
                                                                eligibleInstitue
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }


                                } ?: run {
                                }


                            }
                        }
                    } else {
                        Timber.tag("isEventOver").d("Already Enrolled")
                        viewModel.isUserEnrolled = 1

                        binding.alreadyEnrolled.visible()
                        binding.deadlineView.gone()
                        binding.eventEnroll.gone()
                        binding.notEligible.gone()
                    }
                }
            }
        }

    }

    private fun setupImportantDetails(eventDetails: EventDetails) {

        val (formattedDate, formattedTime) = formatTimestamp(eventDetails.time)

        val items = listOf(
            GridDetails(R.drawable.baseline_calendar_today_24, "Date", formattedDate),
            GridDetails(R.drawable.baseline_access_time_24, "Time", formattedTime),
            GridDetails(R.drawable.baseline_location_pin_24, "Venue", eventDetails.venue),
            GridDetails(R.drawable.hour_glass_svgrepo_com, "Duration", eventDetails.duration),
            GridDetails(R.drawable.internships, "Event Type", eventDetails.domain[0])
        )

        val spacing = 10 // Space in pixels (e.g., 16dp converted to pixels)
        val includeEdge = true
        val spacingInPixels = (spacing * resources.displayMetrics.density).toInt()
        val gridLayoutManager = GridLayoutManager(requireContext(), 2)
        binding.gridRV.layoutManager = gridLayoutManager
        binding.gridRV.addItemDecoration(GridSpacingItemDecoration(2, spacingInPixels, includeEdge))
        binding.gridRV.adapter = GridAdapter(items)
    }

    private fun setupRoundsRV(rounds: List<Round>, userSubmission: List<Submission>) {

        if (rounds.isEmpty()) {
            binding.timelineParent.gone()
            return
        }

        val recyclerView = binding.roundsRecyclerView
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager

        rounds.forEach { round ->
            round.startTime =
                round.timeLine[Codes.Event.startCollege].toString().formatToFullDateWithTime()
            round.endTime =
                round.timeLine[Codes.Event.endCollege].toString().formatToFullDateWithTime()

            Log.d("EventDetails", "${round.startTime}-${round.endTime}")
        }

        adapter = RoundAdapter(
            rounds,
            this,
            userSubmission,
            viewModel.isUserEnrolled,
            viewModel.currentTime
        )
        recyclerView.adapter = adapter
        startUpdatingTimeTask()
        adapter!!.setIsUserEnrolled(viewModel.isUserEnrolled)
    }


    private var lastScrollY = 0
    private val scrollThreshold = 50

    private fun setupViewListeners(title: String) {
        binding.scrollview.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->
            if (scrollY > lastScrollY + scrollThreshold) {
                binding.eventTitle.text = "Details"

            } else if (scrollY < lastScrollY - scrollThreshold) {
                binding.eventTitle.text = title
            }
            lastScrollY = scrollY
        })
    }

    var isExpanded = false

    fun getInflatedEnrolledUserCount(count: Int): Int {
        return if (count == 0) Random().nextInt(5) + 1 else (0.2 * count).toInt() + count
    }

    fun setReadMoreSpan(eventDetails: EventDetails) {
        val eventDescTextView = binding.eventDesc
        val fullDescription = eventDetails.description
        val maxLines = 3

        if (isExpanded) {
            val spannableString = SpannableString("$fullDescription Read Less")
            val readLessSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    isExpanded = false
                    setReadMoreSpan(eventDetails)
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = ContextCompat.getColor(requireContext(), R.color.primary)
                    ds.isUnderlineText = false
                }
            }
            spannableString.setSpan(
                readLessSpan,
                fullDescription.length + 1,
                spannableString.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            eventDescTextView.text = spannableString
            eventDescTextView.movementMethod = LinkMovementMethod.getInstance()
        } else {
            // Truncated text with "Read More"
            val truncatedText = if (fullDescription.length > 100) {
                fullDescription.substring(0, fullDescription.lastIndexOf(' ', 100)) + "..."
            } else {
                fullDescription
            }
            val spannableString = SpannableString("$truncatedText Read More")
            val readMoreSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    isExpanded = true
                    setReadMoreSpan(eventDetails)
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = ContextCompat.getColor(requireContext(), R.color.primary)
                    ds.isUnderlineText = false
                }
            }
            spannableString.setSpan(
                readMoreSpan,
                truncatedText.length + 1,
                spannableString.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            eventDescTextView.text = spannableString
            eventDescTextView.movementMethod = LinkMovementMethod.getInstance()
        }
    }

    private fun setUpRequiremntsRV(requirements: List<String>) {
        val recyclerView = binding.requirementsRV
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        val adapter = RequirementsAdapter(requirements)
        recyclerView.adapter = adapter
    }

    private fun setUpTopicsRV(topics: List<String>) {
        val recyclerView = binding.topicsRv
        val layoutManager = FlexboxLayoutManager(context).apply {
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.FLEX_START
            flexWrap = FlexWrap.WRAP
        }
        recyclerView.layoutManager = layoutManager
        val adapter = TopicsAdapter(topics)
        recyclerView.adapter = adapter
    }

    private fun setUpMentorsRV(team: List<Mentor>, recyclerView: RecyclerView, type: String) {
        val layoutManager = FlexboxLayoutManager(context).apply {
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.FLEX_START
            flexWrap = FlexWrap.WRAP
        }
        recyclerView.layoutManager = layoutManager
        val adapter = TeamAdapter(team, this, type)
        recyclerView.adapter = adapter
    }

    fun formatTimestamp(timestamp: Long): Pair<String, String> {
        val date = Date(timestamp)
        val dateFormat = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val formattedDate = dateFormat.format(date)
        val formattedTime = timeFormat.format(date)
        return Pair(formattedDate, formattedTime)
    }

    override fun onTeamMemberClicked(mentor: Mentor, type: String) {
        if (type == "contact") {
            openWhatsAppChat("91${mentor.wa_num}")
        } else {
            openUrl(mentor.linkedin_url)
        }
    }


    fun openWhatsAppChat(phoneNumber: String) {
        val formattedNumber = "https://wa.me/$phoneNumber"

        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(formattedNumber)
                setPackage("com.whatsapp")
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "WhatsApp is not installed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    override fun onFormButtonClick(round: Round) {

        val questionnaireID = round.questionnaireID
        val bundle = Bundle().apply {
            putString("questionnaireID", questionnaireID)
        }
        viewModel.setRoundId(round.roundID)
        viewModel.setQuestionnaireId(round.questionnaireID)
        findNavController().navigate(
            R.id.action_fragment_event_details_to_fragment_round_questionnaire,
            bundle
        )
    }


}