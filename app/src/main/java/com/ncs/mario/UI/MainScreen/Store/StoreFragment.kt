package com.ncs.mario.UI.MainScreen.Store

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.ncs.mario.Domain.Models.Merch
import com.ncs.mario.Domain.Utility.GlobalUtils
import com.ncs.mario.R
import com.ncs.mario.databinding.FragmentStoreBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StoreFragment : Fragment() {
    private var _binding:FragmentStoreBinding?=null
    private val binding get() = _binding!!
    private lateinit var adapter: StoreAdapter

    private val util: GlobalUtils.EasyElements by lazy {
        GlobalUtils.EasyElements(requireContext())
    }

    private val viewModel: StoreViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentStoreBinding.inflate(inflater, container, false)
        return binding.root

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val items  = listOf(Merch("1","https://drive.google.com/drive-viewer/AKGpihZ6ifNzVPNCPtpSXh_2v_mUO2A461UmBQzoejW25T6leZznQ2Nm7ADTs_IzrZ9NPnn0JOXeHtYy__UhH9uaveVcG28Dg3KSVL8=w1920-h868-rw-v1","tshirt",800,100),
        Merch("2","https://m.media-amazon.com/images/I/41nJIA2wTvL._SX679_.jpg","tshirt",800,10),
        Merch("3","https://m.media-amazon.com/images/I/51La4k58AlL._SX679_.jpg","tshirt",800,20),
            Merch("4","https://drive.google.com/drive-viewer/AKGpihZ6ifNzVPNCPtpSXh_2v_mUO2A461UmBQzoejW25T6leZznQ2Nm7ADTs_IzrZ9NPnn0JOXeHtYy__UhH9uaveVcG28Dg3KSVL8=w1920-h868-rw-v1","tshirt",800,30),
            Merch("5","https://drive.google.com/drive-viewer/AKGpihZ6ifNzVPNCPtpSXh_2v_mUO2A461UmBQzoejW25T6leZznQ2Nm7ADTs_IzrZ9NPnn0JOXeHtYy__UhH9uaveVcG28Dg3KSVL8=w1920-h868-rw-v1","tshirt",800,5),)
        adapter = StoreAdapter(emptyList())
        adapter.updateItems(items)
        binding.recyclerViewItems.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerViewItems.adapter = adapter
        viewModel.getNCSMerch()
        bindObserver()
        handlingButton()

    }

    private fun handlingButton() {
        binding.tabNerd.setOnClickListener {
            binding.tabNerd.setBackgroundResource(R.drawable.filled_primary_box)
            binding.tabNCS.setBackgroundResource(R.color.transparent)
            binding.tabNCS.setTextColor(ContextCompat.getColor(requireContext(),R.color.primary))
            binding.tabNerd.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
            binding.recyclerViewItems.visibility = View.GONE
            binding.comingSoon.visibility = View.VISIBLE
        }
        binding.tabNCS.setOnClickListener {
            binding.tabNCS.setBackgroundResource(R.drawable.filled_primary_box)
            binding.tabNerd.setBackgroundResource(R.color.transparent)
            binding.tabNerd.setTextColor(ContextCompat.getColor(requireContext(),R.color.primary))
            binding.tabNCS.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
            binding.recyclerViewItems.visibility = View.VISIBLE
            binding.comingSoon.visibility = View.GONE
        }
    }

    private fun bindObserver() {

        viewModel.getMerch.observe(viewLifecycleOwner){
            if (it!=null){
                if(it.success){
                    adapter.updateItems(it.merchandise)
                }
                else{
                    util.showActionSnackbar(binding.root,it.message,10000,"Retry"){
                        viewModel.getNCSMerch()
                    }
                }

            }
            else{
                util.showActionSnackbar(binding.root,"Something went wrong",10000,"Retry"){
                }
            }
        }
        viewModel.errorMessage.observe(viewLifecycleOwner){
            if (it!=null){
                util.showActionSnackbar(binding.root,it.toString(),200000,"Retry"){
                    viewModel.getNCSMerch()
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null

    }
}