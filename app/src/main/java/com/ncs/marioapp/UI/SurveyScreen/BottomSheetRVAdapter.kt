package com.ncs.marioapp.UI.SurveyScreen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.marioapp.databinding.BottomSheetEachItemBinding


class BottomSheetRVAdapter(
    private val dataList: MutableList<String>,
    private val type:String,
    private val currentSelected:Int,
    private val onClick: onClickString
) : RecyclerView.Adapter<BottomSheetRVAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            BottomSheetEachItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (currentSelected!=-1) {
            holder.binding.radioButton.isChecked = position == currentSelected
        }

        holder.binding.textFull.text=dataList[position]

        holder.binding.layout.setOnClickThrottleBounceListener{
            onClick.sendString(dataList[position],type, position)
        }
        holder.binding.radioButton.setOnClickThrottleBounceListener{
            onClick.sendString(dataList[position],type, position)
        }
    }

    inner class ViewHolder(val binding: BottomSheetEachItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface onClickString {
        fun sendString(text: String,type: String, currentSelected: Int)
    }

}
