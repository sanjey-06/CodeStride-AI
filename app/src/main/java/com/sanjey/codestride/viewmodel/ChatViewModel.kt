package com.sanjey.codestride.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanjey.codestride.data.model.ai.Message
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

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading


    fun sendMessage(userInput: String) {
        // 1️⃣ Add user’s message immediately
        val updated = _messages.value + Message(role = "user", content = userInput)
        _messages.value = updated

        // 2️⃣ Call OpenAI API in background
        viewModelScope.launch {
            val reply = repo.sendMessage(updated)
            if (reply != null) {
                _messages.value += reply
            }
        }
    }
}
