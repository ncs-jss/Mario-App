package com.ncs.mario.UI.MyRedemptionsScreen

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ncs.mario.Domain.Models.MyOrderData
import com.ncs.mario.Domain.Models.OrderStatus
import com.ncs.mario.Domain.Utility.ExtensionsUtil.gone
import com.ncs.mario.Domain.Utility.GlobalUtils
import com.ncs.mario.R
import com.ncs.mario.UI.MainScreen.Score.RedemptionAdapter
import com.ncs.mario.databinding.ActivityMainBinding
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
        return listOf(
            MyOrderData(
                _id = 1,
                name = "Wireless Headphones",
                image = "https://res.cloudinary.com/x5fsdgeq3/image/upload/v1729354130/m-merchandise/h1plp8w2x5trbydh2bwc.jpg",
                cost = 800,
                status = OrderStatus.PENDING,
                createdAt = System.currentTimeMillis() - 86400000L // 1 day ago
            ),
            MyOrderData(
                _id = 2,
                name = "Smartphone Case",
                image = "https://res.cloudinary.com/x5fsdgeq3/image/upload/v1729354130/m-merchandise/h1plp8w2x5trbydh2bwc.jpg",
                cost = 400,
                status = OrderStatus.FULFILLED,
                createdAt = System.currentTimeMillis() - 604800000L // 7 days ago
            ),
            MyOrderData(
                _id = 3,
                name = "Bluetooth Speaker",
                image = "https://res.cloudinary.com/x5fsdgeq3/image/upload/v1729354130/m-merchandise/h1plp8w2x5trbydh2bwc.jpg",
                cost = 500,
                status = OrderStatus.CANCELLED,
                createdAt = System.currentTimeMillis() - 2592000000L // 30 days ago
            ),
            MyOrderData(
                _id = 4,
                name = "Fitness Tracker",
                image = "https://res.cloudinary.com/x5fsdgeq3/image/upload/v1729354130/m-merchandise/h1plp8w2x5trbydh2bwc.jpg",
                cost = 200,
                status = OrderStatus.REFUND,
                createdAt = System.currentTimeMillis() - 432000000L // 5 days ago
            )
        )

    }

    private fun bindingObserver() {
        viewModel.errorMessage.observe(this){
            if (it!=null){
                util.showActionSnackbar(binding.root,it.toString(),200000,"Retry"){
                    viewModel.getMyMerch()
                }
            }
        }
        viewModel.merchResponse.observe(this){
            if(it.success){
                if(!it.orders.isNullOrEmpty()){
                    redemptionAdapter.submitList(it.orders)
                }
                else{
                    binding.redemptionsRv.visibility = View.GONE
                    binding.noRedemptionsTV.visibility = View.VISIBLE
                }
            }
            else{
                util.showActionSnackbar(binding.root,it.message,200000,"Retry"){
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