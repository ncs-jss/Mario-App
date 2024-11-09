package com.ncs.marioapp.UI.MainScreen.Home.Adapters


import android.animation.Animator
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ncs.marioapp.Domain.Models.Events.Option
import com.ncs.marioapp.Domain.Models.Events.Poll
import com.ncs.marioapp.Domain.Models.Posts.Post
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.bubblePopIn
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.fadeInAndVisible
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.fadeOutAndGone
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.loadImage
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.pulseEffect
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.setOnDoubleClickListener
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.visible
import com.ncs.marioapp.R
import com.ncs.marioapp.databinding.ItemPostBinding
import com.ncs.marioapp.databinding.PollItemBinding
import com.ncs.marioapp.databinding.PollUiBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostAdapter(private val callBack: CallBack) :
    ListAdapter<ListItem, RecyclerView.ViewHolder>(PostDiffCallback()) {

    companion object {
        private const val POLL = 0
        private const val EVENT = 1
    }


    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ListItem.Poll -> POLL
            is ListItem.Post -> EVENT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            POLL -> PollViewHolder(
                PollUiBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            EVENT -> PostViewHolder(
                ItemPostBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PollViewHolder -> holder.bind(getItem(position) as ListItem.Poll)
            is PostViewHolder -> holder.bind(getItem(position) as ListItem.Post)
        }
    }

    inner class PollViewHolder(private val binding: PollUiBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var selectedOption = -1
        private var previousSelectedOption = -1

        fun bind(poll: ListItem.Poll) {
            binding.tvQuestion.text = poll.poll.question
            setupUI(poll.poll)
        }

        private fun setupUI(poll: Poll) {
            binding.container.removeAllViews()
            poll.options.forEachIndexed { index, option ->


                val optionBinding = PollItemBinding.inflate(
                    LayoutInflater.from(binding.root.context),
                    binding.container,
                    false
                )
                val userchoice = poll.userChoice
                if (userchoice != null) {
                    val userChoiceIndex = poll.options.indexOfFirst { it.text == userchoice }
                    selectedOption = userChoiceIndex
                }

                optionBinding.radioButton.text = option.text
                optionBinding.radioButton.isChecked = (index == selectedOption)

                val percentage = (option.votes.toDouble() / poll.options.sumOf { it.votes }) * 100
                optionBinding.tvScore.text = option.votes.toString()
                optionBinding.tvOption.text = option.text
                optionBinding.seekBar.progress = percentage.toInt()
                optionBinding.seekBar.max = 100
                optionBinding.seekBar.isEnabled = false

                optionBinding.radioButton.setOnClickListener {
                    if (selectedOption != index) {
                        callBack.onCheckBoxClick(poll, poll.options[index].text)
                        previousSelectedOption = selectedOption
                        selectedOption = index
                        updateSelection()
                        updateCounts(poll.options, index)
                    }
                }

                binding.container.addView(optionBinding.root)
            }

        }

        private fun updateCounts(options: List<Option>, selectedIndex: Int) {
            options[selectedIndex].votes += 1
            if (previousSelectedOption != -1) {
                options[previousSelectedOption].votes -= 1
            }
            updateUI(options)
        }

        private fun updateUI(options: List<Option>) {
            options.forEachIndexed { index, option ->
                val optionBinding = binding.container.getChildAt(index) as ViewGroup
                optionBinding.findViewById<TextView>(R.id.tvScore).text = option.votes.toString()
                optionBinding.findViewById<SeekBar>(R.id.seekBar).progress =
                    (option.votes.toDouble() / options.sumOf { it.votes } * 100).toInt()
            }
        }

        private fun updateSelection() {
            val totalOptions = binding.container.childCount
            for (i in 0 until totalOptions) {
                val optionBinding = binding.container.getChildAt(i) as ViewGroup
                val radioButton = optionBinding.findViewById<RadioButton>(R.id.radioButton)
                radioButton.isChecked = (i == selectedOption)
            }
        }
    }

    inner class PostViewHolder(private val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(post: ListItem.Post) {

            binding.likeAnimationView.addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                }

                override fun onAnimationEnd(animation: Animator) {
                    binding.likeAnimationView.fadeOutAndGone()
                }

                override fun onAnimationCancel(animation: Animator) {
                }

                override fun onAnimationRepeat(animation: Animator) {
                }

            })

            binding.postImage.loadImage(post.post.image)
            binding.likesCount.text = post.post.likes.toString()

            if (post.post.liked) {
                binding.likeImage.setImageResource(R.drawable.baseline_favorite_24)
            } else {
                binding.likeImage.setImageResource(R.drawable.baseline_favorite_border_24)
            }

            binding.share.setOnClickThrottleBounceListener {
                callBack.onShareClick(post.post)
            }

            binding.like.setOnClickThrottleBounceListener {
                if (post.post.liked) {
                    binding.likeImage.bubblePopIn()
                } else {
                    binding.likeAnimationView.fadeInAndVisible()
                    binding.likeAnimationView.playAnimation()
                    binding.likesCount.pulseEffect()
                    binding.likeImage.bubblePopIn()
                }
                callBack.onLikeClick(post.post, post.post.liked)
            }

            binding.root.setOnDoubleClickListener {
                if (post.post.liked) {
                    binding.likeImage.bubblePopIn()
                } else {
                    binding.likeAnimationView.fadeInAndVisible()
                    binding.likeAnimationView.playAnimation()
                    binding.likesCount.pulseEffect()
                    binding.likeImage.bubblePopIn()
                }
                callBack.onLikeClick(post.post, post.post.liked)
            }


            binding.markdownView.visible()
            binding.markdownView.text = post.post.caption

        }


