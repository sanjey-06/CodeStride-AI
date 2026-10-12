package com.sanjey.codestride.data.repository

import com.sanjey.codestride.data.model.backend.UsageResponse
import com.sanjey.codestride.data.remote.backend.CodeStrideApi
import com.sanjey.codestride.data.remote.backend.backendCall
import javax.inject.Inject

/** Today's AI usage per feature (GET /usage). Reading it never counts towards a limit. */
class UsageRepository @Inject constructor(
    private val api: CodeStrideApi
) {
    suspend fun getUsage(): Result<UsageResponse> = backendCall { api.usage() }
}