package com.ncs.mario.UI.MainScreen.Score

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ncs.mario.Domain.Utility.GlobalUtils
import com.ncs.mario.R
import com.ncs.mario.databinding.MarioScoreInfoBottomSheetBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MarioCoinsInfoBottomSheet(val points:Int): BottomSheetDialogFragment() {

    private lateinit var binding: MarioScoreInfoBottomSheetBinding

    private val util: GlobalUtils.EasyElements by lazy {
        GlobalUtils.EasyElements(requireActivity())
    }

    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MarioScoreInfoBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
    }

    private fun setUpViews(){

        binding.points.text=points.toString()

        if(points<=100){
            binding.noobieView.setBackgroundResource(R.drawable.button_bg)
            binding.intermediateView.setBackgroundResource(0)
            binding.proView.setBackgroundResource(0)
        }
        else if(points<=400){
            binding.noobieView.setBackgroundResource(0)
            binding.intermediateView.setBackgroundResource(R.drawable.button_bg)
            binding.proView.setBackgroundResource(0)
        }
        else{
            binding.noobieView.setBackgroundResource(0)
            binding.intermediateView.setBackgroundResource(0)
            binding.proView.setBackgroundResource(R.drawable.button_bg)
        }
    }

}
