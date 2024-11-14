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
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.firebase.Timestamp
import com.ncs.marioapp.Domain.HelperClasses.Utils.formatToFullDateWithTime
import com.ncs.marioapp.Domain.Models.Admin.Round
import com.ncs.marioapp.Domain.Models.Events.EventDetails.EventDetails
import com.ncs.marioapp.Domain.Models.Events.EventDetails.Mentor
import com.ncs.marioapp.Domain.Models.ServerResult
import com.ncs.marioapp.Domain.Utility.Codes
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.gone
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.isNull
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.load
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.visible
import com.ncs.marioapp.Domain.Utility.GlobalUtils
import com.ncs.marioapp.R
import com.ncs.marioapp.UI.EventDetailsScreen.Adapters.RequirementsAdapter
import com.ncs.marioapp.UI.EventDetailsScreen.Adapters.RoundAdapter
import com.ncs.marioapp.UI.EventDetailsScreen.Adapters.TeamAdapter
import com.ncs.marioapp.UI.EventDetailsScreen.Adapters.TopicsAdapter
import com.ncs.marioapp.UI.MainScreen.MainActivity
import com.ncs.marioapp.databinding.FragmentEventDetailsBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Random

@AndroidEntryPoint
class EventDetailsFragment : Fragment(), TeamAdapter.TeamAdapterCallback {

