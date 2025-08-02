package com.sanjey.codestride.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sanjey.codestride.common.UiState
import com.sanjey.codestride.data.model.UserProfileData
import com.sanjey.codestride.data.model.UserSettings
import com.sanjey.codestride.data.prefs.OnboardingPreferences
import com.sanjey.codestride.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val firestore: FirebaseFirestore
) : ViewModel() {
    private val _userSettings = MutableLiveData<UserSettings>()
    val userSettings: LiveData<UserSettings> = _userSettings

    private val _profileState = MutableStateFlow<UiState<UserProfileData>>(UiState.Loading)
    val profileState: StateFlow<UiState<UserProfileData>> = _profileState

    var avatar by mutableStateOf("ic_none")
        private set
    var email by mutableStateOf("")
        private set


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

    fun loadUserSettings() {
        Log.d("SETTINGS_DEBUG", "userId = ${FirebaseAuth.getInstance().currentUser?.uid}")

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                Log.d("SETTINGS_DEBUG", "Fetching user settings for $userId")

                val settings = userRepository.getUserSettings(userId)
                Log.d("SETTINGS_DEBUG", "Fetched settings: $settings")

                _userSettings.value = settings
            } catch (e: Exception) {
                Log.e("SETTINGS_DEBUG", "Error fetching settings: ${e.message}")

            }
        }
    }

    fun saveUserSettings(settings: UserSettings) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                userRepository.updateUserSettings(userId, settings)
                _userSettings.value = settings
            } catch (e: Exception) {
                // Optionally handle error
            }
        }
    }

    private val _accountDeleted = MutableLiveData<Boolean>()
    val accountDeleted: LiveData<Boolean> = _accountDeleted

    fun deleteUserAccount() {
        val user = FirebaseAuth.getInstance().currentUser ?: return

        viewModelScope.launch {
            try {
                val userId = user.uid

                // 🔹 1. Delete settings
                userRepository.deleteUserSettings(userId)

                // 🔹 2. Delete progress subcollection
                val progressCollection = firestore.collection("users")
                    .document(userId)
                    .collection("progress")
                    .get()
                    .await()

                for (doc in progressCollection.documents) {
                    doc.reference.delete().await()
                }

                // 🔹 3. Delete main user document
                firestore.collection("users").document(userId).delete().await()

                // 🔹 4. Delete from FirebaseAuth
                user.delete().addOnCompleteListener { task ->
                    _accountDeleted.postValue(task.isSuccessful)
                }

            } catch (e: Exception) {
                _accountDeleted.postValue(false)
            }
        }
    }

    private val _isLoggedOut = MutableLiveData<Boolean>()
    val isLoggedOut: LiveData<Boolean> = _isLoggedOut

    fun logout() {
        userRepository.logout()
        _isLoggedOut.value = true
    }





    fun changePassword(
        currentPassword: String,
        newPassword: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        val user = FirebaseAuth.getInstance().currentUser
        val email = user?.email

        if (user != null && email != null) {
            val credential = EmailAuthProvider.getCredential(email, currentPassword)
            user.reauthenticate(credential)
                .addOnCompleteListener { reauthTask ->
                    if (reauthTask.isSuccessful) {
                        user.updatePassword(newPassword)
                            .addOnCompleteListener { updateTask ->
                                if (updateTask.isSuccessful) {
                                    onResult(true, null)
                                } else {
                                    onResult(false, updateTask.exception?.message)
                                }
                            }
                    } else {
                        onResult(false, reauthTask.exception?.message)
                    }
                }
        } else {
            onResult(false, "User not logged in")
        }
    }



    fun updateAvatar(newAvatar: String) {
        avatar = newAvatar
    }

    fun saveAvatar() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                userRepository.updateAvatar(userId, avatar)
            } catch (e: Exception) {
                // Optionally: log or expose UI error
            }
        }
    }

    fun loadUserProfile() {
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid ?: return
        val authEmail = user.email ?: ""

        viewModelScope.launch {
            try {
                val data = userRepository.getUserProfileData(userId)

                // ⚠️ use Firestore email only if available, else fallback to FirebaseAuth one
                avatar = data.avatar.ifBlank { "ic_none" }
                email = data.email.ifBlank { authEmail }

                _profileState.value = UiState.Success(data.copy(email = email))
            } catch (e: Exception) {
                _profileState.value = UiState.Error(e.message ?: "Failed to load profile")
            }
        }
    }



}
