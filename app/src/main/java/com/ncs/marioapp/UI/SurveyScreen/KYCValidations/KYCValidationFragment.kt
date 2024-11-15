package com.ncs.marioapp.UI.SurveyScreen.KYCValidations

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.ncs.marioapp.Domain.HelperClasses.PrefManager
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.gone
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.isNull
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.showProgressDialog
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.visible
import com.ncs.marioapp.Domain.Utility.GlobalUtils
import com.ncs.marioapp.R
import com.ncs.marioapp.UI.StartScreen.StartScreen
import com.ncs.marioapp.UI.SurveyScreen.SurveyViewModel
import com.ncs.marioapp.UI.WaitScreen.WaitActivity
import com.ncs.marioapp.databinding.FragmentKYCValidationBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class KYCValidationFragment : Fragment() {

    lateinit var binding: FragmentKYCValidationBinding
    private val surveyViewModel: SurveyViewModel by activityViewModels()
    private val CAMERA_PERMISSION_REQUEST = 100
    private val REQUEST_USER_IMAGE_CAPTURE = 1
    private var capturedUserImageUri: Uri? = null
    private val REQUEST_COLLEGE_ID_IMAGE_CAPTURE = 2
    private var capturedCollegeIDImageUri: Uri? = null
    private val util: GlobalUtils.EasyElements by lazy {
        GlobalUtils.EasyElements(requireActivity())
    }
    lateinit var dialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentKYCValidationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        surveyViewModel.setCurrentStep(SurveyViewModel.SurveyStep.KYC_DETAILS)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        onBackPress()
    }

    private fun observeViewModel(){

        surveyViewModel.profileCreateResult.observe(viewLifecycleOwner, Observer { result ->
            if (result){
                surveyViewModel.uploadUserImage(uri = Uri.parse(surveyViewModel.userSelfie.value!!), context = requireContext())
            }
        })

//        surveyViewModel.progressStateImageUpload.observe(viewLifecycleOwner, Observer { isLoading ->
//            if (isLoading) {
//                setUpLoader(true,"Uploading the images...")
//            } else {
//                setUpLoader(false,"")
//            }
//        })

//        surveyViewModel.progressState.observe(viewLifecycleOwner, Observer { isLoading ->
//            if (isLoading) {
//                setUpLoader(true,"Please wait...")
//            } else {
//                setUpLoader(false,"")
//            }
//        })

        surveyViewModel.finalProgressState.observe(viewLifecycleOwner, Observer { isLoading ->
            if (isLoading) {
                setUpLoader(true,"Please wait...")
            } else {
                setUpLoader(false,"")
            }
        })

        surveyViewModel.errorMessageKYCDetails.observe(viewLifecycleOwner, Observer { message ->
            if (message != null) {
                util.showSnackbar(binding.root,message!!,2000)
                surveyViewModel.resetErrorMessageKYCDetails()
            }
        })

        surveyViewModel.kycDetailsPageResult.observe(viewLifecycleOwner, Observer { result ->
            if (result){
                surveyViewModel.resetKYCDetailsPageResult()
                surveyViewModel.resetErrorMessageKYCDetails()
                val userSurvey= PrefManager.getUserSurvey()!!
                userSurvey.userImg=surveyViewModel.userSelfie.value!!
                userSurvey.collegeIdImg=surveyViewModel.userCollegeID.value!!
                PrefManager.setUserSurvey(userSurvey)
                Log.d("usercheck","${PrefManager.getUserSurvey()}")
                startActivity(Intent(requireContext(),WaitActivity::class.java))
                requireActivity().finish()
            }
        })
        surveyViewModel.userSelfie.observe(viewLifecycleOwner) {
            if (!it.isNull && it!=""){
                binding.userDP.gone()
                binding.userDPLayout.root.visible()
                val imageBitmap = handleImageRotation(Uri.parse(it!!))
                binding.userDPLayout.imgPreview.setImageBitmap(imageBitmap)
                binding.userDPLayout.imgPreview.setBackgroundResource(0)
            }
        }

        surveyViewModel.userCollegeID.observe(viewLifecycleOwner) {
            if (!it.isNull && it!=""){
                binding.collegeID.gone()
                binding.collegeIDlayout.root.visible()
                val imageBitmap = handleImageRotation(Uri.parse(it!!))
                binding.collegeIDlayout.imgPreview.setImageBitmap(imageBitmap)
                binding.collegeIDlayout.imgPreview.setBackgroundResource(0)
            }
        }
    }

    private fun setUpViews(){
        binding.userDPLayout.root.setOnClickThrottleBounceListener{
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_REQUEST
                )
                util.showSnackbar(binding.root,"Camera permission is required",2000)
            } else {
                pickUserImage()
            }
        }
        binding.collegeIDlayout.root.setOnClickThrottleBounceListener{
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_REQUEST
                )
                util.showSnackbar(binding.root,"Camera permission is required",2000)
            } else {
                pickCollegeIDImage()
            }
        }
        binding.userDP.setOnClickThrottleBounceListener{
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_REQUEST
                )
                util.showSnackbar(binding.root,"Camera permission is required",2000)
            } else {
                pickUserImage()
            }
        }
        binding.collegeID.setOnClickThrottleBounceListener{
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_REQUEST
                )
                util.showSnackbar(binding.root,"Camera permission is required",2000)
            } else {
                pickCollegeIDImage()
            }
        }
        binding.btnBack.setOnClickThrottleBounceListener {
            moveToPrevious()
        }
        binding.btnNext.setOnClickThrottleBounceListener {
            surveyViewModel.validateInputsOnKYCDetailsPage()
        }
    }


    private fun pickUserImage() {
        val options = arrayOf<CharSequence>("Take Selfie", "Cancel")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Click your selfie in a well lit environment")
        builder.setItems(options) { dialog, item ->
            when (options[item]) {
                "Take Selfie" -> {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                    capturedUserImageUri = FileProvider.getUriForFile(
                        requireContext(),
                        "${requireContext().packageName}.provider",
                        createImageFile("User_Selfie")
                    )

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, capturedUserImageUri)
                    startActivityForResult(intent, REQUEST_USER_IMAGE_CAPTURE)
                }

                "Cancel" -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }


    private fun pickCollegeIDImage() {
        val options = arrayOf<CharSequence>("Take Photo", "Cancel")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Click photo of college id from front side")
        builder.setItems(options) { dialog, item ->
            when (options[item]) {
                "Take Photo" -> {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                    capturedCollegeIDImageUri = FileProvider.getUriForFile(
                        requireContext(),
                        "${requireContext().packageName}.provider",
                        createImageFile("College_ID")
                    )

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, capturedCollegeIDImageUri)
                    startActivityForResult(intent, REQUEST_COLLEGE_ID_IMAGE_CAPTURE)
                }

                "Cancel" -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }
    private fun createImageFile(type: String): File {
        val storageDir: File = File(requireContext().filesDir, "images")
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
        return File(storageDir, "NCS_MARIO-Snap-${type}-${System.currentTimeMillis()}.jpeg")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_USER_IMAGE_CAPTURE -> {
                    capturedUserImageUri?.let { uri ->
                        surveyViewModel.setUserSelfie(uri.toString())
                        PrefManager.setUserDPCacheData(uri.toString())
                    }
                }
                REQUEST_COLLEGE_ID_IMAGE_CAPTURE -> {
                    capturedCollegeIDImageUri?.let { uri ->
                        surveyViewModel.setUserCollegeID(uri.toString())
                        PrefManager.setCollegeIDCacheData(uri.toString())
                    }
                }
            }
        }
    }
    private fun handleImageRotation(imageUri: Uri): Bitmap? {
        val inputStream = requireContext().contentResolver.openInputStream(imageUri)
        val bitmap = BitmapFactory.decodeStream(inputStream)

        val exif = ExifInterface(requireContext().contentResolver.openInputStream(imageUri)!!)
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)

        val rotatedBitmap = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270f)
            ExifInterface.ORIENTATION_NORMAL -> bitmap
            else -> bitmap // Default case
        }

        return rotatedBitmap?.let { maintainAspectRatio(it) }
    }

    private fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix().apply {
            postRotate(angle)
        }
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    private fun maintainAspectRatio(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val maxSize = 1024

        val scale = if (width > height) {
            maxSize.toFloat() / width
        } else {
            maxSize.toFloat() / height
        }

        val newWidth = (width * scale).toInt()
        val newHeight = (height * scale).toInt()

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }


    private fun setUpLoader(show: Boolean, message: String) {
        if (show) {
            if (this::dialog.isInitialized && dialog.isShowing) {
                dialog.dismiss()
            }
            dialog = showProgressDialog(requireContext(), message)
            dialog.show()
        } else {
            if (this::dialog.isInitialized && dialog.isShowing) {
                dialog.dismiss()
            }
        }
    }


    private fun onBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            moveToPrevious()
        }
    }

    fun moveToPrevious(){
        findNavController().navigate(R.id.action_fragment_k_y_c_validation_to_fragment_social_links)
    }

}