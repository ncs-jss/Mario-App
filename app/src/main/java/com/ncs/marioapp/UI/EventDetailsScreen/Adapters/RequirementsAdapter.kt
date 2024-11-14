package com.ncs.marioapp.UI.EventDetailsScreen.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ncs.marioapp.databinding.RequirementsRvItemViewBinding



class RequirementsAdapter(private var requirements: List<String>) :
    RecyclerView.Adapter<RequirementsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RequirementsRvItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val requirement=requirements[position]
        holder.binding.reqText.text=requirement
    }

    inner class ViewHolder(val binding: RequirementsRvItemViewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemCount() = requirements.size

}
