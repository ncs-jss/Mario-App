package com.ncs.marioapp

import android.app.Application
import android.os.StrictMode
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


        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            enableStrictMode()
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