package com.ncs.mario.UI.MainScreen.Home.Adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.RadioButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.tiagohm.markdownview.css.InternalStyleSheet
import br.tiagohm.markdownview.css.styles.Github
import com.bumptech.glide.Glide
import com.ncs.mario.Domain.Models.Events.Option
import com.ncs.mario.Domain.Models.Events.Poll
import com.ncs.mario.Domain.Models.Posts.Post
import com.ncs.mario.Domain.Utility.ExtensionsUtil.performHapticFeedback
import com.ncs.mario.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.mario.Domain.Utility.ExtensionsUtil.setOnDoubleClickListener
import com.ncs.mario.Domain.Utility.ExtensionsUtil.visible
import com.ncs.mario.R
import com.ncs.mario.databinding.ItemPostBinding
import com.ncs.mario.databinding.PollItemBinding
import com.ncs.mario.databinding.PollUiBinding
import me.shouheng.utils.device.VibrateUtils.vibrate
import kotlin.random.Random

class PostAdapter(private val items: MutableList<ListItem>, private val callBack: CallBack) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val POLL = 0
        private const val EVENT = 1
        private const val QNA = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is ListItem.Poll -> POLL
            is ListItem.Post -> EVENT
            else -> throw IllegalArgumentException("Invalid type of data at position $position")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            POLL -> {
                val binding = PollUiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PollViewHolder(binding)
            }
            EVENT -> {
                val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PostViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PollViewHolder -> holder.bind(items[position] as ListItem.Poll)
            is PostViewHolder -> holder.bind(items[position] as ListItem.Post)
        }
    }

    override fun getItemCount(): Int = items.size

    inner class PollViewHolder(private val binding: PollUiBinding) : RecyclerView.ViewHolder(binding.root) {
        private var selectedOption = -1
        private var previousSelectedOption = -1

        fun bind(poll: ListItem.Poll) {
            binding.tvQuestion.text=poll.poll.question
            setupUI(poll.poll)
        }

        private fun setupUI(poll: Poll) {
            binding.container.removeAllViews()
            poll.options.forEachIndexed { index, option ->


                val optionBinding = PollItemBinding.inflate(LayoutInflater.from(binding.root.context), binding.container, false)
                val userchoice=poll.userChoice
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
                val percentText = optionBinding.findViewById<TextView>(R.id.tvScore)
                val seekBar = optionBinding.findViewById<SeekBar>(R.id.seekBar)
                val percent = (option.votes.toDouble() / options.sumOf { it.votes }) * 100
                percentText.text = option.votes.toString()
                seekBar.progress = percent.toInt()
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

    inner class PostViewHolder(private val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: ListItem.Post) {
            Glide.with(binding.root.context).load(post.post.image).placeholder(R.drawable.placeholder_image).into(binding.postImage)

            binding.likesCount.text=post.post.likes.toString()

            if (post.post.liked){
                binding.likeImage.setImageResource(R.drawable.baseline_favorite_24)
            }else{
                binding.likeImage.setImageResource(R.drawable.baseline_favorite_border_24)
            }

            binding.root.setOnDoubleClickListener {
                binding.likeImage.setImageResource(R.drawable.baseline_favorite_24)
                callBack.onLikeClick(post.post,false, true)
            }

            binding.share.setOnClickThrottleBounceListener {
                callBack.onShareClick(post.post)
            }

            binding.like.setOnClickThrottleBounceListener {
                if (post.post.liked){
                    binding.likeImage.setImageResource(R.drawable.baseline_favorite_border_24)
                }else {
                    binding.likeImage.setImageResource(R.drawable.baseline_favorite_24)
                }
                callBack.onLikeClick(post.post,post.post.liked)
            }
            setUpMarkdown(post.post.caption)
        }

        private fun setUpMarkdown(description: String) {
            val css: InternalStyleSheet = Github()

            with(css) {
                addFontFace(
                    "o2font",
                    "normal",
                    "normal",
                    "normal",
                    "url('file:///android_res/font/sfregular.ttf')"
                )
                addRule("body", "font-family:o2font")
                addRule("body", "font-size:18px")
                addRule("body", "line-height:28px")
                addRule("body", "background-color: #242424")
                addRule("body", "color: #fff")
                addRule("body", "padding: 0")
                addRule("body", "margin: 0")
                addRule("p", "margin: 0")
                addRule("div", "margin: 0")
                addRule("a", "color: #86ff7c")
                addRule("pre", "border: 1px solid #000;")
                addRule("pre", "border-radius: 0px;")
                addRule("pre", "max-height: 400px;")
                addRule("pre", "overflow:auto")
                addRule("pre", "white-space: pre-line")
            }

            binding.markdownView.settings.javaScriptEnabled = true
            binding.markdownView.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
            binding.markdownView.settings.setRenderPriority(WebSettings.RenderPriority.HIGH)

            binding.markdownView.addStyleSheet(css)
            binding.markdownView.addJavascriptInterface(AndroidToJsInterface(), "send")

            binding.markdownView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    view?.evaluateJavascript("script") {}
                }


                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    val intent = Intent(Intent.ACTION_VIEW, request?.url)
                    binding.root.context.startActivity(intent)
                    return true
                }

            }

            binding.markdownView.loadMarkdown(description)

            binding.markdownView.visible()

        }


        inner class AndroidToJsInterface {
            @JavascriptInterface
            fun sendCode(codeText: String, language: String?) {
            }

        }


    }
    interface CallBack{
        fun onCheckBoxClick(poll: Poll, selectedOption:String)
        fun onLikeClick(post: Post, isLiked:Boolean, isDoubleTapped:Boolean=false)
        fun onShareClick(post: Post)
    }

    fun appendPosts(posts: List<ListItem>) {
        items.addAll(posts.distinctBy {
            when (it) {
                is ListItem.Poll -> it.poll._id
                is ListItem.Post -> it.post._id
            }
        })
        items.sortByDescending {
            when (it) {
                is ListItem.Poll -> it.poll.createdAt
                is ListItem.Post -> it.post.createdAt
            }
        }
        notifyDataSetChanged()
    }

    fun removePost(post: ListItem) {
        items.remove(post)
        items.sortByDescending {
            when (it) {
                is ListItem.Poll -> it.poll.createdAt
                is ListItem.Post -> it.post.createdAt
            }
        }
    }

}

sealed class ListItem {
    data class Poll(val title: String, val poll: com.ncs.mario.Domain.Models.Events.Poll) : ListItem()
    data class Post(val post:com.ncs.mario.Domain.Models.Posts.Post) : ListItem()
}

