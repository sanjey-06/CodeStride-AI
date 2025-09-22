package com.sanjey.codestride.data.repository

import com.sanjey.codestride.data.model.ai.AiRequest
import com.sanjey.codestride.data.model.ai.Message
import com.sanjey.codestride.data.remote.AiApiService
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val api: AiApiService
) {
    suspend fun sendMessage(messages: List<Message>): Message? {
        return try {
            val response = api.getAiRoadmap(
                AiRequest(
                    model = "gpt-3.5-turbo",
                    messages = messages,
                    max_tokens = 500,
                    temperature = 0.7
                )
            )

            // 🔹 Get assistant reply content from MessageContent
            val replyContent = response.choices.firstOrNull()?.message?.content

            if (replyContent != null) {
                Message(role = "assistant", content = replyContent)
            } else {
                null
            }

        } catch (e: Exception) {
            Message(role = "assistant", content = "⚠️ Error: ${e.message}")
        }
    }
}
