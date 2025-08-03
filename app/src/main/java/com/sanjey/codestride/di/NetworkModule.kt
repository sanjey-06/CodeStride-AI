package com.sanjey.codestride.di

import com.sanjey.codestride.data.remote.AiApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.openai.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder().addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer REMOVED_KEY") // Replace securely
                        .build()
                    chain.proceed(request)
                }.build()
            )
            .build()
    }

    @Provides
    fun provideAiApiService(retrofit: Retrofit): AiApiService =
        retrofit.create(AiApiService::class.java)
}
