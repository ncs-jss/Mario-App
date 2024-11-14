package com.ncs.marioapp.UI.AdminScreen.QuestionnaireCreation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.ncs.marioapp.Domain.HelperClasses.Utils
import com.ncs.marioapp.Domain.Models.Admin.FormItem
import com.ncs.marioapp.Domain.Models.Admin.FormType
import com.ncs.marioapp.Domain.Models.Admin.Questionnaire
import com.ncs.marioapp.Domain.Models.Admin.Round
import com.ncs.marioapp.Domain.Models.Events.Event
import com.ncs.marioapp.Domain.Models.ServerResult
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.gone
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.visible
import com.ncs.marioapp.R
import com.ncs.marioapp.UI.AdminScreen.RecordCreation.FormAdapter
import com.ncs.marioapp.UI.AdminScreen.RecordCreation.FormValidationUseCase
import com.ncs.marioapp.UI.AdminScreen.RecordCreation.RecordCreationViewModel
import com.ncs.marioapp.databinding.FragmentQuestionnaireCreationBinding
import com.ncs.marioapp.databinding.FragmentRecordCreationBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class QuestionnaireCreationFragment : Fragment() {

    companion object {
        fun newInstance() = QuestionnaireCreationFragment()
    }

    private val binding: FragmentQuestionnaireCreationBinding by lazy {
        FragmentQuestionnaireCreationBinding.inflate(layoutInflater)
    }
    private val viewModel: QuestionnaireViewModel by viewModels()
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
        setupViews()
        setupObservers()
    }

    private fun setupObservers() {

        viewModel.postQuestionnaireResponse.observe(viewLifecycleOwner) { response ->
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

    private fun setupViews() {
        setupRecyclerview()
    }

    lateinit var adapter: FormAdapter
    private fun setupRecyclerview() {

        val formItems = listOf(

            FormItem("Basic Details", FormType.SEPARATOR),

            FormItem("Questionnaire Title", FormType.EDIT_TEXT),



            FormItem("Submit Form", FormType.BUTTON),
        )

        adapter = FormAdapter(requireContext(), formItems.toMutableList(), { buttonItem, items ->
            Log.d("Items", items.toString())

            val validationResult = formValidationUseCase.validateRequiredFields(items)
            if (validationResult != null) {
                showValidationError(validationResult)
                return@FormAdapter
            } else {
                Snackbar.make(binding.root, "Saving questionnaire...", Snackbar.LENGTH_SHORT).show()
                val questionnaire = createQuestionnaire(items)

                lifecycleScope.launch {
                    viewModel.postQuestionnaire(questionnaire)
                }
            }

        }, {})

        binding.requirementsRV.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.requirementsRV.adapter = adapter
        binding.requirementsRV.visible()
    }

    private fun createQuestionnaire(items: List<FormItem>): Questionnaire {

        val queTitle =  items[1].value

        return Questionnaire(
            queID =  Utils.generateRandomId(),
            queTitle=queTitle,
            questions = emptyList()
        )

    }


    private fun showValidationError(errorMessage: String) {
        Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT).show()
    }


}