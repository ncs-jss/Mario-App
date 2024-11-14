package com.ncs.marioapp.UI.MainScreen

import android.Manifest
import android.app.Dialog
import android.os.Bundle
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.provider.MediaStore
import android.view.View
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.anupkumarpanwar.scratchview.ScratchView
import com.anupkumarpanwar.scratchview.ScratchView.IRevealListener
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.ncs.marioapp.Domain.Models.ServerResult
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.gone
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.visible
import com.ncs.marioapp.R
import com.ncs.marioapp.databinding.ActivityQrscannerBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QRScannerActivity : AppCompatActivity() {

    val binding: ActivityQrscannerBinding by lazy {
        ActivityQrscannerBinding.inflate(layoutInflater)
    }
    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        } else {
            startScanning()
        }
    }

    private fun startScanning() {
        binding.barcodeScanner.resume()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScanning()
            } else {
                Toast.makeText(this, "Camera permission is required to scan QR codes", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private val mainViewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkCameraPermission()
        this.onBackPressedDispatcher.addCallback(this) {
            startActivity(Intent(this@QRScannerActivity, MainActivity::class.java))
            finish()

        }
        binding.btnBack.setOnClickThrottleBounceListener {
            onBackPressed()
        }

        setContentView(binding.root)

        mainViewModel.validateScannedQR.observe(this){result ->
            when(result){
                is ServerResult.Failure ->{
                    showError(result.message)
                    binding.linearProgressIndicator.gone()
                    binding.barcodeScanner.resume()
                }
                ServerResult.Progress -> {
                    showLoading()
                }
                is ServerResult.Success ->{
                    Toast.makeText(this,"Scratch the card to win coins and points!!",Toast.LENGTH_LONG).show()
                    binding.linearProgressIndicator.gone()
                    result.data.points?.let { showScratchCardPopup(it,result.data.message) }

                }
            }
        }

        binding.barcodeScanner.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult?) {
                result?.let {
                    mainViewModel.validateScannedQR(it.text)
                    binding.barcodeScanner.pause()
                }
            }

            override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}
        })

        binding.btnGallery.setOnClickThrottleBounceListener {
            openGallery()
        }
    }

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            binding.barcodeScanner.resume()
        }
    }

    override fun onPause() {
        super.onPause()
        binding.barcodeScanner.pause()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            val fileUri = data?.data
            if (fileUri != null) {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, fileUri)
                decodeQRCodeFromBitmap(bitmap,mainViewModel,this@QRScannerActivity)
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGallery() {
        ImagePicker.with(this)
            .galleryOnly()
            .start ()
    }

    private fun showScratchCardPopup(point: Int,message:String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.scratch_card_popup)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val parent = dialog.findViewById<ConstraintLayout>(R.id.screenparent)
        val scratchView = dialog.findViewById<ScratchView>(R.id.scratchView)
        val bg = dialog.findViewById<LottieAnimationView>(R.id.bganim)
        val anim = dialog.findViewById<LottieAnimationView>(R.id.success_anim)
        val coins = dialog.findViewById<TextView>(R.id.coins)
        val coinAnim = dialog.findViewById<LottieAnimationView>(R.id.coin_anim)
        val points = dialog.findViewById<TextView>(R.id.points)
        parent.isEnabled = false
        parent.isClickable = false
        parent.setOnClickThrottleBounceListener {
            dialog.dismiss()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        coins.text = "${point / 5}"
        points.text = "$point"
        scratchView.setStrokeWidth(10)
        scratchView.setRevealListener(object : IRevealListener {
            override fun onRevealed(scratchView: ScratchView) {
                anim.visibility = View.VISIBLE
                coinAnim.playAnimation()
                bg.playAnimation()
                anim.playAnimation()
                parent.isEnabled = true
                parent.isClickable = true
                Toast.makeText(this@QRScannerActivity,message,Toast.LENGTH_SHORT).show()
            }

            override fun onRevealPercentChangedListener(scratchView: ScratchView, percent: Float) {
                if (percent > 0.35f) {
                    scratchView.reveal()

                }
            }
        })

        dialog.show()
    }
    private fun showError(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    private fun showLoading() {
        binding.linearProgressIndicator.visible()
    }

}

private fun decodeQRCodeFromBitmap(bitmap: Bitmap, mainViewModel: MainViewModel, activity: QRScannerActivity) {
    val intArray = IntArray(bitmap.width * bitmap.height)
    bitmap.getPixels(intArray, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

    val source = RGBLuminanceSource(bitmap.width, bitmap.height, intArray)
    val binaryBitmap = BinaryBitmap(HybridBinarizer(source))

    try {
        val result = MultiFormatReader().decode(binaryBitmap)
        mainViewModel.validateScannedQR(result.text)
    } catch (e: NotFoundException) {
        e.printStackTrace()
        Toast.makeText(activity, "No QR code found in the image", Toast.LENGTH_SHORT).show()
    }
}