package com.ncs.marioapp.UI.EventDetailsScreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.ncs.marioapp.Domain.Models.Answer
import com.ncs.marioapp.Domain.Models.Events.EventDetails.Question
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.marioapp.UI.EventDetailsScreen.Adapters.OptionsAdapter
import com.ncs.marioapp.databinding.FragmentQuestionBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuestionFragment : Fragment() {
    private lateinit var binding: FragmentQuestionBinding
    private lateinit var question: Question
    private var onAnswerSubmitted: ((Answer) -> Unit)? = null
    private var selectedAnswer: String = ""

    companion object {
        fun newInstance(question: Question, onAnswerSubmitted: (Answer) -> Unit): QuestionFragment {
            val fragment = QuestionFragment()
            fragment.question = question
            fragment.onAnswerSubmitted = onAnswerSubmitted
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuestionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvQuestion.text = question.question

        if (question.type == "TEXT-RESPONSE") {
            setupTextResponse()
        } else if (question.type == "MULTI_CHOICE") {
            setupMultiChoice()
        }

        binding.continueBtn.setOnClickThrottleBounceListener {
            val answerText = if (question.type == "TEXT-RESPONSE") {
                binding.answer.text.toString()
            } else {
                selectedAnswer
            }

            if (answerText.isNotEmpty()) {
                onAnswerSubmitted?.invoke(Answer(question.question, answerText))
            }
            else{
                Toast.makeText(requireContext(), "Answer can't be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupTextResponse() {
        binding.answer.visibility = View.VISIBLE
        binding.recyclerViewOptions.visibility = View.GONE
    }

    private fun setupMultiChoice() {
        binding.answer.visibility = View.GONE
        binding.recyclerViewOptions.visibility = View.VISIBLE

        val optionsAdapter = OptionsAdapter(question.options) { selectedOption ->
            selectedAnswer = selectedOption
        }

        binding.recyclerViewOptions.adapter = optionsAdapter
        binding.recyclerViewOptions.layoutManager = LinearLayoutManager(requireContext())
    }
}
