package com.ncs.marioapp.UI.EventDetailsScreen.RoundQuestionnaire

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ncs.marioapp.R
import com.ncs.marioapp.UI.EventDetailsScreen.EventDetailsViewModel
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.ncorti.slidetoact.SlideToActView
import com.ncs.marioapp.Domain.HelperClasses.PrefManager
import com.ncs.marioapp.Domain.HelperClasses.Utils
import com.ncs.marioapp.Domain.Models.Answer
import com.ncs.marioapp.Domain.Models.Events.EnrollUser
import com.ncs.marioapp.Domain.Models.Events.EventDetails.Question
import com.ncs.marioapp.Domain.Models.Events.EventDetails.Submission
import com.ncs.marioapp.Domain.Models.ServerResult
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.gone
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.isNull
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.visible
import com.ncs.marioapp.Domain.Utility.GlobalUtils
import com.ncs.marioapp.UI.EventDetailsScreen.Adapters.SummaryAdapter
import com.ncs.marioapp.databinding.FragmentQuestionnaireSummaryBinding
import com.ncs.marioapp.databinding.FragmentRoundsQuestionnaireSummaryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RoundsQuestionnaireSummaryFragment : Fragment() {

    private var _binding: FragmentRoundsQuestionnaireSummaryBinding? = null
    private val binding get() = _binding!!
    private lateinit var summaryAdapter: SummaryAdapter
    private var answers: List<Answer> = emptyList()
    private var questions: List<Question> = emptyList()
    private val viewModel: EventDetailsViewModel by activityViewModels()
    private val util: GlobalUtils.EasyElements by lazy {
        GlobalUtils.EasyElements(requireActivity())
    }

    companion object {
        fun newInstance(answers: List<Answer>, questions: List<Question>): RoundsQuestionnaireSummaryFragment {
            val fragment = RoundsQuestionnaireSummaryFragment()
            fragment.answers = answers
            fragment.questions = questions
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoundsQuestionnaireSummaryBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        setupRecyclerView()
        binding.submit.setOnClickThrottleBounceListener{
            val response= Submission(
                eventID = viewModel.getEvent()?._id!!,
                questionnaireID = viewModel.getQuestionnaireId()!!,
                roundID = viewModel.getRoundId()!!,
                submissionID = Utils.generateRandomId(),
                userID = PrefManager.getUserID()!!,
                response = answers,
            )

            viewModel.postSubmission(response)
        }
    }

    private fun observeViewModel() {

        viewModel.postSubmissionResult.observe(viewLifecycleOwner){res->
            when(res){
                is ServerResult.Failure -> {
                    binding.summaryView.visible()
                    binding.progressView.gone()
                    Snackbar.make(binding.root, res.message, Snackbar.LENGTH_SHORT).show()
                }

                ServerResult.Progress -> {
                    binding.summaryView.gone()
                    binding.progressView.visible()
                }

                is ServerResult.Success -> {
                    viewModel.getAllSubmissionsForRounds(eventID = viewModel.getEvent()!!._id, userId = PrefManager.getUserID()!!)
                    binding.summaryView.visible()
                    binding.progressView.gone()
                    findNavController().navigate(R.id.action_fragment_round_questionnaire_to_fragment_event_details)
                }
            }
        }

        viewModel.progressState.observe(viewLifecycleOwner) {
            if (it) {
                binding.linearProgressIndicator.visible()
            } else {
                binding.linearProgressIndicator.gone()
            }
        }

        viewModel.enrollResult.observe(viewLifecycleOwner) {
            if (it) {
                util.showSnackbar(binding.root, "Enrolled Successfully", 2000)
                findNavController().navigate(R.id.action_eventQuestionnaireFragment_to_fragment_event_enrolled)
            }
        }

        viewModel.normalErrorMessage.observe(viewLifecycleOwner) {
            if (!it.isNull) {
                util.showSnackbar(binding.root, it.toString(), 2000)
                viewModel.resetErrorMessage()
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