package com.ncs.marioapp.UI.EventDetailsScreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.ncs.marioapp.Domain.Models.Answer
import com.ncs.marioapp.Domain.Models.Question
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.marioapp.Domain.Utility.GlobalUtils
import com.ncs.marioapp.UI.EventDetailsScreen.Adapters.QuestionPagerAdapter
import com.ncs.marioapp.databinding.FragmentEventQuestionnaireBinding

class EventQuestionnaireFragment : Fragment() {
    private  var _binding: FragmentEventQuestionnaireBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: QuestionPagerAdapter
    private val answers = mutableListOf<Answer>()

    private val util: GlobalUtils.EasyElements by lazy {
        GlobalUtils.EasyElements(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentEventQuestionnaireBinding.inflate(inflater, container, false)

        setupViewPager()
        val questions = listOf(
            Question(
                question = "What is your name?",
                type = "TEXT-RESPONSE"
            ),
            Question(
                question = "What is your favorite color?",
                type = "MULTI_CHOICE",
                options = listOf("Red", "Blue", "Green", "Yellow")
            ),
            Question(
                question = "Describe your ideal vacation.",
                type = "TEXT-RESPONSE"
            ),
            Question(
                question = "Which programming languages do you know?",
                type = "MULTI_CHOICE",
                options = listOf("Kotlin", "Java", "Python", "JavaScript", "C++")
            ),
            Question(
                question = "What is your preferred mode of transport?",
                type = "MULTI_CHOICE",
                options = listOf("Car", "Bike", "Public Transit", "Walking")
            ),
            Question(
                question = "What are your hobbies?",
                type = "TEXT-RESPONSE"
            ),
        )

        adapter.setQuestions(questions)
        return binding.root
    }

    private fun setupViewPager() {

        adapter = QuestionPagerAdapter(this) { answer ->
            answers.add(answer)
            updateProgressBar()
            goToNextQuestion()
        }
        binding.viewPager.adapter = adapter
        binding.btnBack.setOnClickThrottleBounceListener{
            if (binding.viewPager.currentItem > 0) {
                binding.viewPager.currentItem = binding.viewPager.currentItem - 1
            }

        }
        binding.close.setOnClickThrottleBounceListener{
            util.twoBtnDialog("Close QnA","Closing the QnA will loose all the answers.","Ok","Cancel",{
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
        binding.progressBar.progress = (binding.viewPager.currentItem + 1) * 100 / adapter.itemCount
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}