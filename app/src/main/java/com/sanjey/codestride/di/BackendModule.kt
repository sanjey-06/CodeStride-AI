package com.sanjey.codestride.di

import com.sanjey.codestride.BuildConfig
import com.sanjey.codestride.data.remote.backend.CodeStrideApi
import com.sanjey.codestride.data.remote.backend.FirebaseAuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Networking for the CodeStride Worker.
 *
 * Builds its OWN OkHttp client + Retrofit (not the existing ones), so:
 *  - the old OpenAI key interceptor in AppModule is never attached to Worker calls
 *  - there's no clash with the existing OkHttpClient / Retrofit bindings
 * Only CodeStrideApi is exposed to the rest of the app.
 */
@Module
@InstallIn(SingletonComponent::class)
object BackendModule {

    @Provides
    @Singleton
    fun provideCodeStrideApi(authInterceptor: FirebaseAuthInterceptor): CodeStrideApi {
        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)                 // adds the Firebase token
            .apply {
                if (BuildConfig.DEBUG) {                     // never log in release builds
                    addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                        redactHeader("Authorization")        // logs "Authorization: ██" instead of the token
                    })
                }
            }
            .connectTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)               // the Worker gives OpenAI up to 45s
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CodeStrideApi::class.java)
    }
}