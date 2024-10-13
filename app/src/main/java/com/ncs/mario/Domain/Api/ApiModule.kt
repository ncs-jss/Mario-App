package com.ncs.mario.Domain.Api

import com.ncs.mario.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {

    @Singleton
    @Provides
    fun provideRetrofitBuilder(): Retrofit.Builder {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
    }
    @Singleton
    @Provides
    @Named("defaultClient")
    fun provideDefaultOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }

    @Singleton
    @Provides
    fun provideQrApi(
        retrofitBuilder: Retrofit.Builder,
        @Named("defaultClient") okHttpClient: OkHttpClient
    ): QRAPI {
        return retrofitBuilder
            .client(okHttpClient)
            .build()
            .create(QRAPI::class.java)
    }

    @Singleton
    @Provides
    fun provideProfileApi(
        retrofitBuilder: Retrofit.Builder,
        @Named("defaultClient") okHttpClient: OkHttpClient
    ): ProfileApi {
        return retrofitBuilder
            .client(okHttpClient)
            .build()
            .create(ProfileApi::class.java)
    }
    @Singleton
    @Provides
    fun provideEventApi(
        retrofitBuilder: Retrofit.Builder,
        @Named("defaultClient") okHttpClient: OkHttpClient
    ): EventsApi {
        return retrofitBuilder
            .client(okHttpClient)
            .build()
            .create(EventsApi::class.java)
    }
}