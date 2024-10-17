package com.ncs.mario.UI.MainScreen.Home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ncs.mario.Domain.Models.EVENTS.PollItem
import com.ncs.mario.Domain.Models.EVENTS.Posts
import com.ncs.mario.R
import com.ncs.mario.databinding.ItemPostBinding
import com.ncs.mario.databinding.PollItemBinding
import com.ncs.mario.databinding.PollUiBinding

class PostAdapter(private val items: List<ListItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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

    // ViewHolder for Poll
    inner class PollViewHolder(private val binding: PollUiBinding) : RecyclerView.ViewHolder(binding.root) {
        private var selectedOption = -1 // Keeps track of the selected option
        private var previousSelectedOption = -1

        fun bind(poll: ListItem.Poll) {
            setupUI(poll.pollItem.options)
        }

        private fun setupUI(options: List<PollItem.Option>) {
            binding.container.removeAllViews() // Clear previous views

            options.forEachIndexed { index, option ->
                val optionBinding = PollItemBinding.inflate(LayoutInflater.from(binding.root.context), binding.container, false)

                optionBinding.radioButton.text = option.text
                val percentage = (option.votes.toDouble() / options.sumOf { it.votes }) * 100
                optionBinding.tvScore.text = option.votes.toString()
                optionBinding.tvOption.text = option.text
                optionBinding.seekBar.progress = percentage.toInt()
                optionBinding.seekBar.max = 100
                optionBinding.seekBar.isEnabled = false // Disable interaction

                optionBinding.radioButton.setOnClickListener {
                    if (selectedOption != index) {
                        previousSelectedOption = selectedOption
                        selectedOption = index
                        updateSelection()
                        updateCounts(options, index)
                    }
                }

                binding.container.addView(optionBinding.root)
            }
        }

        private fun updateCounts(options: List<PollItem.Option>, selectedIndex: Int) {
            // Increment vote for the selected option
            options[selectedIndex].votes += 1

            // Decrement vote for the previously selected option
            if (previousSelectedOption != -1) {
                options[previousSelectedOption].votes -= 1
            }

            // Update the UI with the new vote counts
            updateUI(options)
        }

        private fun updateUI(options: List<PollItem.Option>) {
            // Iterate over the options and update the corresponding UI components (TextView and SeekBar)
            options.forEachIndexed { index, option ->
                val optionBinding = binding.container.getChildAt(index) as ViewGroup
                val percentText = optionBinding.findViewById<TextView>(R.id.tvScore)
                val seekBar = optionBinding.findViewById<SeekBar>(R.id.seekBar)

                // Recalculate the percentage based on the new vote count
                val percent = (option.votes.toDouble() / options.sumOf { it.votes }) * 100
                percentText.text = option.votes.toString()
                seekBar.progress = percent.toInt()
            }
        }

        private fun updateSelection() {
            // Update the radio button states to reflect the newly selected option
            val totalOptions = binding.container.childCount
            for (i in 0 until totalOptions) {
                val optionBinding = binding.container.getChildAt(i) as ViewGroup
                val radioButton = optionBinding.findViewById<RadioButton>(R.id.radioButton)

                radioButton.isChecked = (i == selectedOption)
            }
        }
    }


    // ViewHolder for Post (Event)
    inner class PostViewHolder(private val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: ListItem.Post) {
            Glide.with(binding.root.context).load(post.logo).placeholder(R.drawable.placeholder_image).into(binding.postImage)
            binding.like.setOnClickListener {
                binding.likeImage.setImageResource(R.drawable.baseline_favorite_24)
            }
        }
    }
}

// Sealed class to represent multiple item types
sealed class ListItem {
    data class Poll(val title: String, val pollItem: PollItem) : ListItem()
    data class Post(val title: String, val description: String, val logo: String) : ListItem()
}

// PollItem model
