package com.sanjey.codestride.data.repository

import com.sanjey.codestride.data.model.ai.Message
import com.sanjey.codestride.data.model.backend.ChatRequest
import com.sanjey.codestride.data.remote.backend.CodeStrideApi
import com.sanjey.codestride.data.remote.backend.backendCall
import javax.inject.Inject

/**
 * Chat with CodeStride AI through the Worker (POST /chat).
 * The Worker adds the system prompt, runs moderation and enforces the daily limit.
 */
class ChatRepository @Inject constructor(
    private val api: CodeStrideApi
) {
    /**
     * Sends the conversation (ending with the user's new message) and returns the AI's reply.
     * On failure the Result holds a BackendError with a ready-to-show message.
     */
    suspend fun sendMessage(conversation: List<Message>): Result<String> = backendCall {
        api.chat(ChatRequest(conversation.takeLast(MAX_HISTORY))).reply
    }

    private companion object {
        /** Same limit the Worker uses - no point sending more. */
        const val MAX_HISTORY = 10
    }
}