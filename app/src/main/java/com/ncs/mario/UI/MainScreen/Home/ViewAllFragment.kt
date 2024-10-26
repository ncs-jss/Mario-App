package com.ncs.mario.UI.MainScreen.Home

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ncs.mario.Domain.Models.Events.AnswerPollBody
import com.ncs.mario.Domain.Models.Events.Event
import com.ncs.mario.Domain.Models.Events.ParticipatedEvent
import com.ncs.mario.Domain.Models.Events.Poll
import com.ncs.mario.Domain.Models.Posts.LikePostBody
import com.ncs.mario.Domain.Models.Posts.Post
import com.ncs.mario.Domain.Models.ServerResult
import com.ncs.mario.Domain.Utility.ExtensionsUtil
import com.ncs.mario.Domain.Utility.ExtensionsUtil.gone
import com.ncs.mario.Domain.Utility.ExtensionsUtil.isNull
import com.ncs.mario.Domain.Utility.ExtensionsUtil.performHapticFeedback
import com.ncs.mario.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.mario.Domain.Utility.ExtensionsUtil.visible
import com.ncs.mario.Domain.Utility.GlobalUtils
import com.ncs.mario.R
import com.ncs.mario.UI.MainScreen.Home.Adapters.EventsAdapter
import com.ncs.mario.UI.MainScreen.Home.Adapters.ListItem
import com.ncs.mario.UI.MainScreen.Home.Adapters.PostAdapter
import com.ncs.mario.UI.MainScreen.MainActivity
import com.ncs.mario.UI.MainScreen.MainViewModel
import com.ncs.mario.databinding.FragmentViewAllBinding
import com.ncs.mario.databinding.TicketDialogBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class ViewAllFragment : Fragment(), EventsAdapter.Callback, PostAdapter.CallBack {

    lateinit var binding: FragmentViewAllBinding
    var type:String?=null
    private val activityBinding: MainActivity by lazy {
        (requireActivity() as MainActivity)
    }
    lateinit var eventsAdapter: EventsAdapter
    private lateinit var adapter: PostAdapter
    private val viewModel: HomeViewModel by activityViewModels()
    private val util: GlobalUtils.EasyElements by lazy {
        GlobalUtils.EasyElements(requireActivity())
    }
    private val activityViewModel : MainViewModel by activityViewModels()

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
        observeViewModel()
        setUpViews()
    }

    private fun setUpViews(){

        setUpPostsRV(mutableListOf())

        activityBinding.binding.actionbar.titleTv.text=type!!.capitalize()

        activityBinding.binding.actionbar.btnHam.setOnClickThrottleBounceListener{
            moveToPrevious()
        }

//        if (type=="Posts"){
//            viewModel.getPolls()
//            viewModel.getPosts()
//        }
//        else if (type=="Events"){
//            viewModel.getEvents()
//        }

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

        if (type == "Posts") {

            viewModel.polls.observe(viewLifecycleOwner) { polls ->
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

                    adapter.appendPosts(combinedList)
                }
            }

        } else if (type == "Events") {

            viewModel.getEventsResponse.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is ServerResult.Failure -> {}
                    ServerResult.Progress -> {}
                    is ServerResult.Success -> {
                        val events = result.data
                        Log.d("eventscheck", "$events")
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

    private fun setUpPostsRV(posts:MutableList<ListItem>){
        adapter = PostAdapter(posts, this)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun onBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            moveToPrevious()
        }
    }

    fun moveToPrevious(){
        findNavController().navigate(R.id.action_fragment_view_all_to_fragment_home)
    }

    override fun onClick(event: Event, isEnrolled: Boolean) {
        if (isEnrolled){
            viewModel.unenrollUser(event._id)
        }
        else{
            viewModel.enrollUser(event._id)
        }
    }

    override fun onCheckBoxClick(poll: Poll, selectedOption:String) {
        viewModel.answerPoll(AnswerPollBody(poll_id = poll._id, option = selectedOption))
    }

    override fun onLikeClick(post: Post, isLiked: Boolean, isDoubleTapped: Boolean) {
        if (!isLiked){
            requireContext().performHapticFeedback()
            adapter.removePost(ListItem.Post(post))
            viewModel.likePost(LikePostBody(post_id = post._id, action = "LIKE"))
            val newpost=post.copy(likes = if (!post.liked) post.likes+1 else post.likes, liked = true,image = post.image ?: "default_image_url")
            adapter.appendPosts(mutableListOf(ListItem.Post(newpost)))
        }
        else {
            adapter.removePost(ListItem.Post(post))
            viewModel.likePost(LikePostBody(post_id = post._id, action = "UNLIKE"))
            val newpost=post.copy(likes = post.likes-1, liked = false,image = post.image ?: "default_image_url")
            adapter.appendPosts(mutableListOf(ListItem.Post(newpost)))
        }
    }

    override fun onShareClick(post: Post) {
        if (post.image.isNullOrEmpty()) {
            util.showSnackbar(binding.root, "Something went wrong, try again later", 2000)
        }
        else{
            ExtensionsUtil.generateShareLink(post._id) { link ->
                if (link.isNull) {
                    util.showSnackbar(binding.root, "Something went wrong, try again later", 2000)
                } else {
                    downloadImage(post.image) { bitmap ->
                        if (bitmap != null) {
                            sharePost(bitmap, post, link.toString())
                        } else {
                            util.showSnackbar(binding.root, "Failed to load image", 2000)
                        }
                    }
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

    private fun sharePost(bitmap: Bitmap, post: Post, link:String) {
        val cachePath = File(requireContext().filesDir, "images")
        cachePath.mkdirs()
        val stream = FileOutputStream(File(cachePath, "shared_image.png"))
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.close()
        val imagePath = File(cachePath, "shared_image.png")
        val imageUri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", imagePath)
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, imageUri)
            putExtra(Intent.EXTRA_TEXT, "${post.caption}\n\n$link")
            type = "image/png"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        requireContext().startActivity(Intent.createChooser(shareIntent, "Share news article"))
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

}