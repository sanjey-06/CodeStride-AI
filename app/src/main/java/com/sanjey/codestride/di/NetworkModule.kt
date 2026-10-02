package com.sanjey.codestride.di

import com.sanjey.codestride.BuildConfig
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
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)   // ⏱️ Connection timeout
            .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)      // ⏱️ Response read timeout
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader(
                        "Authorization",
                        "Bearer ${BuildConfig.OPENAI_API_KEY}"
                    )
                    .build()
                chain.proceed(request)
            }
            .build()

        return Retrofit.Builder()
            .baseUrl("https://api.openai.com/v1/")
            .client(okHttpClient) // 🔧 apply the custom client with timeouts
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun provideAiApiService(retrofit: Retrofit): AiApiService {
        // 🔎 Log before creating service
        if (BuildConfig.DEBUG) {
            android.util.Log.d("AI_TRACE", "🛠 Creating AiApiService from Retrofit")
        }

        // 🔎 Dump method signatures & annotations (so we see if R8 stripped them)
        AiApiService::class.java.methods.forEach { m ->
            if (BuildConfig.DEBUG) {
                android.util.Log.d(
                    "AI_TRACE",
                    "📜 Method: ${m.name}, return=${m.genericReturnType}, " +
                            "params=${m.parameterTypes.joinToString()}, " +
"annotations=${m.annotations.joinToString()}"
)
}
}

val service = retrofit.create(AiApiService::class.java)

// 🔎 Log after creation
if (BuildConfig.DEBUG) {
    android.util.Log.d("AI_TRACE", "✅ AiApiService created: $service")
}
return service
}
}
