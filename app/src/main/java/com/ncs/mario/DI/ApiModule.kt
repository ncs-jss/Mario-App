package com.ncs.mario.DI


import com.ncs.mario.BuildConfig
import com.ncs.mario.Domain.Api.AuthApiService
import com.ncs.mario.Domain.Api.EventsApi
import com.ncs.mario.Domain.Api.MerchApi
import com.ncs.mario.Domain.Api.ProfileApiService
import com.ncs.mario.Domain.Api.QRAPI
import com.ncs.mario.Domain.Models.Merch
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
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder().addInterceptor(interceptor)
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

}