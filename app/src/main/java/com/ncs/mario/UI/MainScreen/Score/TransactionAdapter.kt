package com.ncs.mario.UI.MainScreen.Home.Adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ncs.mario.Domain.Models.Events.Event
import com.ncs.mario.Domain.Models.Events.ParticipatedEvent
import com.ncs.mario.Domain.Models.Score.Transaction
import com.ncs.mario.Domain.Utility.ExtensionsUtil.gone
import com.ncs.mario.Domain.Utility.ExtensionsUtil.load
import com.ncs.mario.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.mario.Domain.Utility.ExtensionsUtil.visible
import com.ncs.mario.R
import com.ncs.mario.databinding.EventsItemViewBinding
import com.ncs.mario.databinding.TransactionItemLayoutBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Random


class TransactionAdapter(private var transactions: List<Transaction>, private val callback: Callback) :
    RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = TransactionItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction=transactions[position]

        holder.binding.title.text=transaction.title
        holder.binding.time.text = "${formatTimestamp(transaction.time).first} | ${formatTimestamp(transaction.time).second}"
        if (transaction.operation=="DEBIT"){
            holder.binding.operation.text="DEBIT"
            holder.binding.operation.setBackgroundResource(R.drawable.status_failure)
            holder.binding.coins.text = "- ${transaction.coins}"
        }
        else{
            holder.binding.operation.text="CREDIT"
            holder.binding.operation.setBackgroundResource(R.drawable.status_success)
            holder.binding.coins.text = "+ ${transaction.coins}"
        }
        holder.binding.type.text="${transaction.type.toString().capitalize()}"

        holder.binding.root.setOnClickThrottleBounceListener {
            callback.onClick(transaction)
        }
    }


    fun formatTimestamp(timestamp: Long): Pair<String, String> {
        val date = Date(timestamp)
        val dateFormat = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val formattedDate = dateFormat.format(date)
        val formattedTime = timeFormat.format(date)
        return Pair(formattedDate, formattedTime)
    }

    inner class ViewHolder(val binding: TransactionItemLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemCount() = transactions.size

    interface Callback{
        fun onClick(transaction: Transaction)
    }
}
