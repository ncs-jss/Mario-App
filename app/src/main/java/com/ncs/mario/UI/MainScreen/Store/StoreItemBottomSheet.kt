package com.ncs.mario.UI.MainScreen.Store

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ncorti.slidetoact.SlideToActView
import com.ncs.mario.Domain.HelperClasses.PrefManager
import com.ncs.mario.Domain.Models.Merch
import com.ncs.mario.R
import com.ncs.mario.UI.MainScreen.MainViewModel
import com.ncs.mario.databinding.BottomSheetStoreItemBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StoreItemBottomSheet: BottomSheetDialogFragment() {
    private lateinit var order: Merch
    private val viewModel: StoreViewModel by activityViewModels()
    private val activityViewModel : MainViewModel by activityViewModels()
    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = BottomSheetStoreItemBinding.inflate(inflater, container, false)
        populateOrderDetails(binding)
        return binding.root
    }

    private fun populateOrderDetails(binding: BottomSheetStoreItemBinding) {
        binding.productName.text = order.name
        binding.points.text = order.cost.toString()
        Glide.with(binding.root).load(order.image).placeholder(R.drawable.profile_pic_placeholder).into(binding.itemIcon)
        binding.totalCoins.text = PrefManager.getUserCoins().toString()
        binding.redeemedCoins.text = "-"+order.cost.toString()
        binding.remainingCoins.text =(PrefManager.getUserCoins()-order.cost).toString()

        binding.confirmButton.onSlideCompleteListener = object : SlideToActView.OnSlideCompleteListener {
            override fun onSlideComplete(view: SlideToActView) {
                viewModel.purchaseMerch(order._id).also {
                    activityViewModel.fetchCriticalInfo()
                }
                dismiss()
            }
        }

    }

    companion object {
        fun newInstance(order: Merch): StoreItemBottomSheet {
            val fragment = StoreItemBottomSheet()
            fragment.order = order
            return fragment
        }
    }
}