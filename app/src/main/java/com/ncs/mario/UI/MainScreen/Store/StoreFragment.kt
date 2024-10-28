package com.ncs.mario.UI.MainScreen.Store

import StoreAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.ncs.mario.Domain.Utility.ExtensionsUtil.gone
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

    private val viewModel: StoreViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        activityBinding.binding.actionbar.score.visibility=View.VISIBLE
        activityBinding.binding.actionbar.btnHam.setImageResource(R.drawable.ham)
        activityBinding.binding.actionbar.titleTv.text="Nerd Store"
        activityBinding.binding.actionbar.btnHam.setOnClickListener {
            if (activityBinding.binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                activityBinding.binding.drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                activityBinding.binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }
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
                val bottomSheet = StoreItemBottomSheet.newInstance(it)
                bottomSheet.show(parentFragmentManager, "OrderDetailsBottomSheet")
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
        viewModel.purchaseResultLiveData.observe(viewLifecycleOwner) {
            if (it != null) {
                activityViewModel.fetchCriticalInfo()
                viewModel.getNCSMerch()
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                viewModel.clearPurchaseResult()
            }
        }

        viewModel.purchaseMerch.observe(viewLifecycleOwner) {
            if (it != null) {
                Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
                activityViewModel.fetchCriticalInfo()
                viewModel.getNCSMerch()
                viewModel.clearPurchaseMessage()
            }
        }

    }



    override fun onDestroy() {
        super.onDestroy()
        _binding = null

    }
}