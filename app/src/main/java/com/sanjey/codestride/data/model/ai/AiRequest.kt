package com.sanjey.codestride.data.model.ai

data class AiRequest(
    val model: String = "gpt-3.5-turbo",
    val messages: List<Message>,
    val max_tokens: Int = 2000,
    val temperature: Double = 0.7
)
