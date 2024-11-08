package com.ncs.marioapp.UI.AdminScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ncs.marioapp.Domain.Models.Admin.GiftCoinsPostBody
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.gone
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.toast
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.visible
import com.ncs.marioapp.Domain.Utility.GlobalUtils
import com.ncs.marioapp.R
import com.ncs.marioapp.databinding.GiftCoinsBottomSheetBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class GiftCoinsBottomSheet(val id:String): BottomSheetDialogFragment() {

    private val viewModel: AdminViewModel by activityViewModels()
    private lateinit var binding: GiftCoinsBottomSheetBinding

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
        binding = GiftCoinsBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        setUpViews()
    }

    private fun setUpViews(){
        binding.btnGiftCoins.setOnClickThrottleBounceListener{
            val coins=binding.coinsEt.text.toString()
            if (coins.isEmpty()){
                toast("Enter some coins")
            }
            else if (coins.toInt()>40){
                toast("Coins cannot be more than 40")
            }
            else if (coins.toInt()<1){
                toast("Coins cannot be less than 1")
            }
            else{
                binding.progressBar.visible()
                binding.btnGiftCoins.gone()
                viewModel.giftCoins(GiftCoinsPostBody(id,coins.toInt()))
            }
        }
    }

    private fun observeViewModel(){
        viewModel.errorMessage.observe(requireActivity()) {
            showError(it)
            if (it=="Coins gifted successfully"){
                toast("Coins gifted successfully")
                dismiss()
            }
        }

        viewModel.progressStateGiftCoins.observe(requireActivity()) {
            if (it) {
                binding.progressBar.visible()
                binding.btnGiftCoins.gone()
            } else {
                binding.progressBar.gone()
                binding.btnGiftCoins.visible()
            }
        }
    }


    private fun showError(message: String?) {
        util.showSnackbar(binding.root,message!!,2000)
    }



}
