package com.ncs.mario.UI.MyRedemptionsScreen

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ncs.mario.Domain.Utility.ExtensionsUtil.gone
import com.ncs.mario.Domain.Utility.GlobalUtils
import com.ncs.mario.R
import com.ncs.mario.databinding.ActivityMainBinding
import com.ncs.mario.databinding.ActivityMyRedemptionsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyRedemptionsActivity : AppCompatActivity(){

    val binding: ActivityMyRedemptionsBinding by lazy {
        ActivityMyRedemptionsBinding.inflate(layoutInflater)
    }
    private val util: GlobalUtils.EasyElements by lazy {
        GlobalUtils.EasyElements(this@MyRedemptionsActivity)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setUpViews()
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