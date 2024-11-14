package com.ncs.marioapp.DI

import com.google.firebase.firestore.FirebaseFirestore
import com.ncs.marioapp.Data.RepositoryImpl.FirestoreRepositoryImpl
import com.ncs.marioapp.Domain.Repository.FirestoreRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Singleton
    @Provides
    fun provideFirestoreInstance(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Singleton
    @Provides
    fun provideFirestoreRepository(firestore: FirebaseFirestore): FirestoreRepository {
        return FirestoreRepositoryImpl(firestore)
    }


}