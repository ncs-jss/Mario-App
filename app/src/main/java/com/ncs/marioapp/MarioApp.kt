package com.ncs.marioapp

import android.app.Application
import android.os.StrictMode
import com.cloudinary.android.MediaManager
import com.google.firebase.FirebaseApp
import com.ncs.marioapp.Domain.HelperClasses.PrefManager
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MarioApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        PrefManager.initialize(this@MarioApp)

        val config = HashMap<String, Any>()
        config["cloud_name"] = BuildConfig.CLOUDINARY_CLOUD_NAME
        config["api_key"] = BuildConfig.CLOUDINARY_API_KEY
        config["api_secret"] = BuildConfig.CLOUDINARY_API_SECRET
        config["secure"] = true

        MediaManager.init(this, config)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
//            enableStrictMode()
        }
    }

    private fun enableStrictMode() {

        // Thread policy to catch accidental I/O operations on the main thread
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .permitDiskReads()
                .penaltyFlashScreen()
                .penaltyDialog()
                .build()
        )

        // VM policy to catch memory leaks and improper object handling
        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build()
        )

    }
}