package com.ncs.mario.Domain.HelperClasses

import android.content.Context
import android.util.Log
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.ncs.mario.R


class RemoteConfigHelper(context: Context) {
    private val mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()


    init {
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remot_config_defaults)

        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(900)
            .build()
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings)

    }

    fun fetchRemoteConfig(callback: (String) -> Unit) {
        mFirebaseRemoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val updated = task.result

                    val latestVersion = mFirebaseRemoteConfig.getString("LATEST_VERSION_CODE")
                    callback(latestVersion)
                } else {
                    Log.d(TAG, "Fetch failed")
                }
            }
    }

    companion object {
        private const val TAG = "RemoteConfigHelper"
    }
}