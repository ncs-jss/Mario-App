package com.ncs.marioapp

import android.app.Application
import android.content.Context
import android.os.StrictMode
import android.util.Log
 import com.cloudinary.android.MediaManager
import com.google.firebase.FirebaseApp
import com.instacart.library.truetime.TrueTime
import com.ncs.marioapp.Domain.HelperClasses.PrefManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.ncs.marioapp.Domain.Api.ProfileApiService
import com.ncs.marioapp.Domain.HelperClasses.ProfileWorker
import com.ncs.marioapp.Domain.HelperClasses.UploadCollegeIDWorker
import com.ncs.marioapp.Domain.HelperClasses.UploadUserImageWorker
import javax.inject.Inject

@HiltAndroidApp
class MarioApp() : Application(), Configuration.Provider {

    @Inject
    lateinit var customWorkerFactory: CustomWorkerFactory



    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setWorkerFactory(customWorkerFactory)
            .build()



    class CustomWorkerFactory @Inject constructor(
        private val profileApiService: ProfileApiService
    ) : WorkerFactory() {

        override fun createWorker(
            appContext: Context,
            workerClassName: String,
            workerParameters: WorkerParameters
        ): ListenableWorker?= when (workerClassName) {
                UploadUserImageWorker::class.java.name -> UploadUserImageWorker(appContext, workerParameters, profileApiService)
                UploadCollegeIDWorker::class.java.name -> UploadCollegeIDWorker(appContext, workerParameters, profileApiService)
                ProfileWorker::class.java.name -> ProfileWorker(appContext, workerParameters, profileApiService)
            else -> {null}
        }
    }

    override fun onCreate() {
        super.onCreate()
        PrefManager.initialize(this@MarioApp)

        CoroutineScope(Dispatchers.IO).launch {
            TrueTime.build()
                .withNtpHost("time.google.com")
                .initialize()

            FirebaseApp.initializeApp(this@MarioApp)


            val config = HashMap<String, Any>()
            config["cloud_name"] = BuildConfig.CLOUDINARY_CLOUD_NAME
            config["api_key"] = BuildConfig.CLOUDINARY_API_KEY
            config["api_secret"] = BuildConfig.CLOUDINARY_API_SECRET
            config["secure"] = true

            MediaManager.init(this@MarioApp, config)
            if (BuildConfig.DEBUG) {
                Timber.plant(Timber.DebugTree())
            }

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