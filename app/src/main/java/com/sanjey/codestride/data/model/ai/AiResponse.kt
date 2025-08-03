package com.sanjey.codestride.data.model.ai

data class AiResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: MessageContent
)

data class MessageContent(
    val content: String
)
