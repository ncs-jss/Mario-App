package com.ncs.mario.DI


import com.ncs.mario.BuildConfig
import com.ncs.mario.Domain.Api.AuthApiService
import com.ncs.mario.Domain.Api.BannerApiService
import com.ncs.mario.Domain.Api.EventsApi
import com.ncs.mario.Domain.Api.MerchApi
import com.ncs.mario.Domain.Api.PostApiService
import com.ncs.mario.Domain.Api.ProfileApiService
import com.ncs.mario.Domain.Api.QRAPI
import com.ncs.mario.Domain.Api.ReportApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
            }).build()
    }


    @Provides
    @Singleton
    fun getAuthApiService(okkHttpClient: OkHttpClient): AuthApiService {
        return Retrofit.Builder()
            .baseUrl("${BuildConfig.API_BASE_URL}${BuildConfig.AUTH_ENDPOINT}")
            .client(okkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun getProfileApiService(okkHttpClient: OkHttpClient): ProfileApiService {
        return Retrofit.Builder()
            .baseUrl("${BuildConfig.API_BASE_URL}${BuildConfig.PROFILE_ENDPOINT}")
            .client(okkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ProfileApiService::class.java)
    }

    @Provides
    @Singleton
    fun getEventsApiService(okkHttpClient: OkHttpClient): EventsApi {
        return Retrofit.Builder()
            .baseUrl("${BuildConfig.API_BASE_URL}${BuildConfig.EVENT_ENDPOINT}")
            .client(okkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EventsApi::class.java)
    }


    @Provides
    @Singleton
    fun getQRApiService(okkHttpClient: OkHttpClient): QRAPI {
        return Retrofit.Builder()
            .baseUrl("${BuildConfig.API_BASE_URL}${BuildConfig.QR_ENDPOINT}")
            .client(okkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(QRAPI::class.java)
    }

    @Provides
    @Singleton
    fun getMerchApiService(okkHttpClient: OkHttpClient): MerchApi {
        return Retrofit.Builder()
            .baseUrl("${BuildConfig.API_BASE_URL}${BuildConfig.MERCH_ENDPOINT}")
            .client(okkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MerchApi::class.java)
    }

    @Provides
    @Singleton
    fun getBannersApiService(okkHttpClient: OkHttpClient): BannerApiService {
        return Retrofit.Builder()
            .baseUrl("${BuildConfig.API_BASE_URL}${BuildConfig.BANNER_ENDPOINT}")
            .client(okkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BannerApiService::class.java)
    }

    @Provides
    @Singleton
    fun getPostsApiService(okkHttpClient: OkHttpClient): PostApiService {
        return Retrofit.Builder()
            .baseUrl("${BuildConfig.API_BASE_URL}${BuildConfig.POST_ENDPOINT}")
            .client(okkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PostApiService::class.java)
    }

    @Provides
    @Singleton
    fun getReportsApiService(okkHttpClient: OkHttpClient): ReportApiService {
        return Retrofit.Builder()
            .baseUrl("${BuildConfig.API_BASE_URL}${BuildConfig.REPORTS_ENDPOINT}")
            .client(okkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ReportApiService::class.java)
    }


}