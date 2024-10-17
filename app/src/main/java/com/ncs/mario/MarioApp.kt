package com.ncs.mario

import android.app.Application
import com.google.firebase.FirebaseApp
import com.ncs.mario.Domain.HelperClasses.PrefManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MarioApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        PrefManager.initialize(this@MarioApp)
    }
}