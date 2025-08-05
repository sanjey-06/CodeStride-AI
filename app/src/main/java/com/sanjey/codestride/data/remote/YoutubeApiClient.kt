package com.sanjey.codestride.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object YouTubeApiClient {
    val retrofit: YouTubeApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/youtube/v3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(YouTubeApiService::class.java)
    }
}
