package com.sanjey.codestride.data.remote

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Call

interface YouTubeApiService {

    @GET("search")
    fun searchVideos(
        @Query("part") part: String = "snippet",
        @Query("q") query: String,
        @Query("key") apiKey: String,
        @Query("type") type: String = "video",
        @Query("maxResults") maxResults: Int = 1
    ): Call<YouTubeResponse>
}
