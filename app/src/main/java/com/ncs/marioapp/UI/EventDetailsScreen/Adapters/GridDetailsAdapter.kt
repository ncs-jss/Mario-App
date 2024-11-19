package com.ncs.marioapp.UI.EventDetailsScreen.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ncs.marioapp.Domain.Models.Events.EventDetails.GridDetails
import com.ncs.marioapp.databinding.ItemDetailsBinding

class GridAdapter(private val items: List<GridDetails>) :
    RecyclerView.Adapter<GridAdapter.GridViewHolder>() {
    inner class GridViewHolder(val binding: ItemDetailsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        val view = ItemDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GridViewHolder(view)
    }

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            itemImage.setImageResource(item.img)
            itemDescription.text = item.desc
            itemTitle.text = item.title
        }
    }

    override fun getItemCount(): Int = items.size
}
