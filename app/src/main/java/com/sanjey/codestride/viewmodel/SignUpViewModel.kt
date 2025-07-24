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
class SignupViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _signupState = MutableLiveData<UiState<Unit>>()
    val signupState: LiveData<UiState<Unit>> = _signupState

    fun signupUser(email: String, password: String, firstName: String, lastName: String, mobile: String) {
        viewModelScope.launch {
            _signupState.value = UiState.Loading
            try {
                firebaseRepository.signupUser(email, password, firstName, lastName, mobile)
                _signupState.value = UiState.Success(Unit)
            } catch (e: Exception) {
                _signupState.value = UiState.Error(e.message ?: "Signup failed")
            }
        }
    }

    fun clearSignupState() { _signupState.value = UiState.Idle }

}