    private var _binding : FragmentEventDetailsBinding? =null
    private val binding get() = _binding!!
    private val viewModel: EventDetailsViewModel by activityViewModels()
    private val util: GlobalUtils.EasyElements by lazy {
        GlobalUtils.EasyElements(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEventDetailsBinding.inflate(inflater, container, false)
        binding.eventEnroll.setOnClickThrottleBounceListener{
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
        if (viewModel.eventDetails.value.isNull) {

            Log.d("EventDetails", "onViewCreated: ${viewModel.getEvent()!!._id}")
            viewModel.getEventDetails(viewModel.getEvent()!!._id)
            viewModel.getAllRoundsForEvent(viewModel.getEvent()!!._id)
        }
        observeViewModel()
        setUpViews()
    }

    private fun setUpViews(){

        binding.btnShare.setOnClickThrottleBounceListener {
            util.showSnackbar(binding.root, "Please wait, generating share link", 2000)
            ExtensionsUtil.generateEventShareLink(viewModel.getEvent()!!._id) { link ->
                if (link.isNull) {
                    util.showSnackbar(binding.root, "Something went wrong, try again later", 2000)
                } else {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, "Hey there! \n\nCheck out this new event from NCS: $link")
                    }
                    startActivity(Intent.createChooser(shareIntent, "Share Event via"))
                }
            }
        }

        binding.backkk.setOnClickThrottleBounceListener {
            startActivity(Intent(requireContext(), MainActivity::class.java))
            requireActivity().finish()
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        onBackPress()
    }



    private fun onBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            startActivity(Intent(requireContext(), MainActivity::class.java))
            requireActivity().finish()
        }
    }

    private fun observeViewModel(){

        viewModel.roundsListLiveData.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ServerResult.Failure -> {}
                ServerResult.Progress -> {}
                is ServerResult.Success -> {
                    Log.d("EventDetails", "onViewCreated: ${result.data}")
                    setupRoundsRV(result.data)
                }
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner){
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
                binding.bannerShimmerLayout.apply {
                    stopShimmer()
                    visibility = View.GONE
                }
                binding.eventLayout.visible()
            }
        }

        viewModel.eventDetails.observe(viewLifecycleOwner){
            if (!it.isNull){
                setUpEventDetails(it)
            }
        }
    }

    private fun setUpEventDetails(eventDetails: EventDetails){
        Log.d("eventDetailsCheck", eventDetails.toString())
        binding.eventTitle.text = eventDetails.title
        binding.bannerImage.load(eventDetails.banner, requireContext().getDrawable(R.drawable.placeholder_image)!!)
        val (formattedDate, formattedTime) = formatTimestamp(eventDetails.time)
        binding.eventDate.text = formattedDate
        binding.eventTime.text = formattedTime
        binding.eventVenue.text = eventDetails.venue
        binding.eventDuration.text = eventDetails.duration
        binding.eventType.text = eventDetails.domain[0]

        setupViewListeners(eventDetails.title)

        setReadMoreSpan(eventDetails)

        setUpRequiremntsRV(eventDetails.requirements)

        setUpTopicsRV(eventDetails.topics)

        setUpMentorsRV(eventDetails.mentors, binding.speakersRv, "mentors")

        setUpMentorsRV(eventDetails.contact, binding.pointOfContactRv, "contact")

        val event=viewModel.getEvent()!!
        val deadline = eventDetails.deadline
        val isEventOver: Boolean

        Timestamp.now().let { currentTimestamp ->
            val currentTime = currentTimestamp.seconds * 1000
            isEventOver = currentTime >= deadline
        }

        Log.d("isEventOver", isEventOver.toString())

        viewModel.getMyEventsResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ServerResult.Failure -> {}
                ServerResult.Progress -> {}
                is ServerResult.Success -> {
                    if (result.data.filter { it._id==event._id }.isNullOrEmpty()){
                        Log.d("isEventOver", "Not Enrolled")
                        if (isEventOver){
                            Log.d("isEventOver", "isEventOver is true")
                            binding.deadlineView.visible()
                            binding.eventEnroll.gone()
                            binding.alreadyEnrolled.gone()
                            binding.notEligible.gone()
                        }
                        else{
                            Log.d("isEventOver", "isEventOver is false")
                            if (!event.isEligibile){
                                Log.d("isEventOver", "isEligibile is false")
                                binding.notEligible.visible()
                                binding.eventEnroll.gone()
                                binding.alreadyEnrolled.gone()
                                binding.deadlineView.gone()
                            }
                            else{
                                Log.d("isEventOver", "isEligibile is true")
                                binding.eventEnroll.visible()
                                binding.notEligible.gone()
                                binding.alreadyEnrolled.gone()
                                binding.deadlineView.gone()
                            }
                        }
                    }
                    else{
                        Log.d("isEventOver", "Already Enrolled")
                        binding.alreadyEnrolled.visible()
                        binding.deadlineView.gone()
                        binding.eventEnroll.gone()
                        binding.notEligible.gone()
                    }
                }
            }
        }


        if (event.enrolled.isEmpty()){
            binding.profilePic1.setImageResource(R.drawable.apphd)
            binding.profilePic2.setImageResource(R.drawable.logo)
            binding.profilePic3.setImageResource(R.drawable.ncs)
        }
        else if (event.enrolled.size==1){
            binding.profilePic1.setImageResource(R.drawable.apphd)
            binding.profilePic2.setImageResource(R.drawable.logo)
            binding.profilePic3.load(event.enrolled[0],requireContext().getDrawable(R.drawable.profile_pic_placeholder)!!)
        }
        else if (event.enrolled.size==2){
            binding.profilePic1.setImageResource(R.drawable.apphd)
            binding.profilePic2.load(event.enrolled[1],requireContext().getDrawable(R.drawable.profile_pic_placeholder)!!)
            binding.profilePic3.load(event.enrolled[0],requireContext().getDrawable(R.drawable.profile_pic_placeholder)!!)
        }
        else{
            binding.profilePic1.load(event.enrolled[2],requireContext().getDrawable(R.drawable.profile_pic_placeholder)!!)
            binding.profilePic2.load(event.enrolled[1],requireContext().getDrawable(R.drawable.profile_pic_placeholder)!!)
            binding.profilePic3.load(event.enrolled[0],requireContext().getDrawable(R.drawable.profile_pic_placeholder)!!)
        }

        binding.enrolledCount.text="${getInflatedEnrolledUserCount(event.enrolledCount)} +"

        val score=event.points
        val coins = (score / 5).coerceAtLeast(0)
        binding.cointCount.text="+${coins}"
        binding.scoreCount.text="+${score}"

    }

    private fun setupRoundsRV(rounds: List<Round>) {
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
        val adapter = RoundAdapter(rounds)
        recyclerView.adapter = adapter
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

    fun getInflatedEnrolledUserCount(count:Int):Int{
        return if (count==0) Random().nextInt(5)+1 else (0.2 * count).toInt()+count
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
            spannableString.setSpan(readLessSpan, fullDescription.length + 1, spannableString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
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
            spannableString.setSpan(readMoreSpan, truncatedText.length + 1, spannableString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            eventDescTextView.text = spannableString
            eventDescTextView.movementMethod = LinkMovementMethod.getInstance()
        }
    }

    private fun setUpRequiremntsRV(requirements:List<String>){
        val recyclerView = binding.requirementsRV
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        val adapter= RequirementsAdapter(requirements)
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
        if (type=="contact"){
            openWhatsAppChat("91${mentor.wa_num}")
        }
        else{
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

}