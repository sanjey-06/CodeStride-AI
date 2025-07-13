package com.sanjey.codestride.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _loginResult = MutableLiveData<Result<Boolean>?>()
    val loginResult: LiveData<Result<Boolean>?> = _loginResult

    fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _loginResult.value = Result.success(true)
                } else {
                    _loginResult.value = Result.failure(task.exception ?: Exception("Login failed"))
                }
            }
    }

    fun clearResult() {
        _loginResult.value = null
    }
}
