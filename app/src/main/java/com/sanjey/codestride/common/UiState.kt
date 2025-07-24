package com.sanjey.codestride.common

sealed class UiState<out T> {
    data object Idle : UiState<Nothing>() // ✅ Initial state
    data object Loading : UiState<Nothing>() // ✅ While fetching
    data class Success<T>(val data: T) : UiState<T>() // ✅ On success
    data object Empty : UiState<Nothing>() // ✅ When no data found
    data class Error(val message: String, val throwable: Throwable? = null) : UiState<Nothing>() // ✅ On failure
}
