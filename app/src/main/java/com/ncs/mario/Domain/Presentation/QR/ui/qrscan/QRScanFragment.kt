package com.ncs.mario.Domain.Presentation.QR.ui.qrscan

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ncs.mario.databinding.FragmentQRScanBinding

class QRScanFragment : Fragment() {

    companion object {
        fun newInstance() = QRScanFragment()
    }
    private var _binding: FragmentQRScanBinding? = null
    private val binding get() = _binding!!

    private val viewModel: QRScanViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQRScanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null

    }

}