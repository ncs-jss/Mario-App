package com.ncs.marioapp.UI.MainScreen.Home

import android.os.Bundle
import android.os.CountDownTimer
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.marioapp.R
import com.ncs.marioapp.databinding.FragmentStoryMainBinding
import io.noties.markwon.Markwon
import io.noties.markwon.editor.MarkwonEditor

class StoryMainFragment : Fragment() {
    private var _binding: FragmentStoryMainBinding? = null
    private val binding get() =_binding!!
    private lateinit var markwon: Markwon
    private lateinit var editor: MarkwonEditor
    private var countDownTimer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 10_000L
    private var timerIsRunning = false
    private lateinit var gestureDetector: GestureDetector
    private val viewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentStoryMainBinding.inflate(inflater,container,false)
        startTimer()
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

    private fun setUpView() {
        binding.topLayout.setOnTouchListener { v, event ->
            gestureDetector.onTouchEvent(event)
        }
        binding.back.setOnClickThrottleBounceListener{
            onBackPressed()
        }
        binding.storyText.setOnTouchListener { v, event ->

            gestureDetector.onTouchEvent(event)
            when(event.action) {

                MotionEvent.ACTION_DOWN -> {

                    pauseTimer()
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    resumeTimer()
                    true
                }
                else -> false
            }
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
        viewModel._story.value = null
        val bindO = requireActivity().findViewById<FragmentContainerView>(R.id.storyFragment)
        bindO.visibility = View.GONE
        countDownTimer?.cancel()
        timerIsRunning = false
        parentFragmentManager.beginTransaction()
            .setCustomAnimations(
                me.shouheng.utils.R.anim.slide_top_to_bottom,
                0
            )
            .remove(this)
            .commit()
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 5) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateProgressBar()
            }

            override fun onFinish() {
                timerIsRunning = false
                performCompletionTask()
            }
        }.start()
        timerIsRunning = true
    }

    private fun pauseTimer() {
        countDownTimer?.cancel()
        timerIsRunning = false
    }

    private fun resumeTimer() {
        if (!timerIsRunning) {
            startTimer()
        }
    }


    private fun updateProgressBar() {
        val progress = ((10_000L - timeLeftInMillis) / 10_000L.toFloat() * 100).toInt()
        binding.progressBar.progress = progress
    }

    private fun performCompletionTask() {
        onBackPressed()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        countDownTimer?.cancel()
        timerIsRunning = false

    }

}