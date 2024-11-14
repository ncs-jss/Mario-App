package com.ncs.marioapp.UI.AdminScreen.QuestionnaireCreation

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.ncs.marioapp.Domain.HelperClasses.Utils
import com.ncs.marioapp.Domain.Models.Admin.FormItem
import com.ncs.marioapp.Domain.Models.Admin.FormType
import com.ncs.marioapp.Domain.Models.Admin.QuestionItem
import com.ncs.marioapp.Domain.Models.Admin.Questionnaire
import com.ncs.marioapp.Domain.Models.Admin.Round
import com.ncs.marioapp.Domain.Models.Events.Event
import com.ncs.marioapp.Domain.Models.ServerResult
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.gone
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.toast
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.visible
import com.ncs.marioapp.R
import com.ncs.marioapp.UI.AdminScreen.AdminMainActivity
import com.ncs.marioapp.UI.AdminScreen.RecordCreation.DragSwipeCallback
import com.ncs.marioapp.UI.AdminScreen.RecordCreation.FormAdapter
import com.ncs.marioapp.UI.AdminScreen.RecordCreation.FormValidationUseCase
import com.ncs.marioapp.UI.AdminScreen.RecordCreation.QuestionnaireAdapter
import com.ncs.marioapp.databinding.CreateQuestionDialogBinding
import com.ncs.marioapp.databinding.FragmentQuestionnaireCreationBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

    private fun setupViews() {
        setupRecyclerview()

        binding.addQuestion.setOnClickListener {
            showCreateQuestionDialog(requireContext()){
                adapter.items.add(it)
                adapter.notifyItemInserted(adapter.itemCount - 1)
            }
        }

        binding.btnCreateQuestionnaire.setOnClickThrottleBounceListener {
            val quesTite=binding.editText.text.toString().trim()
            if (quesTite.isNullOrEmpty()){
                toast("Questionnaire Title Can't be empty")
            }
            else{
                val questions:MutableList<QuestionItem> = mutableListOf()
                for (question in adapter.items){
                    questions.add(QuestionItem(
                        qID = Utils.generateRandomId(),
                        questionText = question.title,
                        type = question.type.name,
                        options = question.options
                    ))
                }
                val questionnaire=Questionnaire(
                    queID = Utils.generateRandomId(),
                    queTitle = quesTite,
                    questions = questions
                )
                lifecycleScope.launch {
                    viewModel.postQuestionnaire(questionnaire)
                }
            }
        }
    }

    private fun setupObservers() {

        viewModel.postQuestionnaireResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is ServerResult.Failure -> {
                    binding.questionsView.visible()
                    binding.progress.gone()
                    Snackbar.make(binding.root, response.message, Snackbar.LENGTH_SHORT).show()
                }

                ServerResult.Progress -> {
                    binding.questionsView.gone()
                    binding.progress.visible()
                }

                is ServerResult.Success -> {
                    binding.questionsView.visible()
                    binding.progress.gone()
                    Snackbar.make(binding.root, response.data, Snackbar.LENGTH_SHORT).show()
                    startActivity(Intent(requireContext(),AdminMainActivity::class.java))
                    requireActivity().finish()
                }
            }
        }
    }


    fun showCreateQuestionDialog(context: Context, onSubmit:(FormItem) -> Unit): Dialog {
        val binding = CreateQuestionDialogBinding.inflate(LayoutInflater.from(context))
        var selectedItem = "Text Input"
        val spinner = binding.reminderSpinner
        val recyclerView = binding.optionsRv
        val addMoreButton = binding.addOption
        val optionsList = mutableListOf<String>()

        val optionsAdapter = QuesOptionAdapter(optionsList)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = optionsAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedItem = parent?.getItemAtPosition(position).toString()
                if (selectedItem == "Option Selection") {
                    binding.optionsView.visible()
                } else {
                    binding.optionsView.gone()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        addMoreButton.setOnClickListener {
            optionsList.add("")
            optionsAdapter.notifyItemInserted(optionsList.size - 1)
        }


        val dialog = AlertDialog.Builder(context)
            .setView(binding.root)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.show()

        val width = context.resources.getDimensionPixelSize(R.dimen.dialog_width)
        val height = context.resources.getDimensionPixelSize(R.dimen.dialog_height)
        dialog.window?.setLayout(width, height)

        binding.addQuestion.setOnClickThrottleBounceListener{
            val title=binding.quesTitle.text.toString().trim()
            if (title.isNullOrEmpty()){
                toast("Ques Title Can't be empty")
            }
            else {
                if (selectedItem == "Option Selection") {
                    if (optionsAdapter.getOptions().isNullOrEmpty()) {
                        toast("Add Some Options")
                    } else {
                        onSubmit(
                            FormItem(
                                title = title,
                                type = FormType.RADIO,
                                value = "",
                                options = optionsAdapter.getOptions()
                            )
                        )
                        dialog.dismiss()
                    }
                }
                else{
                    onSubmit(
                        FormItem(
                            title = title,
                            type = FormType.EDIT_TEXT,
                            value = "",
                            options = emptyList()
                        )
                    )
                    dialog.dismiss()
                }
            }
        }


        return dialog
    }


    lateinit var adapter: QuestionnaireAdapter
    private fun setupRecyclerview() {

        val formItems : MutableList<FormItem> = mutableListOf()

        adapter = QuestionnaireAdapter(requireContext(), formItems.toMutableList())
        val itemTouchHelper = ItemTouchHelper(DragSwipeCallback(adapter))
        itemTouchHelper.attachToRecyclerView(binding.requirementsRV)
        binding.requirementsRV.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.requirementsRV.adapter = adapter
        binding.requirementsRV.visible()
    }


    private fun showValidationError(errorMessage: String) {
        Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT).show()
    }


}