package com.ncs.marioapp.UI.SettingsScreen.ImageView


import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.igreenwood.loupe.Loupe
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.marioapp.databinding.ActivityImageViewBinding


class ImageViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImageViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityImageViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        WindowCompat.getInsetsController(window, window.decorView)?.isAppearanceLightStatusBars = false
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
        val byteArray = intent.getByteArrayExtra("bitmap")
        if (byteArray != null) {
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            binding.image.setImageBitmap(bitmap)
        }


        val receivedUriString = intent.getStringExtra("uri")

        if (!receivedUriString.isNullOrBlank()){
            val imageUri = Uri.parse(receivedUriString)
            Glide.with(this).load(imageUri).into(binding.image)
        }

        val loupe= Loupe.create(binding.image, binding.container) {
            onViewTranslateListener = object : Loupe.OnViewTranslateListener {

                override fun onStart(view: ImageView) {
                }

                override fun onViewTranslate(view: ImageView, amount: Float) {
                }

                override fun onRestore(view: ImageView) {
                }

                override fun onDismiss(view: ImageView) {
                    finish()
                }
            }
        }

        binding.btnBack.setOnClickThrottleBounceListener{
            finish()
        }

    }

}