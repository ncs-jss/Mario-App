package com.ncs.marioapp.UI.EventDetailsScreen.RoundQuestionnaire

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.ncs.marioapp.Domain.Models.Answer
import com.ncs.marioapp.Domain.Models.Events.EventDetails.EventQuestionnaire
import com.ncs.marioapp.Domain.Models.Events.EventDetails.Question
import com.ncs.marioapp.Domain.Models.ServerResult
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.gone
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.isNull
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.visible
import com.ncs.marioapp.Domain.Utility.GlobalUtils
import com.ncs.marioapp.UI.AdminScreen.AdminMainActivity
import com.ncs.marioapp.UI.EventDetailsScreen.Adapters.QuestionPagerAdapter
import com.ncs.marioapp.UI.EventDetailsScreen.EventDetailsViewModel
import com.ncs.marioapp.databinding.FragmentEventQuestionnaireBinding
import com.ncs.marioapp.databinding.FragmentRoundQuestionnaireBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RoundQuestionnaireFragment : Fragment() {
    private  var _binding: FragmentRoundQuestionnaireBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: RoundsQuestionAdapter
    private val answers = mutableListOf<Answer>()
    private val viewModel: EventDetailsViewModel by activityViewModels()
    private val util: GlobalUtils.EasyElements by lazy {
        GlobalUtils.EasyElements(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentRoundQuestionnaireBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val questionnaireID = arguments?.getString("questionnaireID")
        viewModel.getAllQuestionnairesById(questionnaireID!!)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(0, systemBars.top, 0, systemBars.bottom)
            insets
        }
        setupViewPager()
        observeViewModel()
    }

    private fun observeViewModel(){
        viewModel.errorMessage.observe(viewLifecycleOwner){
            if (!it.isNull) {
                util.showActionSnackbar(binding.root, it.toString(), 4000, "Retry") {
                    viewModel.getEventDetails(viewModel.getEvent()!!._id)
                }
                viewModel.resetErrorMessage()
            }
        }

        viewModel.normalErrorMessage.observe(viewLifecycleOwner){
            if (!it.isNull) {
                util.showActionSnackbar(binding.root, it.toString(), 4000, "Retry") {
                    viewModel.getEventDetails(viewModel.getEvent()!!._id)
                }
                viewModel.resetErrorMessage()
            }
        }

        viewModel.getQuestionnaireForRound.observe(viewLifecycleOwner){res->
            when(res){
                is ServerResult.Failure -> {
                    binding.quesView.visible()
                    binding.progressView.gone()
                    Snackbar.make(binding.root, res.message, Snackbar.LENGTH_SHORT).show()
                    if (res.message=="No document found with the provided queID"){
                        findNavController().popBackStack()
                    }
                }

                ServerResult.Progress -> {
                    binding.quesView.gone()
                    binding.progressView.visible()
                }

                is ServerResult.Success -> {
                    binding.quesView.visible()
                    binding.progressView.gone()
                    val ques : MutableList<Question> = mutableListOf()
                    for (q in res.data.questions){
                        ques.add(Question(options = q.options, question = q.questionText, type = if (q.type=="RADIO") "MULTI_CHOICE" else "TEXT-RESPONSE"))
                    }
                    adapter.setQuestions(ques)
                    updateProgressBar()
                }
            }
        }

        viewModel.eventDetails.observe(viewLifecycleOwner){
            if (!it.isNull){
                adapter.setQuestions(it.questionnaire.questions)
                updateProgressBar()
            }
        }
    }

    private fun setupViewPager() {

        adapter = RoundsQuestionAdapter(this) { answer ->
            answers.add(answer)
            goToNextQuestion()
            updateProgressBar()
        }

        binding.viewPager.adapter = adapter

        binding.btnBack.setOnClickThrottleBounceListener{
            if (binding.viewPager.currentItem==0){
                util.twoBtnDialog("Close","Closing this will loose all the answers.","OK","Cancel",{
                    findNavController().popBackStack()
                },{})
            }
            if (binding.viewPager.currentItem >= 0) {
                binding.viewPager.currentItem = binding.viewPager.currentItem - 1
                updateProgressBar()
            }
        }

        binding.close.setOnClickThrottleBounceListener{
            util.twoBtnDialog("Close","Closing this will loose all the answers.","OK","Cancel",{
                findNavController().popBackStack()
            },{})
        }

        binding.viewPager.isUserInputEnabled = false
    }

    private fun goToNextQuestion() {
        val nextItem = binding.viewPager.currentItem + 1
        if (nextItem < adapter.itemCount) {
            binding.viewPager.setCurrentItem(nextItem, true)
        } else {
            showSummaryPage()
        }
    }

    private fun showSummaryPage() {
        adapter.setSummaryPage(answers)
        binding.viewPager.setCurrentItem(adapter.itemCount - 1, true)
    }

    private fun updateProgressBar() {
        val totalQuestions = viewModel.getNumberOfQuestions()
        val progress = (binding.viewPager.currentItem + 1) * 100 / (totalQuestions + 1)
        binding.progressBar.progress = progress
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}