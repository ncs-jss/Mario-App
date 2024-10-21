package com.ncs.mario.UI.MainScreen.Store

import StoreAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.ncs.mario.Domain.HelperClasses.PrefManager
import com.ncs.mario.Domain.Models.Merch
import com.ncs.mario.Domain.Utility.ExtensionsUtil.gone
import com.ncs.mario.Domain.Utility.ExtensionsUtil.isNull
import com.ncs.mario.Domain.Utility.ExtensionsUtil.visible
import com.ncs.mario.Domain.Utility.GlobalUtils
import com.ncs.mario.R
import com.ncs.mario.UI.MainScreen.MainActivity
import com.ncs.mario.UI.MainScreen.MainViewModel
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
    private val activityBinding: MainActivity by lazy {
        (requireActivity() as MainActivity)
    }


    private val activityViewModel : MainViewModel by activityViewModels()

    private val viewModel: StoreViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        activityBinding.binding.actionbar.titleTv.text="Nerd Store"
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.swiperefresh.setOnRefreshListener {
            viewModel.getNCSMerch()
            activityViewModel.fetchCriticalInfo()
        }

        binding.storeShimmerLayout.apply {
            startShimmer()
            visibility = View.VISIBLE
        }
        binding.recyclerViewItems.gone()
        activityViewModel.fetchCriticalInfo()

        adapter = StoreAdapter {
            if (!it._id.isNullOrEmpty()) {
                viewModel.purchaseMerch(it._id)
            } else {
                Toast.makeText(requireContext(), "Invalid item selected", Toast.LENGTH_SHORT).show()
            }
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
                    binding.storeShimmerLayout.apply {
                        stopShimmer()
                        visibility = View.GONE
                    }
                    binding.recyclerViewItems.visible()
                    adapter.submitList(it.merchandise)
                    if (binding.swiperefresh.isRefreshing){
                        binding.swiperefresh.isRefreshing = false
                    }
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
                activityViewModel.fetchCriticalInfo()
                Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
            }
        }

    }



    override fun onDestroy() {
        super.onDestroy()
        _binding = null

    }
}