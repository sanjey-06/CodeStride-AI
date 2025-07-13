package com.sanjey.codestride.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    val resetSuccess = mutableStateOf<Boolean?>(null)
    val resetError = mutableStateOf<String?>(null)

    fun sendPasswordReset(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    resetSuccess.value = true
                } else {
                    resetSuccess.value = false
                    resetError.value = task.exception?.localizedMessage ?: "Unknown error"
                }
            }
    }
}
