package com.ncs.marioapp.UI.AdminScreen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.ncs.marioapp.Domain.Models.Banner
import com.ncs.marioapp.Domain.Models.Story
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.gone
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.isNull
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.setOnClickSingleTimeBounceListener
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.visible
import com.ncs.marioapp.Domain.Utility.GlobalUtils
import com.ncs.marioapp.databinding.FragmentStoryBinding
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon
import io.noties.markwon.editor.MarkwonEditor
import io.noties.markwon.editor.MarkwonEditorTextWatcher
import java.util.concurrent.Executors

@AndroidEntryPoint
class StoryFragment : Fragment() {
    private var _binding : FragmentStoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var markwon: Markwon
    private lateinit var editor: MarkwonEditor
    private var storyId:String?=null
    var issueTemplate = ""
    private val viewModel: AdminViewModel by viewModels()
    private val util: GlobalUtils.EasyElements by lazy {
        GlobalUtils.EasyElements(requireContext())
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentStoryBinding.inflate(inflater, container, false)
        bindObserver()
        markwon = Markwon.create(requireContext())
        editor = MarkwonEditor.create(markwon)


        issueTemplate = "" +
                "**Description:**\n" +
                "[Provide a detailed description of the issue. Include any relevant background information, steps to reproduce, and expected vs actual behavior.]\n" +
                "\n" +
                "**Steps to Reproduce:**\n" +
                "1. [Step 1]\n" +
                "2. [Step 2]\n" +
                "3. [Step 3]\n" +
                "   ...\n" +
                "\n" +
                "**Expected Result:**\n" +
                "[Clearly describe what you expected to happen.]\n" +
                "\n" +
                "**Actual Result:**\n" +
                "[Describe what actually happened. Include any error messages or unexpected behavior.]\n" +
                "\n" +
                "**Screenshots / Attachments:**\n" +
                "[If applicable, include screenshots or attachments that help illustrate the issue.]\n" +
                "\n" +
                "**Additional Information:**\n" +
                "[Any additional context, information, or configuration details that might be relevant.]\n" +
                "\n" +
                "**Related Issues:**\n" +
                "[List any related issues or dependencies.]\n" +
                "\n" +
                "**Notes for Reviewers:**\n" +
                "\n"
        binding.editTextMarkdown.setText(issueTemplate)
        val executorService = Executors.newCachedThreadPool()
        binding.editTextMarkdown.addTextChangedListener(
            MarkwonEditorTextWatcher.withPreRender(editor,executorService,binding.editTextMarkdown)
        )

        binding.buttonRenderMarkdown.setOnClickListener {
            val markdownText = binding.editTextMarkdown.text.toString()
            markwon.setMarkdown(binding.textViewMarkdown, markdownText)
        }

        binding.buttonPickImage.setOnClickSingleTimeBounceListener {
            pickImageFromGallery()
        }
        binding.addBanner.setOnClickThrottleBounceListener{
            createBanner()
        }
        binding.uploadButton.setOnClickThrottleBounceListener{
            createStory()
        }
        binding.btnBack.setOnClickThrottleBounceListener {
            findNavController().popBackStack()
        }
        return binding.root
    }


    private fun pickImageFromGallery() {
        ImagePicker.with(this)
            .galleryOnly()
            .start()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            val fileUri = data?.data
            if (fileUri != null) {
                binding.imageView.setImageURI(fileUri)
                viewModel.uploadImageToCloudinary(fileUri)
                Log.d("Image URL","SUCCESS")
            }
        }
        else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()

        }
    }
    fun createBanner(){
        if(!storyId.isNullOrEmpty()||!viewModel.imageUrlLiveData.value.isNull){
            storyId = generateRandomId(14)
            val banner =                Banner(
                    generateRandomId(12),
                    System.currentTimeMillis(),
                viewModel.imageUrlLiveData.value!!,
                    "",
                    "story",
                    storyId!!
                )
            Log.d("Banner",banner.toString())
            if (banner != null) {
                viewModel.addBanner(banner)
            }
        }
        else{
            Toast.makeText(requireContext(),"First Add a image",Toast.LENGTH_SHORT).show()
        }
    }

    fun createStory(){
        if (storyId.isNullOrEmpty()){
            Toast.makeText(requireContext(),"First Add a banner",Toast.LENGTH_SHORT).show()
        }else{
            if(binding.editTextMarkdown.text.isNullOrEmpty()){
                Toast.makeText(requireContext(),"Add story text",Toast.LENGTH_SHORT).show()
                return
            }else{
                val story = Story(
                    storyId!!,
                    binding.editTextMarkdown.text.toString(),
                )
                viewModel.addStory(story)
            }
        }
    }

    fun generateRandomId(length: Int = 8): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..length)
            .map { chars.random() }
            .joinToString("")
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun bindObserver(){
        viewModel.errorMessage.observe(requireActivity()) {
            showError(it)
            binding.progress.gone()
        }

        viewModel.addStoryResult.observe(requireActivity()){
            if(it){
                Toast.makeText(requireContext(),"Story added",Toast.LENGTH_SHORT).show()
                storyId = null
                binding.editTextMarkdown.setText(issueTemplate)
                binding.imageView.setImageDrawable(null)

            }
        }
        viewModel.progressState.observe(requireActivity()) {
            if (it) {
                binding.progress.visible()
            } else {
                binding.progress.gone()
            }
        }
        viewModel.imageUrlLiveData.observe(requireActivity()){
            Toast.makeText(requireContext(),"Image URL received$it",Toast.LENGTH_SHORT).show()
        }
        viewModel.addBannerResult.observe(requireActivity()){
            if(it){
                Toast.makeText(requireContext(),"Banner added",Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun showError(message: String?) {
        util.showSnackbar(binding.root,message!!,2000)
    }
}