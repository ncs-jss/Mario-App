package com.ncs.marioapp.Domain.HelperClasses

import android.content.Context
import android.util.Log
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.ncs.marioapp.R


class RemoteConfigHelper(context: Context) {
    private val mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()


    init {
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remot_config_defaults)

        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(1800)
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

    fun fetchBaseURLFromRemoteConfig(buildType:String,callback: (Boolean) -> Unit) {
        mFirebaseRemoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val updated = task.result
                    if (buildType=="debug"){
                        val baseUrl=mFirebaseRemoteConfig.getString("DEBUG_BASE_URL")
                        PrefManager.setBaseUrl(baseUrl)
                        Log.d(TAG, "Fetch Succeeded Debug, $baseUrl")
                        callback(true)
                    }
                    else{
                        val baseUrl=mFirebaseRemoteConfig.getString("RELEASE_BASE_URL")
                        PrefManager.setBaseUrl(baseUrl)
                        Log.d(TAG, "Fetch Succeeded release, $baseUrl")
                        callback(true)
                    }
                } else {
                    callback(false)
                    Log.d(TAG, "Fetch failed")
                }
            }
            .addOnFailureListener{
                callback(false)
            }
    }


    companion object {
        private const val TAG = "RemoteConfigHelper"
    }

}