package com.sanjey.codestride.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.sanjey.codestride.common.UiState
import com.sanjey.codestride.data.model.UserProfileData
import com.sanjey.codestride.data.prefs.OnboardingPreferences
import com.sanjey.codestride.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _profileState = MutableStateFlow<UiState<UserProfileData>>(UiState.Loading)
    val profileState: StateFlow<UiState<UserProfileData>> = _profileState


    private val auth = FirebaseAuth.getInstance()

    private val _splashNavigationState = MutableLiveData<String?>()
    val splashNavigationState: LiveData<String?> get() = _splashNavigationState

    fun handleSplashNavigation(context: Context) {
        viewModelScope.launch {
            delay(1500) // Splash delay for logo/text animation

            val isLoggedIn = auth.currentUser != null
            val hasSeenOnboarding = OnboardingPreferences.readOnboardingSeen(context).first()

            val target = when {
                isLoggedIn -> "home"
                hasSeenOnboarding -> "login"
                else -> "onboarding"
            }

            _splashNavigationState.postValue(target)
        }
    }
    fun loadUserProfile() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val data = userRepository.getUserProfileData(userId)
                _profileState.value = UiState.Success(data)
            } catch (e: Exception) {
                _profileState.value = UiState.Error(e.message ?: "Failed to load profile")
            }
        }
    }

}
