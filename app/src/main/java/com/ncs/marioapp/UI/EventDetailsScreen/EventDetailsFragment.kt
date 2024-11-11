package com.ncs.marioapp.UI.EventDetailsScreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.marioapp.R
import com.ncs.marioapp.databinding.FragmentEventDetailsBinding


class EventDetailsFragment : Fragment() {

    lateinit var binding: FragmentEventDetailsBinding
    private var _binding : FragmentEventDetailsBinding? =null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEventDetailsBinding.inflate(inflater, container, false)
        return binding.root
        _binding = FragmentEventDetailsBinding.inflate(inflater, container, false)
        binding.enroll.setOnClickThrottleBounceListener{
            findNavController().navigate(R.id.action_fragment_event_details_to_eventQuestionnaireFragment)
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
    }

    private fun setUpViews(){

    }



}