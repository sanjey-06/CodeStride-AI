package com.sanjey.codestride.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanjey.codestride.common.UiState
import com.sanjey.codestride.data.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _loginState = MutableLiveData<UiState<Unit>>()
    val loginState: LiveData<UiState<Unit>> = _loginState

    fun clearLoginState() { _loginState.value = UiState.Idle }


    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = UiState.Loading
            try {
                firebaseRepository.loginUser(email, password)
                _loginState.value = UiState.Success(Unit)
            } catch (e: Exception) {
                _loginState.value = UiState.Error(e.message ?: "Login failed")
            }
        }
    }
}
