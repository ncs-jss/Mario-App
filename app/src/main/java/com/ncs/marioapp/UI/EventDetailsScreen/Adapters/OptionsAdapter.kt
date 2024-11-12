package com.ncs.marioapp.UI.EventDetailsScreen.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ncs.marioapp.databinding.PollItemBinding

class OptionsAdapter(
    private val options: List<Any>,
    private val onOptionSelected: (String) -> Unit
) : RecyclerView.Adapter<OptionsAdapter.OptionViewHolder>() {

    private var selectedPosition = -1

    inner class OptionViewHolder(private val binding: PollItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(option: Any, position: Int) {
            binding.radioButton.text = option.toString()
            binding.tvOption.text = option.toString()
            binding.radioButton.isChecked = position == selectedPosition
            binding.tvScore.visibility = View.GONE
            binding.seekBar.visibility = View.GONE

            binding.radioButton.setOnClickListener {
                selectedPosition = position
                onOptionSelected(option.toString())
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionViewHolder {
        val binding = PollItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OptionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OptionViewHolder, position: Int) {
        holder.bind(options[position], position)
    }

    override fun getItemCount(): Int = options.size
}
