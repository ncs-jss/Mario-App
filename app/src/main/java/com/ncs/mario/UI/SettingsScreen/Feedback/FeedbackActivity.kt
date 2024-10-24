package com.ncs.mario.UI.SettingsScreen.Feedback

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import com.google.android.material.chip.Chip
import com.ncs.mario.Domain.Models.Report.ReportBody
import com.ncs.mario.Domain.Utility.ExtensionsUtil.gone
import com.ncs.mario.Domain.Utility.ExtensionsUtil.isNull
import com.ncs.mario.Domain.Utility.ExtensionsUtil.runDelayed
import com.ncs.mario.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.mario.Domain.Utility.ExtensionsUtil.toast
import com.ncs.mario.Domain.Utility.ExtensionsUtil.visible
import com.ncs.mario.Domain.Utility.GlobalUtils
import com.ncs.mario.R
import com.ncs.mario.UI.MainScreen.Home.HomeViewModel
import com.ncs.mario.UI.SettingsScreen.ImageView.ImageViewActivity
import com.ncs.mario.databinding.ActivityFeedbackBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream


@AndroidEntryPoint
class FeedbackActivity : AppCompatActivity() {


    val binding: ActivityFeedbackBinding by lazy {
        ActivityFeedbackBinding.inflate(layoutInflater)
    }

    lateinit var bitmap: Bitmap
    private val util: GlobalUtils.EasyElements by lazy {
        GlobalUtils.EasyElements(this)
    }
    var imageCount=0
    var imagesPosted=0
    val urls:MutableList<String> = mutableListOf()
    val bitmaps:MutableList<Bitmap> = mutableListOf()
    var defaultBitmap:Bitmap?=null
    val uris:MutableList<Uri> = mutableListOf()

    var type:MutableList<String> = mutableListOf()

    private val REQUEST_IMAGE_PICK = 2

    var fileName:String?=null

