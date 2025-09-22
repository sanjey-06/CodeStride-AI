package com.sanjey.codestride.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sanjey.codestride.BuildConfig
import com.sanjey.codestride.data.remote.ModerationApiService
import com.sanjey.codestride.data.repository.FirebaseRepository
import com.sanjey.codestride.data.repository.ModuleRepository
import com.sanjey.codestride.data.repository.RoadmapRepository
import com.sanjey.codestride.data.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
        firestore: FirebaseFirestore,
        firebaseRepository: FirebaseRepository
    ): UserRepository {
        return UserRepository(firestore, firebaseRepository)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer " + BuildConfig.OPENAI_API_KEY)
                    .build()
                chain.proceed(request)
            }
            .build()
    }


    @Provides
    @Singleton
    fun provideModerationApiService(okHttpClient: OkHttpClient): ModerationApiService {
        return Retrofit.Builder()
            .baseUrl("https://api.openai.com/") // base URL for moderation endpoint
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ModerationApiService::class.java)
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
