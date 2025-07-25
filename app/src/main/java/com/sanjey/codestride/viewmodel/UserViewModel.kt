package com.sanjey.codestride.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.sanjey.codestride.data.prefs.OnboardingPreferences
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

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
}