    private val viewModel: FeedbackViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (fileName != null) {
            val internalStorageDir = this.filesDir
            val file = File(internalStorageDir, fileName)
            if (file.exists()) {
                bitmap = BitmapFactory.decodeFile(file.absolutePath)
                defaultBitmap=bitmap
                imageCount++
                bitmaps.add(bitmap)
            }
        }
        binding.desc.requestFocus()
        observeViewModel()
        setUpViews()
        setImages()
    }

    private fun observeViewModel(){
        viewModel.progressState.observe(this) {
            if (it) {
                binding.linearProgressIndicator.visible()
            } else {
                binding.linearProgressIndicator.gone()
            }
        }
        viewModel.errorMessage.observe(this) {
            showError(it)
        }
        viewModel.feedbackResult.observe(this){
            if (it){
                runDelayed(1500) {
                    onBackPressed()
                }
            }
        }
    }

    private fun showError(message: String?) {
        if (!message.isNull) {
            util.showSnackbar(binding.root, message!!, 2000)
        }
    }


    private fun setImages(){

        Log.d("bitmaps",bitmaps.toString())

        if (bitmaps.size==0){
            binding.imagesCont.gone()
            binding.placeholder.visible()
        }
        else{
            binding.imagesCont.visible()
            binding.placeholder.gone()
            val size=bitmaps.size
            when(size){
                1-> {
                    binding.image1.visible()
                    binding.image1.setImageBitmap(bitmaps[0])

                    binding.image3.gone()

                    binding.image2.gone()

                }
                2->{
                    binding.image1.visible()
                    binding.image1.setImageBitmap(bitmaps[0])

                    binding.image2.visible()
                    binding.image2.setImageBitmap(bitmaps[1])

                    binding.image3.gone()
                }

                3->{
                    binding.image1.visible()
                    binding.image1.setImageBitmap(bitmaps[0])

                    binding.image2.visible()
                    binding.image2.setImageBitmap(bitmaps[1])

                    binding.image3.visible()
                    binding.image3.setImageBitmap(bitmaps[2])
                }
            }

        }

    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {

                REQUEST_IMAGE_PICK -> {
                    val selectedImage = data?.data
                    bitmap = uriToBitmap(this.contentResolver, selectedImage!!)!!
                    bitmaps.add(bitmap)
                    uris.add(selectedImage)
                    imageCount++
                    setImages()
                }
            }
        }
    }

    fun uriToBitmap(contentResolver: ContentResolver, uri: Uri): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            if (inputStream != null) {
                bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bitmap
    }

    private fun setUpViews(){

        binding.actionbar.btnHam.setImageResource(R.drawable.ic_back_arrow)
        binding.actionbar.btnHam.setOnClickListener {
            onBackPressed()
        }
        binding.actionbar.titleTv.text = "Feedback"
        binding.actionbar.score.gone()

        binding.deleteIc.setOnClickThrottleBounceListener {
            bitmaps.removeLast()
            if(uris.isNotEmpty()){
                uris.removeLast()
            }
            imageCount--
            setImages()
        }

        binding.addImages.setOnClickThrottleBounceListener {
            if (imageCount<3){
                pickImage()
            }
            else{
                toast("At maximum only 3 images can be added")
            }
        }

        binding.image1.setOnClickThrottleBounceListener {
            if (defaultBitmap!=null){
                val _bitmap = bitmaps[0]
                val stream = ByteArrayOutputStream()
                _bitmap.compress(Bitmap.CompressFormat.PNG, 80, stream)
                val byteArray: ByteArray = stream.toByteArray()
                val intent = Intent(this, ImageViewActivity::class.java)
                intent.putExtra("bitmap", byteArray)
                startActivity(intent)
            }
            else{
                val intent = Intent(this, ImageViewActivity::class.java)
                intent.putExtra("uri", uris[0].toString())
                startActivity(intent)
            }
        }

        binding.image2.setOnClickThrottleBounceListener {

            if (defaultBitmap!=null) {
                val intent = Intent(this, ImageViewActivity::class.java)
                intent.putExtra("uri", uris[0].toString())
                startActivity(intent)
            }
            if (defaultBitmap==null) {
                val intent = Intent(this, ImageViewActivity::class.java)
                intent.putExtra("uri", uris[1].toString())
                startActivity(intent)
            }

        }

        binding.image3.setOnClickThrottleBounceListener {
            if (defaultBitmap!=null) {
                val intent = Intent(this, ImageViewActivity::class.java)
                intent.putExtra("uri", uris[1].toString())
                startActivity(intent)
            }
            if (defaultBitmap==null) {
                val intent = Intent(this, ImageViewActivity::class.java)
                intent.putExtra("uri", uris[2].toString())
                startActivity(intent)
            }
        }

        binding.chipGroup.setOnCheckedChangeListener { group, checkedId ->
            val clickedChip: Chip? = group.findViewById(checkedId)
            clickedChip?.isChecked = true

            if (type.isEmpty()){
                type.add(clickedChip?.text.toString())
            }
            else{
                type.clear()
                type.add(clickedChip?.text.toString())
            }

        }


        binding.submitIssue.setOnClickThrottleBounceListener{
            if (type.isEmpty()){
                binding.layout.visible()
                toast("Issue Type is required")
            }
            else if (binding.desc.text?.trim().isNullOrBlank()){
                binding.layout.visible()
                toast("Description is required")
            }
            else{
                if (bitmaps.isNotEmpty()){
                    val images= mutableListOf<String>()
                    for (bit in bitmaps){
                        images.add(bitmapToBase64WithMimeType(bit))
                    }
                    val reportBody = ReportBody(
                        type=if (type[0]=="Bug Found 🐞") "BUG" else "FEEDBACK",
                        description = binding.desc.text.toString(),
                        images = images
                    )
                    viewModel.addReport(reportBody)
                }
                else{
                    val images= mutableListOf<String>()
                    val reportBody = ReportBody(
                        type=if (type[0]=="Bug Found 🐞") "BUG" else "FEEDBACK",
                        description = binding.desc.text.toString(),
                        images = images
                    )
                    viewModel.addReport(reportBody)
                }
            }

        }
    }

    private fun bitmapToBase64WithMimeType(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        val base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT)

        return "data:image/jpeg;base64,$base64Image"
    }

}