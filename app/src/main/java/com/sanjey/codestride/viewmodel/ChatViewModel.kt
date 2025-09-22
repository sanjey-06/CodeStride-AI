package com.sanjey.codestride.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanjey.codestride.data.model.ai.Message
import com.sanjey.codestride.data.repository.ChatRepository
import com.sanjey.codestride.data.repository.ModerationRepository // ✅ NEW import
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repo: ChatRepository,
    private val moderationRepo: ModerationRepository // ✅ NEW
) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun sendMessage(userInput: String) {
        // 1️⃣ Add user’s message immediately
        val updated = _messages.value + Message(role = "user", content = userInput)
        _messages.value = updated

        // 2️⃣ Start loading
        _isLoading.value = true

        // 3️⃣ Call Moderation + API in background
        viewModelScope.launch {
            try {
                val safe = moderationRepo.isContentSafe(userInput) // ✅ moderation check
                if (!safe) {
                    // Show a warning instead of sending to Chat API
                    _messages.value += Message(
                        role = "assistant",
                        content = "⚠️ Your message was blocked because it may contain inappropriate content."
                    )
                } else {
                    // proceed to normal flow
                    val reply = repo.sendMessage(updated)
                    if (reply != null) {
                        _messages.value += reply
                    }
                }
            } finally {
                // 4️⃣ Stop loading (always, even if error)
                _isLoading.value = false
            }
        }
    }
}
