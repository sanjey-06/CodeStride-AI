package com.sanjey.codestride.data.model.backend

import com.google.gson.annotations.SerializedName
import com.sanjey.codestride.data.model.Question
import com.sanjey.codestride.data.model.RoadmapItem
import com.sanjey.codestride.data.model.ai.Message

/*
 * JSON shapes for the CodeStride Worker (codestride-api).
 * Field names match the Worker exactly - see the Worker's README for each endpoint.
 * This package is covered by the "-keep class ...data.model.**" ProGuard rule.
 */

// ---------------------------------------------------------------- Requests (app -> Worker)

/** POST /roadmap */
data class RoadmapRequest(
    @SerializedName("topic") val topic: String
)

/** POST /module-content and POST /quiz (same body) */
data class ModuleRequest(
    @SerializedName("topic") val topic: String,
    @SerializedName("moduleTitle") val moduleTitle: String
)

/** POST /chat - the conversation so far, ending with the user's new message */
data class ChatRequest(
    @SerializedName("messages") val messages: List<Message>
)

// ---------------------------------------------------------------- Responses (Worker -> app)

/** POST /roadmap -> 10 steps. "link" is always the YouTube placeholder. */
data class RoadmapResponse(
    @SerializedName("items") val items: List<RoadmapItem>
)

/** POST /module-content -> the tutorial HTML */
data class ModuleContentResponse(
    @SerializedName("html") val html: String
)

/** POST /quiz -> up to 5 checked questions */
data class QuizResponse(
    @SerializedName("questions") val questions: List<Question>
)

/** POST /chat -> the assistant's reply text */
data class ChatResponse(
    @SerializedName("reply") val reply: String
)

/** GET /me -> who the Worker thinks you are (debug) */
data class MeResponse(
    @SerializedName("uid") val uid: String,
    @SerializedName("email") val email: String?
)

/** GET /usage -> today's usage per feature ("roadmap", "moduleContent", "quiz", "chat") */
data class UsageResponse(
    @SerializedName("date") val date: String,
    @SerializedName("resetAt") val resetAt: String,
    @SerializedName("features") val features: Map<String, FeatureUsage>
)

data class FeatureUsage(
    @SerializedName("used") val used: Int,
    @SerializedName("limit") val limit: Int,
    @SerializedName("remaining") val remaining: Int
)

// ---------------------------------------------------------------- Errors

/**
 * Every Worker error looks like:
 * {"error": {"code": "rate_limited", "message": "...", "feature": "chat", "limit": 50, "resetAt": "..."}}
 * feature/limit/resetAt are only present for rate_limited, so they're nullable.
 */
data class ErrorResponse(
    @SerializedName("error") val error: ErrorBody?
)

data class ErrorBody(
    @SerializedName("code") val code: String?,
    @SerializedName("message") val message: String?,
    @SerializedName("feature") val feature: String? = null,
    @SerializedName("limit") val limit: Int? = null,
    @SerializedName("resetAt") val resetAt: String? = null
)