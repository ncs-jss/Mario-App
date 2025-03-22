package com.ncs.marioapp.UI.EventDetailsScreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ncorti.slidetoact.SlideToActView
import com.ncs.marioapp.Domain.Models.Answer
import com.ncs.marioapp.Domain.Models.Events.EnrollUser
import com.ncs.marioapp.Domain.Models.Events.EventDetails.Question
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.gone
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.isNull
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.visible
import com.ncs.marioapp.Domain.Utility.GlobalUtils
import com.ncs.marioapp.R
import com.ncs.marioapp.UI.EventDetailsScreen.Adapters.SummaryAdapter
import com.ncs.marioapp.UI.MainScreen.Home.HomeViewModel
import com.ncs.marioapp.databinding.FragmentQuestionnaireSummaryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuestionnaireSummaryFragment : Fragment() {

    private var _binding: FragmentQuestionnaireSummaryBinding? = null
    private val binding get() = _binding!!
    private lateinit var summaryAdapter: SummaryAdapter
    private var answers: List<Answer> = emptyList()
    private var questions: List<Question> = emptyList()
    private val viewModel: EventDetailsViewModel by activityViewModels()
    private val util: GlobalUtils.EasyElements by lazy {
        GlobalUtils.EasyElements(requireActivity())
    }

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
    ): View {
        _binding = FragmentQuestionnaireSummaryBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        setupRecyclerView()
        binding.confirmButtonEnroll.onSlideCompleteListener = object : SlideToActView.OnSlideCompleteListener {
            override fun onSlideComplete(view: SlideToActView) {
                val enrollUser=EnrollUser(event_id = viewModel.getEvent()?._id!!, response = answers)
//                viewModel.enrollUser(enrollUser)
                viewModel.checkAndSendTheEmailInvites(enrollUser)
            }
        }
    }

    private fun observeViewModel() {

        viewModel.progressState.observe(viewLifecycleOwner) {
            if (it) {
                binding.progressLayout.visible()
                binding.normalLayout.gone()
            } else {
                binding.progressLayout.gone()
                binding.normalLayout.visible()
            }
        }

        viewModel.enrollResult.observe(viewLifecycleOwner) {
            if (it) {
                util.showSnackbar(binding.root, "Enrolled Successfully", 2000)
                findNavController().navigate(
                    R.id.action_eventQuestionnaireFragment_to_fragment_event_enrolled,
                    null,
                    androidx.navigation.NavOptions.Builder()
                        .setPopUpTo(R.id.eventQuestionnaireFragment, true)
                        .build()
                )
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