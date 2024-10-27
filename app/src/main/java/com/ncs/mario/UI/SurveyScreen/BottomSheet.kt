package com.ncs.mario.UI.SurveyScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ncs.mario.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.mario.Domain.Utility.ExtensionsUtil.visible
import com.ncs.mario.R
import com.ncs.mario.databinding.BottomSheetBinding


class BottomSheet(private val dataList: List<String>,private val type:String, private val sendText: SendText, val currentSelected:Int):BottomSheetDialogFragment (),
    BottomSheetRVAdapter.onClickString{
    lateinit var binding: BottomSheetBinding
    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.closeBtn.setOnClickThrottleBounceListener{
            dismiss()
        }
        binding.title.text=type.capitalize()
        setRecyclerView(dataList,type)
    }

    fun setRecyclerView(dataList: List<String>, type: String){
        val recyclerView = binding.bottomsheetRv
        val adapter = BottomSheetRVAdapter(dataList.toMutableList(), type, currentSelected,this@BottomSheet)
        val gridLayoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.adapter = adapter
        recyclerView.visible()
    }

    override fun sendString(text: String,type: String,currentSelected: Int) {
        sendText.stringtext(text,type,currentSelected)
        dismiss()
    }

    interface SendText {
        fun stringtext(text: String,type: String,currentSelected: Int)
    }

}