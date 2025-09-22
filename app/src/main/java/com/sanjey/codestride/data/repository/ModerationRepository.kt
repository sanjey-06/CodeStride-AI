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
            // If flagged = true → content is unsafe
            !response.results.firstOrNull()?.flagged.orFalse()
        } catch (e: Exception) {
            false // if API fails, be cautious and block
        }
    }
}

fun Boolean?.orFalse(): Boolean = this ?: false
