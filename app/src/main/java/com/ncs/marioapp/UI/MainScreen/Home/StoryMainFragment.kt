package com.ncs.marioapp.UI.MainScreen.Home

import android.os.Bundle
import android.os.CountDownTimer
import android.view.GestureDetector
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.gone
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.marioapp.R
import com.ncs.marioapp.UI.MainScreen.MainActivity
import com.ncs.marioapp.databinding.FragmentStoryMainBinding
import io.noties.markwon.Markwon
import io.noties.markwon.editor.MarkwonEditor

class StoryMainFragment : Fragment() {
    private var _binding: FragmentStoryMainBinding? = null
    private val binding get() =_binding!!
    private lateinit var markwon: Markwon
    private lateinit var editor: MarkwonEditor
    private lateinit var gestureDetector: GestureDetector
    private val viewModel: HomeViewModel by activityViewModels()
    private val activityBinding: MainActivity by lazy {
        (requireActivity() as MainActivity)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentStoryMainBinding.inflate(inflater,container,false)
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    onBackPressed()
                }
            }
        )
        gestureDetectorFun()

        setUpView()

        val bundle = arguments?.getString("storyText")
        markwon = Markwon.create(requireContext())
        editor = MarkwonEditor.create(markwon)
        if (bundle != null) {
            markwon.setMarkdown(binding.storyText, bundle)
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        activityBinding.binding.actionbar.root.gone()
        activityBinding.binding.bottomNavigationView.gone()
        activityBinding.binding.linearProgressIndicator.gone()
    }

    private fun setUpView() {
        binding.topLayout.setOnTouchListener { v, event ->
            gestureDetector.onTouchEvent(event)
        }
        binding.back.setOnClickThrottleBounceListener{
            onBackPressed()
        }
        binding.storyText.setOnTouchListener { v, event ->
            gestureDetector.onTouchEvent(event)

        }
    }

    private fun gestureDetectorFun() {
        gestureDetector = GestureDetector(requireContext(), object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent): Boolean {
                return true
            }

            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                if (e1 != null && e2 != null && e2.y - e1.y > 100) {
                    onBackPressed()
                    return true
                }
                return false
            }
        })
    }

    fun onBackPressed() {
        findNavController().navigate(R.id.action_fragment_story_main_to_fragment_home)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        onBackPress()
    }



    private fun onBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}