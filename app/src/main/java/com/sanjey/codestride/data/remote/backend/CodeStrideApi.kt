package com.sanjey.codestride.data.remote.backend

import com.sanjey.codestride.data.model.backend.ChatRequest
import com.sanjey.codestride.data.model.backend.ChatResponse
import com.sanjey.codestride.data.model.backend.MeResponse
import com.sanjey.codestride.data.model.backend.ModuleContentResponse
import com.sanjey.codestride.data.model.backend.ModuleRequest
import com.sanjey.codestride.data.model.backend.QuizResponse
import com.sanjey.codestride.data.model.backend.RoadmapRequest
import com.sanjey.codestride.data.model.backend.RoadmapResponse
import com.sanjey.codestride.data.model.backend.UsageResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * The CodeStride Worker (Cloudflare) API.
 * Base URL = BuildConfig.API_BASE_URL. The Firebase token is added by FirebaseAuthInterceptor.
 *
 * Every function throws retrofit2.HttpException for non-2xx responses
 * (401, 400, 429, 502...) - chunk 5.5 turns those into friendly app errors.
 */
interface CodeStrideApi {

    /** Debug: who the Worker thinks you are. */
    @GET("me")
    suspend fun me(): MeResponse

    /** Today's usage per feature (doesn't count towards limits). */
    @GET("usage")
    suspend fun usage(): UsageResponse

    @POST("roadmap")
    suspend fun roadmap(@Body body: RoadmapRequest): RoadmapResponse

    @POST("module-content")
    suspend fun moduleContent(@Body body: ModuleRequest): ModuleContentResponse

    @POST("quiz")
    suspend fun quiz(@Body body: ModuleRequest): QuizResponse

    @POST("chat")
    suspend fun chat(@Body body: ChatRequest): ChatResponse
}