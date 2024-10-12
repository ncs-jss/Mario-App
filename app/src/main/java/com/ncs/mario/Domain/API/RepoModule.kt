package com.ncs.mario.Domain.API

import com.ncs.mario.Domain.Interfaces.EventRepository
import com.ncs.mario.Domain.Interfaces.ProfileRepository
import com.ncs.mario.Domain.Interfaces.QrRepository
import com.ncs.mario.Domain.Repository.RetrofitEventRepository
import com.ncs.mario.Domain.Repository.RetrofitProfileRepository
import com.ncs.mario.Domain.Repository.RetrofitQrRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepoModule {

    @Singleton
    @Binds
    abstract fun bindsEventRepo(retrofitEventRepo:RetrofitEventRepository):EventRepository

    @Singleton
    @Binds
    abstract fun bindsProfileRepo(retrofitProfileRepo:RetrofitProfileRepository):ProfileRepository

    @Singleton
    @Binds
    abstract fun bindsQrRepo(retrofitQrRepo:RetrofitQrRepository):QrRepository


}