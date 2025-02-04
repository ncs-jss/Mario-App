package com.ncs.marioapp.DI


import android.content.Context
import android.content.SharedPreferences
import com.ncs.marioapp.BuildConfig
import com.ncs.marioapp.Domain.Api.AuthApiService
import com.ncs.marioapp.Domain.Api.BannerApiService
import com.ncs.marioapp.Domain.Api.EventsApi
import com.ncs.marioapp.Domain.Api.MailApiService
import com.ncs.marioapp.Domain.Api.MerchApi
import com.ncs.marioapp.Domain.Api.PostApiService
import com.ncs.marioapp.Domain.Api.ProfileApiService
import com.ncs.marioapp.Domain.Api.QRAPI
import com.ncs.marioapp.Domain.Api.ReportApiService
import com.ncs.marioapp.Domain.Api.TimeApiService
import com.ncs.marioapp.Domain.HelperClasses.PrefManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun provideOkHTTPClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        return OkHttpClient.Builder().addInterceptor(loggingInterceptor)
            .addInterceptor(Interceptor { chain: Interceptor.Chain ->
                val request: Request = chain.request()
                chain.proceed(request)
            })
            .connectTimeout(45, TimeUnit.SECONDS)
            .writeTimeout(45, TimeUnit.SECONDS)
            .readTimeout(45, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("NCS_MARIO_PREFS", Context.MODE_PRIVATE)
    }

    fun getBaseUrlFromSharedPrefs(sharedPreferences: SharedPreferences) : String?{
        return sharedPreferences.getString("baseurl", "https://mario-backend.onrender.com")
    }

    @Provides
    @Singleton
    fun getAuthApiService(okkHttpClient: OkHttpClient, sharedPreferences: SharedPreferences): AuthApiService {
        return Retrofit.Builder()
            .baseUrl("${getBaseUrlFromSharedPrefs(sharedPreferences)}${BuildConfig.AUTH_ENDPOINT}")
            .client(okkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun getProfileApiService(okkHttpClient: OkHttpClient, sharedPreferences: SharedPreferences): ProfileApiService {
        return Retrofit.Builder()
            .baseUrl("${getBaseUrlFromSharedPrefs(sharedPreferences)}${BuildConfig.PROFILE_ENDPOINT}")
            .client(okkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ProfileApiService::class.java)
    }

    @Provides
    @Singleton
    fun getEventsApiService(okkHttpClient: OkHttpClient, sharedPreferences: SharedPreferences): EventsApi {
        return Retrofit.Builder()
            .baseUrl("${getBaseUrlFromSharedPrefs(sharedPreferences)}${BuildConfig.EVENT_ENDPOINT}")
            .client(okkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EventsApi::class.java)
    }


    @Provides
    @Singleton
    fun getQRApiService(okkHttpClient: OkHttpClient, sharedPreferences: SharedPreferences): QRAPI {
        return Retrofit.Builder()
            .baseUrl("${getBaseUrlFromSharedPrefs(sharedPreferences)}${BuildConfig.QR_ENDPOINT}")
            .client(okkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(QRAPI::class.java)
    }

    @Provides
    @Singleton
    fun getMerchApiService(okkHttpClient: OkHttpClient, sharedPreferences: SharedPreferences): MerchApi {
        return Retrofit.Builder()
            .baseUrl("${getBaseUrlFromSharedPrefs(sharedPreferences)}${BuildConfig.MERCH_ENDPOINT}")
            .client(okkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MerchApi::class.java)
    }

    @Provides
    @Singleton
    fun getBannersApiService(okkHttpClient: OkHttpClient, sharedPreferences: SharedPreferences): BannerApiService {
        return Retrofit.Builder()
            .baseUrl("${getBaseUrlFromSharedPrefs(sharedPreferences)}${BuildConfig.BANNER_ENDPOINT}")
            .client(okkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BannerApiService::class.java)
    }

    @Provides
    @Singleton
    fun getPostsApiService(okkHttpClient: OkHttpClient, sharedPreferences: SharedPreferences): PostApiService {
        return Retrofit.Builder()
            .baseUrl("${getBaseUrlFromSharedPrefs(sharedPreferences)}${BuildConfig.POST_ENDPOINT}")
            .client(okkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PostApiService::class.java)
    }

    @Provides
    @Singleton
    fun getReportsApiService(okkHttpClient: OkHttpClient, sharedPreferences: SharedPreferences): ReportApiService {
        return Retrofit.Builder()
            .baseUrl("${getBaseUrlFromSharedPrefs(sharedPreferences)}${BuildConfig.REPORTS_ENDPOINT}")
            .client(okkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ReportApiService::class.java)
    }

    @Provides
    @Singleton
    fun getTimeApiService(okkHttpClient: OkHttpClient): TimeApiService {
        return Retrofit.Builder()
            .baseUrl("https://worldtimeapi.org/api/")
            .client(okkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TimeApiService::class.java)

    }



}