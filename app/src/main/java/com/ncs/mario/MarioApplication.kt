package com.ncs.mario

import android.app.Application
import com.ncs.mario.Domain.HelperClasses.PrefManager
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class MarioApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        PrefManager.initialize(this@MarioApplication)
    }
}