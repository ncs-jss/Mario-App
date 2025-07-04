package com.ncs.marioapp.UI.MainScreen.Home

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.ncs.marioapp.Domain.HelperClasses.IRevealListener
import com.ncs.marioapp.Domain.HelperClasses.PrefManager
import com.ncs.marioapp.Domain.HelperClasses.ReviewPreferenceManager
import com.ncs.marioapp.Domain.HelperClasses.ScratchView
import com.ncs.marioapp.Domain.Models.Banner
import com.ncs.marioapp.Domain.Models.Events.AnswerPollBody
import com.ncs.marioapp.Domain.Models.Events.Event
import com.ncs.marioapp.Domain.Models.Events.ParticipatedEvent
import com.ncs.marioapp.Domain.Models.Events.Poll
import com.ncs.marioapp.Domain.Models.Posts.LikePostBody
import com.ncs.marioapp.Domain.Models.Posts.Post
import com.ncs.marioapp.Domain.Models.ServerResult
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.gone
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.isNull
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.performHapticFeedback
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.showProgressDialog
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.toast
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.visible
import com.ncs.marioapp.Domain.Utility.GlobalUtils
import com.ncs.marioapp.R
import com.ncs.marioapp.UI.EventDetailsScreen.EventDetailsActivity
import com.ncs.marioapp.UI.MainScreen.Home.Adapters.BannerAdapter
import com.ncs.marioapp.UI.MainScreen.Home.Adapters.EventsAdapter
import com.ncs.marioapp.UI.MainScreen.Home.Adapters.ListItem
import com.ncs.marioapp.UI.MainScreen.Home.Adapters.PostAdapter
import com.ncs.marioapp.UI.MainScreen.MainActivity
import com.ncs.marioapp.UI.MainScreen.MainViewModel
import com.ncs.marioapp.databinding.FragmentHomeBinding
import com.ncs.marioapp.databinding.TicketDialogBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class HomeFragment : Fragment(), EventsAdapter.Callback, PostAdapter.CallBack, EventActionBottomSheet.Callback, BannerAdapter.Callback, ClaimCouponBottomSheet.Callback {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var currentPosition = Int.MAX_VALUE / 2
    private val delayMillis: Long = 3000
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var adapter: PostAdapter
    lateinit var eventsAdapter: EventsAdapter
    private val activityBinding: MainActivity by lazy {
        (requireActivity() as MainActivity)
    }
    private val viewModel: HomeViewModel by activityViewModels()
    private val activityViewModel : MainViewModel by activityViewModels()
    private val util: GlobalUtils.EasyElements by lazy {
        GlobalUtils.EasyElements(requireActivity())
    }
    private var lastRefreshTime = 0L
    private var backPressedTime: Long = 0
    lateinit var loadingdialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.swiperefresh.setOnRefreshListener {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastRefreshTime > 2000) {
                activityViewModel.fetchCriticalInfo()
                loadHomeData()
                lastRefreshTime = currentTime
            }
        }
        setViews()

        val reviewPrefManager = ReviewPreferenceManager(requireContext())

        if (reviewPrefManager.shouldShowReviewDialog()) {
            Log.d("ReviewReq","Requesting Review now...")
            val bottomSheet = ReviewRequestBottomSheet()
            bottomSheet.show(childFragmentManager, bottomSheet.tag)
        }

    }

    fun requestReview(activity: Activity) {
        val manager: ReviewManager = ReviewManagerFactory.create(activity)
        val request = manager.requestReviewFlow()

        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result
                val flow = manager.launchReviewFlow(activity, reviewInfo)
                flow.addOnCompleteListener {
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        startAutoScroll()
        activityBinding.binding.actionbar.titleTv.text="Mario"
        activityBinding.binding.actionbar.root.visible()
        activityBinding.binding.bottomNavigationView.visible()
        activityBinding.binding.linearProgressIndicator.visible()
        loadHomeData()

    }

    private fun loadHomeData() {
        CoroutineScope(Dispatchers.Main).launch {
            delay(100)
            viewModel.getHomePageItems()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        stopAutoScroll()
        _binding = null
    }

    override fun onStop() {
        super.onStop()
        stopAutoScroll()
    }


    private fun setViews(){
        binding.postsShimmerLayout.apply {
            startShimmer()
            visibility = View.VISIBLE
        }

        binding.recyclerViewPosts.gone()
        setUpPostsRV(mutableListOf())

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

        observeViewModel()
        setUpViews()

    }

    private fun observeViewModel(){

        activityViewModel.validateScannedQR.observe(requireActivity()){result ->
            if (!result.isNull) {
                when (result) {
                    is ServerResult.Failure -> {
                        activityBinding.binding.linearProgressIndicator.gone()
                        showError(result.message)
                        setUpLoader(false, "")
                    }

                    ServerResult.Progress -> {
                        activityBinding.binding.linearProgressIndicator.visible()
                        setUpLoader(true, "Please wait, validating the coupon!")
                    }

                    is ServerResult.Success -> {
                        activityBinding.binding.linearProgressIndicator.gone()
                        toast("Scratch this card to claim your coins!")
                        setUpLoader(false, "")
                        result.data?.points?.let { showScratchCardPopup(it) }
                    }

                    null -> {}
                }
            }
        }

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
                util.showActionSnackbar(binding.root, it.toString(), 4000, "Retry", {
                    loadHomeData()
                })
                viewModel.resetErrorMessage()
            }
        }
        viewModel.banners.observe(viewLifecycleOwner){banners->
            setupBannerRecyclerView(banners.distinctBy { it._id }.sortedByDescending { it.createdAt })
            startAutoScroll()
        }
        viewModel.progressState.observe(viewLifecycleOwner) {
            if (it) {
                activityBinding.binding.linearProgressIndicator.visible()
            } else {
                activityBinding.binding.linearProgressIndicator.gone()
            }
        }
        viewModel.getEventsResponse.observe(viewLifecycleOwner){result->
            when(result){
                is ServerResult.Failure -> {
                    util.showSnackbar(binding.root, result.message, 2000)
                }
                ServerResult.Progress -> {}
                is ServerResult.Success -> {
                    val events=result.data.sortedByDescending { it.createdAt }.distinctBy { it._id }

                    val eventFromDeeplink=PrefManager.getEventIdByDeeplink()
                    if (eventFromDeeplink!=null){
                        PrefManager.setEventIdByDeeplink(null)
                        val event=events.firstOrNull { it._id==eventFromDeeplink }
                        val intent = Intent(requireContext(), EventDetailsActivity::class.java)
                        intent.putExtra("event_data", event)
                        startActivity(intent)
                        requireActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
                    }


                    val requiredEvents = if (events.size > 3) {
                        events.subList(0, 3)
                    } else {
                        events
                    }
                    setEventsRecyclerView(requiredEvents)
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

        viewModel.polls.observe(viewLifecycleOwner) { polls ->
            Log.d("chckk", "observeViewModel: $polls")
            val posts: MutableList<ListItem> = mutableListOf()
            for (poll in polls) {
                posts.add(ListItem.Poll(title = poll.question, poll = poll))
            }

            viewModel.posts.observe(viewLifecycleOwner) { postItems ->
                val _posts: MutableList<ListItem> = mutableListOf()
                for (post in postItems) {
                    _posts.add(ListItem.Post(post = post))
                }
                val combinedList = (posts + _posts).sortedByDescending {
                    when (it) {
                        is ListItem.Poll -> it.poll.createdAt
                        is ListItem.Post -> it.post.createdAt
                    }
                }
                val topPosts = combinedList.take(5)
                adapter.submitList(topPosts)
                binding.postsShimmerLayout.apply {
                    stopShimmer()
                    visibility = View.GONE
                }
                if(combinedList.isEmpty()){
                    binding.postTv.visible()
                }else{
                    binding.recyclerViewPosts.visible()
                    if (binding.swiperefresh.isRefreshing){
                        binding.swiperefresh.isRefreshing = false
                    }
                }
            }
        }

        val couponFromDeeplink=PrefManager.getCouponIdByDeeplink()
        if (couponFromDeeplink!=null){
            PrefManager.setCouponIdByDeeplink(null)
            Log.d("couponCheck", couponFromDeeplink)
            val bottomSheet = ClaimCouponBottomSheet(couponFromDeeplink, this)
            bottomSheet.show(childFragmentManager, bottomSheet.tag)
        }



    }

    private fun addUserEvents(events:List<ParticipatedEvent>){
        eventsAdapter.userEvents(events)
    }

    private fun setUpViews(){

        binding.viewAllEvents.setOnClickThrottleBounceListener {
            findNavController().navigate(R.id.fragment_events)
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

        binding.discord.setOnClickThrottleBounceListener{
            openUrl("https://discord.com/invite/7YuTnNyKFv")
        }

        binding.whatsapp.setOnClickThrottleBounceListener{
            openUrl("https://chat.whatsapp.com/HkJHTJuNpNz5zJNBi7tKcl")
        }

        binding.github.setOnClickThrottleBounceListener{
            openUrl("https://github.com/ncs-jss")
        }

    }

    private fun showScratchCardPopup(point: Int) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.scratch_card_popup)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val parent = dialog.findViewById<ConstraintLayout>(R.id.screenparent)
        val scratchView = dialog.findViewById<ScratchView>(R.id.scratchView)
        val bg = dialog.findViewById<LottieAnimationView>(R.id.bganim)
        val anim = dialog.findViewById<LottieAnimationView>(R.id.success_anim)
        val coins = dialog.findViewById<TextView>(R.id.coins)
        val coinAnim = dialog.findViewById<LottieAnimationView>(R.id.coin_anim)
        val points = dialog.findViewById<TextView>(R.id.points)
        parent.isEnabled = false
        parent.isClickable = false
        parent.setOnClickThrottleBounceListener {
            dialog.dismiss()
            activityViewModel.fetchCriticalInfo()
            activityViewModel.resetValidateScannedQR()
        }
        coins.text = "${point / 5}"
        points.text = "$point"
        scratchView.setStrokeWidth(10)
        scratchView.setRevealListener(object : IRevealListener {
            override fun onRevealed(scratchView: ScratchView) {
                anim.visibility = View.VISIBLE
                coinAnim.playAnimation()
                bg.playAnimation()
                anim.playAnimation()
                parent.isEnabled = true
                parent.isClickable = true
                activityViewModel.resetValidateScannedQR()
                activityViewModel.fetchCriticalInfo()
            }

            override fun onRevealPercentChangedListener(scratchView: ScratchView, percent: Float) {
                if (percent > 0.35f) {
                    scratchView.reveal()

                }
            }
        })

        dialog.show()
    }
    private fun showError(message: String?) {
        util.showSnackbar(binding.root, "OOPS! The coupon is already claimed now", 2000)
    }

    private fun setUpLoader(show: Boolean, message: String) {
        if (show) {
            if (this::loadingdialog.isInitialized && loadingdialog.isShowing) {
                loadingdialog.dismiss()
            }
            loadingdialog = showProgressDialog(requireContext(), message)
            loadingdialog.show()
        } else {
            if (this::loadingdialog.isInitialized && loadingdialog.isShowing) {
                loadingdialog.dismiss()
            }
        }
    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    private fun setupBannerRecyclerView(list: List<Banner>) {
        binding.bannerShimmerLayout.apply {
            stopShimmer()
            visibility = View.GONE
        }
        if (list.isEmpty()){
            binding.bannerTv.visible()
        }
        else{
            binding.bannerRecyclerView.visible()

            if (binding.bannerRecyclerView.onFlingListener == null) {
                LinearSnapHelper().attachToRecyclerView(binding.bannerRecyclerView)
            }

            binding.bannerRecyclerView.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = BannerAdapter(list, this@HomeFragment)
                scrollToPosition(currentPosition)
                addOnScrollListener(autoScrollListener)
            }
        }
    }


    private fun setEventsRecyclerView(events:List<Event>){
        binding.eventsShimmerLayout.apply {
            stopShimmer()
            visibility = View.GONE
        }
        if (events.isEmpty()){
            binding.eventTv.visible()
        }
        else{
            binding.EventsRecyclerView.visible()
            val recyclerView = binding.EventsRecyclerView
            val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            recyclerView.setItemViewCacheSize(events.size)
            recyclerView.layoutManager = layoutManager
            eventsAdapter= EventsAdapter(events, this)
            recyclerView.adapter = eventsAdapter
        }
    }

    private fun setUpPostsRV(posts:List<ListItem>){
        adapter = PostAdapter( this)
        binding.recyclerViewPosts.setItemViewCacheSize(4)
        binding.recyclerViewPosts.itemAnimator = null
        binding.recyclerViewPosts.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewPosts.adapter = adapter
    }

    private val autoScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            when (newState) {
                RecyclerView.SCROLL_STATE_IDLE -> startAutoScroll()
                else -> stopAutoScroll()
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        onBackPress()
    }

    private fun onBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            val currentTime = System.currentTimeMillis()

            if (currentTime - backPressedTime < 2000) {
                requireActivity().finish()
            } else {
                util.showSnackbar(binding.root,"Press back again to exit", 2000)
                backPressedTime = currentTime
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

    override fun onClick(event: Event, isEnrolled: Boolean, enrolledCount:String) {
        if (isEnrolled){
            val bottomSheet = EventActionBottomSheet(event,"Unenroll", this, enrolledCount)
            bottomSheet.show(childFragmentManager, bottomSheet.tag)
        }
        else{
            val bottomSheet = EventActionBottomSheet(event,"Enroll", this, enrolledCount)
            bottomSheet.show(childFragmentManager, bottomSheet.tag)
        }
    }

    override fun onEnroll(event: Event) {
//        viewModel.enrollUser(event._id)
    }

    override fun onUnenroll(event: Event) {
//        viewModel.unenrollUser(event._id)
    }

    override fun onMoreDetails(event: Event, enrolledCount: String) {
        val intent = Intent(requireContext(), EventDetailsActivity::class.java)
        intent.putExtra("event_data", event)
        intent.putExtra("enrolled_count", enrolledCount)
        startActivity(intent)
        requireActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
    }

    override fun onGetTicketClick(event: Event) {
        if (event.venue=="Online"){
            val bottomSheet = OnlineEventMeetLinkRequestBottomSheet()
            bottomSheet.show(childFragmentManager, bottomSheet.tag)
        }
        else{
            showTicketDialog(requireContext(), event)
        }
    }

    override fun onCheckBoxClick(poll: Poll, selectedOption:String) {
        viewModel.answerPoll(AnswerPollBody(poll_id = poll._id, option = selectedOption))
    }

    override fun onLikeClick(post: Post, isLiked: Boolean) {
        if (!isLiked){
            requireContext().performHapticFeedback()
            viewModel.likePost(LikePostBody(post_id = post._id, action = "LIKE"))
            val updatedPost = post.copy(
                likes = post.likes + 1,
                liked = true,
                image = post.image ?: "default_image_url"
            )
            adapter.updatePost(updatedPost)

            viewModel.likeResult.observe(viewLifecycleOwner) { success ->
                if (!success.isNull && !success!!) {
                    val update = post.copy(
                        likes = (post.likes).coerceAtLeast(0),
                        liked = true,
                        image = post.image ?: "default_image_url"
                    )
                    adapter.updatePost(update)
                    Snackbar.make(
                        binding.root,
                        "Unable to like post, try again.",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    viewModel.resetLikeResult()
                }
            }
        }
        else {

            val updatedPost = post.copy(
                likes = (post.likes - 1).coerceAtLeast(0),
                liked = false,
                image = post.image ?: "default_image_url"
            )

            adapter.updatePost(updatedPost)
            viewModel.likePost(LikePostBody(post_id = post._id, action = "UNLIKE"))
            viewModel.unlikeResult.observe(viewLifecycleOwner){ success->
                if (!success.isNull && !success!!) {
                    val updatedPost = post.copy(
                        likes = post.likes.coerceAtLeast(0),
                        liked = false,
                        image = post.image ?: "default_image_url"
                    )
                    adapter.updatePost(updatedPost)
                    Snackbar.make(
                        binding.root,
                        "Unable to dislike post, try again.",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    viewModel.resetUnlikeResult()
                }
            }
        }
    }

    override fun onShareClick(post: Post, bitmap: Bitmap?) {
        util.showSnackbar(binding.root, "Please wait, generating share link", 2000)
        if (post.image.isNullOrEmpty()) {
            util.showSnackbar(binding.root, "Something went wrong, try again later", 2000)
        }
        else{
            ExtensionsUtil.generateShareLink(post._id) { link ->
                if (link.isNull) {
                    util.showSnackbar(binding.root, "Something went wrong, try again later", 2000)
                } else {
                    sharePost(bitmap, post, link.toString())
                }
            }
        }
    }

    private fun downloadImage(imageUrl: String, callback: (Bitmap?) -> Unit) {
        Thread {
            try {
                val url = URL(imageUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val input = connection.inputStream
                val bitmap = BitmapFactory.decodeStream(input)
                callback(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
                callback(null)
            }
        }.start()
    }

    private fun sharePost(bitmap: Bitmap?, post: Post, link:String) {
        if (!bitmap.isNull) {
            val cachePath = File(requireContext().filesDir, "images")
            cachePath.mkdirs()
            val stream = FileOutputStream(File(cachePath, "shared_image.png"))
            bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()
            val imagePath = File(cachePath, "shared_image.png")
            val imageUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.provider",
                imagePath
            )
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, imageUri)
                putExtra(Intent.EXTRA_TEXT, "${post.caption}\n\n$link")
                type = "image/png"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            requireContext().startActivity(Intent.createChooser(shareIntent, "Share news article"))
        }
        else{
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Hey there! \n\nCheck out this new post from NCS: ${post.caption}\n\n$link"
                )
            }
            startActivity(Intent.createChooser(shareIntent, "Share Event via"))
        }

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

    override fun onBannerClick(banner: Banner) {
        if(banner.type =="link"){
            openUrl(banner.link)
        }
        else{
            viewModel.getStory(banner.storyId){
                if (!it.isNull){
                    openStoryFragment(it?.storyText!!)
                }
            }
        }
    }

    private fun openStoryFragment(text:String){
        val bundle=Bundle()
        bundle.putString("storyText", text)
        findNavController().navigate(R.id.action_fragment_home_to_fragment_story_main, bundle)
    }

    override fun onClaimCLick(couponCode: String) {
        activityViewModel.validateScannedQR(couponCode)
    }


}