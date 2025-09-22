package com.sanjey.codestride.data.model.ai

data class ModerationRequest(
    val model: String = "omni-moderation-latest", // OpenAI's moderation model
    val input: String
)

data class ModerationResponse(
    val results: List<ModerationResult>
)

data class ModerationResult(
    val flagged: Boolean,
    val categories: Map<String, Boolean>
)
