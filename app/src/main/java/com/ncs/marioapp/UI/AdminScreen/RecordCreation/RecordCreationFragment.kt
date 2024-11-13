package com.ncs.marioapp.UI.AdminScreen.RecordCreation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.ncs.marioapp.Domain.HelperClasses.Utils
import com.ncs.marioapp.Domain.Models.Admin.FormItem
import com.ncs.marioapp.Domain.Models.Admin.FormType
import com.ncs.marioapp.Domain.Models.Admin.Round
import com.ncs.marioapp.Domain.Models.Events.Event
import com.ncs.marioapp.Domain.Models.ServerResult
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.gone
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.visible
import com.ncs.marioapp.databinding.FragmentRecordCreationBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class RecordCreationFragment : Fragment() {

    companion object {
        fun newInstance() = RecordCreationFragment()
    }

    private val binding: FragmentRecordCreationBinding by lazy {
        FragmentRecordCreationBinding.inflate(layoutInflater)
    }
    private val viewModel: RecordCreationViewModel by viewModels()
    private val formValidationUseCase by lazy {
        FormValidationUseCase()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.getEventsResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ServerResult.Failure -> {
                    binding.progress.gone()
                    Snackbar.make(binding.root, result.message, Snackbar.LENGTH_SHORT).show()
                }

                ServerResult.Progress -> {
                    binding.progress.visible()
                }

                is ServerResult.Success -> {
                    binding.progress.gone()
                    val events = result.data
                    setupViews(events)
                }
            }
        }

        viewModel.postRoundResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is ServerResult.Failure -> {
                    binding.progress.gone()
                    Snackbar.make(binding.root, response.message, Snackbar.LENGTH_SHORT).show()
                }

                ServerResult.Progress -> {
                    binding.progress.visible()
                }

                is ServerResult.Success -> {
                    binding.progress.gone()
                    Snackbar.make(binding.root, response.data, Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupViews(events: List<Event>) {
        setupRecyclerview(events)
    }

    lateinit var adapter: FormAdapter
    private fun setupRecyclerview(events: List<Event>) {

        val eventTitles: MutableList<String> = events.map { it.title }.toMutableList()
        eventTitles.add(0, "Select an Option")

        if (eventTitles.isEmpty()) {
            Snackbar.make(binding.root, "No Events Found", Snackbar.LENGTH_SHORT).show()
            return
        }

        val formItems = listOf(

            FormItem("Basic Details", FormType.SEPARATOR),

            FormItem("Event ID", FormType.DROPDOWN, options = eventTitles),
            FormItem("Round Title", FormType.EDIT_TEXT),
            FormItem("Description", FormType.EDIT_TEXT),
            FormItem("Venue", FormType.DROPDOWN, options = listOf("Online", "Offline")),

            FormItem("State", FormType.SEPARATOR),

            FormItem("Currently Live?", FormType.DROPDOWN),
            FormItem("Require Submission?", FormType.DROPDOWN),

            FormItem("Timeline", FormType.SEPARATOR),

            FormItem("Start Time -> College", FormType.DATE_PICKER),
            FormItem("End Time -> College", FormType.DATE_PICKER),
            FormItem("Start Time -> University", FormType.DATE_PICKER),
            FormItem("End Time -> University", FormType.DATE_PICKER),
            FormItem("Same as College", FormType.RADIO),


            FormItem("Submit Form", FormType.BUTTON),
        )

        adapter = FormAdapter(requireContext(), formItems.toMutableList(), { buttonItem, items ->
            Log.d("Items", items.toString())

            val validationResult = formValidationUseCase.validateRequiredFields(items)
            if (validationResult != null) {
                showValidationError(validationResult)
                return@FormAdapter
            } else {
                Snackbar.make(binding.root, "Saving round...", Snackbar.LENGTH_SHORT).show()
                val round = createRound(items)

                lifecycleScope.launch {
                    viewModel.postRound(round)
                }
            }

        }, { pos ->

            val uniStartPos = 11
            val uniEndPos = 12

            if (formItems[pos].value == "True") {
                formItems[pos].value = "False"
                formItems[uniStartPos].value = "Select Date"
                formItems[uniEndPos].value = "Select Date"

            } else {
                formItems[pos].value = "True"
                formItems[uniStartPos].value = formItems[9].value
                formItems[uniEndPos].value = formItems[10].value
            }

            adapter.notifyItemChanged(pos)
            adapter.notifyItemChanged(uniStartPos)
            adapter.notifyItemChanged(uniEndPos)
        })

        binding.requirementsRV.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.requirementsRV.adapter = adapter
        binding.requirementsRV.visible()
    }

    private fun createRound(items: List<FormItem>): Round {


        val eventID = items[1].value
        val roundTitle = items[2].value
        val description = items[3].value
        val venue = items[4].value
        val currentlyLive = items[6].value.toBoolean()
        val requireSubmission = items[7].value.toBoolean()
        val startCollege = items[9].value
        val endCollege = items[10].value
        val startUniversity = items[11].value
        val endUniversity = items[11].value
        val roundID = Utils.generateRandomId()

        val timeLine = mapOf(
            "startCollege" to Utils.convertToFirestoreTimestamp(startCollege)!!,
            "endCollege" to Utils.convertToFirestoreTimestamp(endCollege)!!,
            "startUniversity" to Utils.convertToFirestoreTimestamp(startUniversity)!!,
            "endUniversity" to Utils.convertToFirestoreTimestamp(endUniversity)!!
        )

        return Round(
            eventID = eventID,
            roundTitle = roundTitle,
            description = description,
            venue = venue,
            isLive = currentlyLive,
            requireSubmission = requireSubmission,
            timeLine = timeLine,
            roundID = roundID,
            questionnaireID = ""
        )

    }


    private fun showValidationError(errorMessage: String) {
        Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT).show()
    }


}