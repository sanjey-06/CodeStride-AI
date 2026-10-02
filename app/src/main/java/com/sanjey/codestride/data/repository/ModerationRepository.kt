package com.sanjey.codestride.data.repository

import com.sanjey.codestride.data.model.ai.ModerationRequest
import com.sanjey.codestride.data.remote.ModerationApiService
import javax.inject.Inject

class ModerationRepository @Inject constructor(
    private val moderationApi: ModerationApiService
) {
    suspend fun isContentSafe(text: String): Boolean {
        return try {
            val response = moderationApi.checkContent(
                ModerationRequest(input = text)
            )
            // Only block if flagged = true
            !(response.results.firstOrNull()?.flagged.orFalse())
        } catch (e: Exception) {
            // If moderation API fails, allow instead of blocking
            true
        }
    }
}

fun Boolean?.orFalse(): Boolean = this ?: false

