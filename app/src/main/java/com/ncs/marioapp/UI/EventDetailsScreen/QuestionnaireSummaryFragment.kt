package com.ncs.marioapp.UI.EventDetailsScreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.ncorti.slidetoact.SlideToActView
import com.ncs.marioapp.Domain.Models.Answer
import com.ncs.marioapp.Domain.Models.Question
import com.ncs.marioapp.UI.EventDetailsScreen.Adapters.SummaryAdapter
import com.ncs.marioapp.databinding.FragmentQuestionnaireSummaryBinding

class QuestionnaireSummaryFragment : Fragment() {

    private var _binding: FragmentQuestionnaireSummaryBinding? = null
    private val binding get() = _binding!!
    private lateinit var summaryAdapter: SummaryAdapter
    private var answers: List<Answer> = emptyList()
    private var questions: List<Question> = emptyList()

    companion object {
        fun newInstance(answers: List<Answer>, questions: List<Question>): QuestionnaireSummaryFragment {
            val fragment = QuestionnaireSummaryFragment()
            fragment.answers = answers
            fragment.questions = questions
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentQuestionnaireSummaryBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        binding.confirmButtonEnroll.onSlideCompleteListener = object : SlideToActView.OnSlideCompleteListener {
            override fun onSlideComplete(view: SlideToActView) {
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun setupRecyclerView() {
        summaryAdapter = SummaryAdapter(answers,questions)
        binding.recyclerViewSummary.adapter = summaryAdapter
        binding.recyclerViewSummary.layoutManager = LinearLayoutManager(requireContext())
    }

}