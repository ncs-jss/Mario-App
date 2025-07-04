package com.ncs.marioapp.UI.EventDetailsScreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.ncs.marioapp.Domain.Models.Answer
import com.ncs.marioapp.Domain.Models.Question
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.gone
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.isNull
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.visible
import com.ncs.marioapp.Domain.Utility.GlobalUtils
import com.ncs.marioapp.UI.EventDetailsScreen.Adapters.QuestionPagerAdapter
import com.ncs.marioapp.databinding.FragmentEventQuestionnaireBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EventQuestionnaireFragment : Fragment() {
    private  var _binding: FragmentEventQuestionnaireBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: QuestionPagerAdapter
    private val answers = mutableListOf<Answer>()
    private val viewModel: EventDetailsViewModel by activityViewModels()
    private val util: GlobalUtils.EasyElements by lazy {
        GlobalUtils.EasyElements(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentEventQuestionnaireBinding.inflate(inflater, container, false)
//        val questions = listOf(
//            Question(
//                question = "What is your name?",
//                type = "TEXT-RESPONSE"
//            ),
//            Question(
//                question = "What is your favorite color?",
//                type = "MULTI_CHOICE",
//                options = listOf("Red", "Blue", "Green", "Yellow")
//            ),
//            Question(
//                question = "Describe your ideal vacation.",
//                type = "TEXT-RESPONSE"
//            ),
//            Question(
//                question = "Which programming languages do you know?",
//                type = "MULTI_CHOICE",
//                options = listOf("Kotlin", "Java", "Python", "JavaScript", "C++")
//            ),
//            Question(
//                question = "What is your preferred mode of transport?",
//                type = "MULTI_CHOICE",
//                options = listOf("Car", "Bike", "Public Transit", "Walking")
//            ),
//            Question(
//                question = "What are your hobbies?",
//                type = "TEXT-RESPONSE"
//            ),
//        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
        observeViewModel()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(0, systemBars.top, 0, systemBars.bottom)
            insets
        }
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

        viewModel.progressState.observe(viewLifecycleOwner) {

        }

        viewModel.eventDetails.observe(viewLifecycleOwner){
            if (!it.isNull){
                adapter.setQuestions(it.questionnaire.questions)
                updateProgressBar()
            }
        }
    }

    private fun setupViewPager() {

        adapter = QuestionPagerAdapter(this) { answer ->
            answers.add(answer)
            goToNextQuestion()
            updateProgressBar()
        }

        binding.viewPager.adapter = adapter

        binding.btnBack.setOnClickThrottleBounceListener{
            if (binding.viewPager.currentItem==0){
                util.twoBtnDialog("Close Q & A","Closing the Q & A will loose all the answers.","OK","Cancel",{
                    findNavController().popBackStack()
                },{})
            }
            if (binding.viewPager.currentItem >= 0) {
                binding.viewPager.currentItem = binding.viewPager.currentItem - 1
                updateProgressBar()
            }
        }

        binding.close.setOnClickThrottleBounceListener{
            util.twoBtnDialog("Close Q & A","Closing the Q & A will loose all the answers.","OK","Cancel",{
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


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        onBackPress()
    }


    private fun onBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (binding.viewPager.currentItem==0){
                util.twoBtnDialog("Close Q & A","Closing the Q & A will loose all the answers.","OK","Cancel",{
                    findNavController().popBackStack()
                },{})
            }
            if (binding.viewPager.currentItem >= 0) {
                binding.viewPager.currentItem = binding.viewPager.currentItem - 1
                updateProgressBar()
            }
        }
    }



}