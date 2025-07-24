package com.sanjey.codestride.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanjey.codestride.common.UiState
import com.sanjey.codestride.data.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _resetPasswordState = androidx.lifecycle.MutableLiveData<UiState<String>>()
    val resetPasswordState: androidx.lifecycle.LiveData<UiState<String>> = _resetPasswordState

    fun sendPasswordReset(email: String) {
        viewModelScope.launch {
            _resetPasswordState.value = UiState.Loading
            try {
                firebaseRepository.sendPasswordReset(email) // ✅ Move logic to repository
                _resetPasswordState.value = UiState.Success("Password reset email sent!")
            } catch (e: Exception) {
                _resetPasswordState.value = UiState.Error(e.message ?: "Something went wrong")
            }
        }
    }

    fun clearResetState() {
        _resetPasswordState.value = UiState.Idle
    }

}
