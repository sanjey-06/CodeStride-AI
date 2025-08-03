package com.sanjey.codestride.data.model.ai

data class AiRequest(
    val model: String = "gpt-3.5-turbo",
    val messages: List<Message>
)
