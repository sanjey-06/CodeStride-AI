package com.sanjey.codestride.data.remote

import com.sanjey.codestride.data.model.ai.AiRequest
import com.sanjey.codestride.data.model.ai.AiResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AiApiService {
    @Headers(
        "Content-Type: application/json"
    )
    @POST("chat/completions")
    suspend fun getAiRoadmap(@Body request: AiRequest): AiResponse
}
