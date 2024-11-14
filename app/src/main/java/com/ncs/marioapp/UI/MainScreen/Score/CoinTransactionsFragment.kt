package com.ncs.marioapp.UI.MainScreen.Score


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ncs.marioapp.Domain.Models.Score.Transaction
import com.ncs.marioapp.Domain.Models.ServerResult
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.gone
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.isNull
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.visible
import com.ncs.marioapp.Domain.Utility.GlobalUtils
import com.ncs.marioapp.R
import com.ncs.marioapp.UI.MainScreen.Home.Adapters.TransactionAdapter
import com.ncs.marioapp.UI.MainScreen.MainActivity
import com.ncs.marioapp.databinding.FragmentCoinTransactionsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CoinTransactionsFragment : Fragment(), TransactionAdapter.Callback {

    companion object {
        fun newInstance() = CoinTransactionsFragment()
    }
    private var _binding: FragmentCoinTransactionsBinding? = null
    private val binding get() = _binding!!
    private val activityBinding: MainActivity by lazy {
        (requireActivity() as MainActivity)
    }
    private val viewModel: ScoreViewModel by activityViewModels()
    private val util: GlobalUtils.EasyElements by lazy {
        GlobalUtils.EasyElements(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCoinTransactionsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        activityBinding.binding.actionbar.score.visibility=View.GONE
        activityBinding.binding.actionbar.btnHam.setImageResource(R.drawable.ic_back_arrow)
        activityBinding.binding.actionbar.titleTv.text="Transactions"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.transactionsShimmerLayout.apply {
            startShimmer()
            visibility = View.VISIBLE
        }
        binding.recyclerView.gone()
        viewModel.getMyEvents()
        viewModel.getUserCoinStatement()
        observeViewModel()
        setUpViews()
    }

    private fun setUpViews(){
        binding.swiperefresh.setOnRefreshListener {
            binding.transactionsShimmerLayout.apply {
                startShimmer()
                visibility = View.VISIBLE
            }
            binding.recyclerView.gone()
            viewModel.getUserCoinStatement()
        }
        activityBinding.binding.actionbar.btnHam.setOnClickThrottleBounceListener{
            moveToPrevious()
        }
    }

    private fun observeViewModel(){
        viewModel.progressState.observe(viewLifecycleOwner) {
            if (it) {
                activityBinding.binding.linearProgressIndicator.visible()
            } else {
                activityBinding.binding.linearProgressIndicator.gone()
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner){
            if (!it.isNull) {
                util.showSnackbar(binding.root, it.toString(), 2000)
                viewModel.resetErrorMessage()
            }
        }

        viewModel.coinsStatementResult.observe(viewLifecycleOwner){
            if (!it){
                util.showActionSnackbar(binding.root, "Something went wrong", 2000, "Retry", {
                    viewModel.getUserCoinStatement()
                })
            }
        }

        viewModel.coinsStatement.observe(viewLifecycleOwner){
            if (binding.swiperefresh.isRefreshing){
                binding.swiperefresh.isRefreshing = false
            }
            if (!it.isNull) {
                binding.transactionsShimmerLayout.apply {
                    stopShimmer()
                    visibility = View.GONE
                }
                if (it.isNotEmpty()) {
                    setUpCoinsStatementRV(it)
                }
                else{
                    binding.transactionTv.visible()
                }
            }
        }

    }

    private fun setUpCoinsStatementRV(list: List<Transaction>) {

        binding.recyclerView.visible()
        val recyclerView = binding.recyclerView
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        val transactionAdapter= TransactionAdapter(list.sortedByDescending { it.time }, this)
        recyclerView.adapter = transactionAdapter
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        onBackPress()
    }

    private fun onBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            moveToPrevious()
        }
    }

    fun moveToPrevious(){
        findNavController().navigate(R.id.action_fragment_coin_transactions_to_fragment_score)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onClick(transaction: Transaction) {
        when (transaction.type) {
            "orders" -> {
                val bottomSheet=OrderTransactionBottomSheet(transaction._id)
                bottomSheet.show(childFragmentManager, "bottomSheet")
            }
            "events" -> {
                Log.d("checkEvents", transaction._id)
                viewModel.getEventsResponse.observe(viewLifecycleOwner){ result ->
                    Log.d("checkEvents", result.toString())
                    when(result){
                        is ServerResult.Failure ->{}
                        ServerResult.Progress -> {}
                        is ServerResult.Success -> {
                            if(result.data.success){
                                val events=result.data.events
                                Log.d("checkEvents", events.toString())
                                Log.d("checkEvents", transaction._id)
                                val event=events.find { it._id==transaction._id }
                                val bottomSheet=EventTranactionBottomSheet(event!!)
                                bottomSheet.show(childFragmentManager, "bottomSheet")
                            }
                        }
                    }
                }

            }
            "admin-gift"->{
                util.showSnackbar(binding.root,"You have received these coins as gift from admin", 3000)
            }
            else -> {
                util.showSnackbar(binding.root,"You have received these coins for redeeming a QR Code", 3000)
            }
        }
    }
}