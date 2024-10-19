package com.ncs.mario.UI.MainScreen.Store

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.ncs.mario.Domain.HelperClasses.PrefManager
import com.ncs.mario.Domain.Models.Merch
import com.ncs.mario.Domain.Utility.ExtensionsUtil.isNull
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

        adapter = StoreAdapter(emptyList()){
                    viewModel.purchaseMerch(it.id)
        }
        binding.recyclerViewItems.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerViewItems.adapter = adapter
        viewModel.getNCSMerch()
        bindObserver()

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
        viewModel.purchaseResultLiveData.observe(viewLifecycleOwner){
            if (it!=null){
                Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                }
            }
        }



    override fun onDestroy() {
        super.onDestroy()
        _binding = null

    }
}