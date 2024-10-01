package com.ncs.mario.Domain.Presentation.QR.ui.qrscan

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.common.moduleinstall.ModuleInstall
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.ncs.mario.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.mario.R
import com.ncs.mario.databinding.FragmentQRScanBinding

class QRScanFragment : Fragment(R.id.q_r_scan) {

    companion object {
        fun newInstance() = QRScanFragment()
    }
    private var _binding: FragmentQRScanBinding? = null
    private val binding get() = _binding!!

    private val viewModel: QRScanViewModel by viewModels()
    private var isScannerInstalled:Boolean = false
    private lateinit var scanner: GmsBarcodeScanner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQRScanBinding.inflate(inflater, container, false)

        installGoogleScanner()
        return binding.root
    }

    private fun installGoogleScanner() {
        val moduleInstall = ModuleInstall.getClient(requireContext())
        val moduleInstallRequest =
            ModuleInstallRequest
                .newBuilder()
                .addApi(GmsBarcodeScanning.getClient(requireContext()))
                .build()
        moduleInstall.installModules(moduleInstallRequest).addOnSuccessListener {
            isScannerInstalled = true
        }.addOnFailureListener {
            Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
        }


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val options = initializeGoogleScanner()
        scanner = GmsBarcodeScanning.getClient(requireContext(), options)
        registerUIListener()
    }

    private fun registerUIListener() {
        binding.scanQrBtn.setOnClickThrottleBounceListener{
            if(isScannerInstalled){
                startScanning()
            }
            else{
                Toast.makeText(requireContext(),"Try Again",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initializeGoogleScanner(): GmsBarcodeScannerOptions {
        return GmsBarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_QR_CODE).enableAutoZoom().build()
    }
    private fun startScanning() {
        scanner.startScan().addOnSuccessListener {
            Toast.makeText(requireContext(),it.rawValue,Toast.LENGTH_SHORT).show()
        }
            .addOnFailureListener {
                Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
            }
            .addOnCanceledListener {
                Toast.makeText(requireContext(),"Cancelled",Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null

    }
}
