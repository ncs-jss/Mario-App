package com.ncs.mario.UI.MyRedemptionsScreen

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ncs.mario.Domain.Models.MyOrderData
import com.ncs.mario.Domain.Utility.ExtensionsUtil.gone
import com.ncs.mario.Domain.Utility.ExtensionsUtil.visible
import com.ncs.mario.Domain.Utility.GlobalUtils
import com.ncs.mario.R
import com.ncs.mario.UI.MainScreen.Score.RedemptionAdapter
import com.ncs.mario.databinding.ActivityMyRedemptionsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyRedemptionsActivity : AppCompatActivity(), RedemptionAdapter.OnOrderClickListener {

    val binding: ActivityMyRedemptionsBinding by lazy {
        ActivityMyRedemptionsBinding.inflate(layoutInflater)
    }
    private val util: GlobalUtils.EasyElements by lazy {
        GlobalUtils.EasyElements(this@MyRedemptionsActivity)
    }
    private val viewModel: RedemptionViewModel by viewModels()
    private lateinit var redemptionAdapter: RedemptionAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.swiperefresh.setOnRefreshListener {
            viewModel.getMyMerch()
        }

        binding.shimmerLayout.apply {
            startShimmer()
            visibility = View.VISIBLE
        }
        binding.redemptionsRv.gone()

        setUpViews()
        viewModel.getMyMerch()
        redemptionAdapter = RedemptionAdapter(this)
        binding.redemptionsRv.apply {
            layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            adapter = redemptionAdapter

        }
        redemptionAdapter.submitList(yourOrderList())
        bindingObserver()
    }

    private fun yourOrderList(): List<MyOrderData> {
        return listOf()
    }

    private fun bindingObserver() {
        viewModel.errorMessage.observe(this){
            if (it!=null){
                util.showActionSnackbar(binding.root,it.toString(),200000,"Retry"){
                    viewModel.getMyMerch()
                }
            }
        }
        viewModel.merchResponse.observe(this){ response ->
            if(response.success){
                if(!response.orders.isNullOrEmpty()){
                    redemptionAdapter.submitList(response.orders.sortedByDescending { it.createdAt })
                    binding.shimmerLayout.apply {
                        stopShimmer()
                        visibility = View.GONE
                    }
                    binding.redemptionsRv.visible()
                    if (binding.swiperefresh.isRefreshing){
                        binding.swiperefresh.isRefreshing = false
                    }
                }
                else{
                    binding.shimmerLayout.apply {
                        stopShimmer()
                        visibility = View.GONE
                    }
                    binding.redemptionsRv.visible()
                    if (binding.swiperefresh.isRefreshing){
                        binding.swiperefresh.isRefreshing = false
                    }
                    binding.redemptionsRv.visibility = View.GONE
                    binding.noRedemptionsTV.visibility = View.VISIBLE
                }
            }
            else{
                util.showActionSnackbar(binding.root,response.message,200000,"Retry"){
                    viewModel.getMyMerch()
                }
            }
        }
    }
    override fun onOrderClick(order: MyOrderData) {
        val bottomSheet = OrderDetailsBottomSheet.newInstance(order)
        bottomSheet.show(supportFragmentManager, "OrderDetailsBottomSheet")
    }

    private fun setUpViews() {
        binding.actionbar.btnHam.setImageResource(R.drawable.ic_back_arrow)
        binding.actionbar.btnHam.setOnClickListener {
            onBackPressed()
        }
        binding.actionbar.titleTv.text = "My Redemptions"
        binding.actionbar.score.gone()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        finish()
    }

}