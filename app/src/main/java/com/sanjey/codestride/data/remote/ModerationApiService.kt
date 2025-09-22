package com.sanjey.codestride.data.remote

import com.sanjey.codestride.data.model.ai.ModerationRequest
import com.sanjey.codestride.data.model.ai.ModerationResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ModerationApiService {
    @Headers("Content-Type: application/json")
    @POST("v1/moderations")
    suspend fun checkContent(
        @Body request: ModerationRequest
    ): ModerationResponse
}
