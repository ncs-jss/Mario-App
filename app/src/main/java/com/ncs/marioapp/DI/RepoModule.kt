package com.ncs.marioapp.DI

import com.ncs.marioapp.Data.RepositoryImpl.RetrofitEventRepository
import com.ncs.marioapp.Data.RepositoryImpl.RetrofitProfileRepository
import com.ncs.marioapp.Data.RepositoryImpl.RetrofitQrRepository
import com.ncs.marioapp.Domain.Repository.EventRepository
import com.ncs.marioapp.Domain.Repository.ProfileRepository
import com.ncs.marioapp.Domain.Repository.QrRepository
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
    abstract fun bindsEventRepo(retrofitEventRepo: RetrofitEventRepository): EventRepository

    @Singleton
    @Binds
    abstract fun bindsProfileRepo(retrofitProfileRepo: RetrofitProfileRepository): ProfileRepository

    @Singleton
    @Binds
    abstract fun bindsQrRepo(retrofitQrRepo: RetrofitQrRepository): QrRepository


}