package com.sanjey.codestride.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sanjey.codestride.data.repository.FirebaseRepository
import com.sanjey.codestride.data.repository.ModuleRepository
import com.sanjey.codestride.data.repository.RoadmapRepository
import com.sanjey.codestride.data.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): FirebaseRepository {
        return FirebaseRepository(firestore, auth)
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        firestore: FirebaseFirestore
    ): UserRepository {
        return UserRepository(firestore)
    }

    @Provides
    @Singleton
    fun provideRoadmapRepository(
        firestore: FirebaseFirestore
    ): RoadmapRepository {
        return RoadmapRepository(firestore)
    }

    @Provides
    @Singleton
    fun provideModuleRepository(
        firestore: FirebaseFirestore
    ): ModuleRepository {
        return ModuleRepository(firestore)
    }
}
