package com.ncs.marioapp.UI.EventDetailsScreen.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ncs.marioapp.databinding.TopicsRvItemBinding


class TopicsAdapter(private var topics: List<String>) :
    RecyclerView.Adapter<TopicsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = TopicsRvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val topic=topics[position]
        holder.binding.topic.text=topic
    }

    inner class ViewHolder(val binding: TopicsRvItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemCount() = topics.size

}