//        private fun setUpMarkdown(description: String) {
//            val css: InternalStyleSheet = Github()
//
//            with(css) {
//                addFontFace(
//                    "o2font",
//                    "normal",
//                    "normal",
//                    "normal",
//                    "url('file:///android_res/font/sfregular.ttf')"
//                )
//                addRule("body", "font-family:o2font")
//                addRule("body", "font-size:18px")
//                addRule("body", "line-height:28px")
//                addRule("body", "background-color: #242424")
//                addRule("body", "color: #fff")
//                addRule("body", "padding: 0")
//                addRule("body", "margin: 0")
//                addRule("p", "margin: 0")
//                addRule("div", "margin: 0")
//                addRule("a", "color: #86ff7c")
//                addRule("pre", "border: 1px solid #000;")
//                addRule("pre", "border-radius: 0px;")
//                addRule("pre", "max-height: 400px;")
//                addRule("pre", "overflow:auto")
//                addRule("pre", "white-space: pre-line")
//            }
//
//            binding.markdownView.settings.javaScriptEnabled = true
//            binding.markdownView.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
//            binding.markdownView.settings.setRenderPriority(WebSettings.RenderPriority.HIGH)
//
//            binding.markdownView.addStyleSheet(css)
//
//            binding.markdownView.webViewClient = object : WebViewClient() {
//                override fun onPageFinished(view: WebView?, url: String?) {
//                    view?.evaluateJavascript("script") {}
//                }
//
//
//                override fun shouldOverrideUrlLoading(
//                    view: WebView?,
//                    request: WebResourceRequest?
//                ): Boolean {
//                    val intent = Intent(Intent.ACTION_VIEW, request?.url)
//                    binding.root.context.startActivity(intent)
//                    return true
//                }
//
//            }
//
////            binding.markdownView.loadMarkdown(description)
//            binding.markdownView.visible()
//
//        }

    }


    fun updatePost(updatedPost: Post) {
        CoroutineScope(Dispatchers.Default).launch {
            if (isActive) {
                Log.d("PostAdapter", "Like job started")
                val newList = currentList.map {
                    if (it is ListItem.Post && it.post._id == updatedPost._id) {
                        ListItem.Post(updatedPost)
                    } else {
                        it
                    }
                }
                withContext(Dispatchers.Main) {
                    submitList(newList)
                }
            } else {
                Log.d("PostAdapter", "Job canceled")
                return@launch
            }
        }
    }

    interface CallBack {
        fun onCheckBoxClick(poll: Poll, selectedOption: String)
        fun onLikeClick(post: Post, isLiked: Boolean)
        fun onShareClick(post: Post)
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<ListItem>() {
    override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
        return when {
            oldItem is ListItem.Post && newItem is ListItem.Post -> oldItem.post._id == newItem.post._id
            oldItem is ListItem.Poll && newItem is ListItem.Poll -> oldItem.poll._id == newItem.poll._id
            else -> false
        }
    }

    override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
        return when {
            oldItem is ListItem.Post && newItem is ListItem.Post -> oldItem.post == newItem.post
            oldItem is ListItem.Poll && newItem is ListItem.Poll -> oldItem.poll == newItem.poll
            else -> false
        }
    }
}


sealed class ListItem {
    data class Poll(val title: String, val poll: com.ncs.marioapp.Domain.Models.Events.Poll) :
        ListItem()

    data class Post(val post: com.ncs.marioapp.Domain.Models.Posts.Post) : ListItem()
}

