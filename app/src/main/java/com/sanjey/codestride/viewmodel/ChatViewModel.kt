package com.sanjey.codestride.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanjey.codestride.data.model.ai.Message
import com.sanjey.codestride.data.remote.backend.BackendError
import com.sanjey.codestride.data.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repo: ChatRepository
) : ViewModel() {

    /** Everything shown on screen: user messages, AI replies, warnings and errors. */
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    /**
     * What the AI actually sees: only exchanges that SUCCEEDED (user message + its reply).
     * Blocked messages and error notices are shown on screen but never sent again.
     */
    private val history = mutableListOf<Message>()

    fun sendMessage(userInput: String) {
        val userMessage = Message(role = "user", content = userInput)

        // 1️⃣ Show the user's message immediately
        _messages.value += userMessage
        _isLoading.value = true

        // 2️⃣ Ask the Worker (moderation + daily limit + AI all happen there)
        viewModelScope.launch {
            try {
                repo.sendMessage(history + userMessage)
                    .onSuccess { reply ->
                        val aiMessage = Message(role = "assistant", content = reply)
                        _messages.value += aiMessage
                        history += userMessage
                        history += aiMessage
                    }
                    .onFailure { e ->
                        // Blocked / limit reached / AI busy / no internet -> show a friendly notice.
                        // Not added to history, so it's never sent to the AI again.
                        val text = (e as? BackendError)?.userMessage ?: "Something went wrong. Please try again."
                        _messages.value += Message(role = "assistant", content = text)
                    }
            } finally {
                // 3️⃣ Stop loading (always)
                _isLoading.value = false
            }
        }
    }
}