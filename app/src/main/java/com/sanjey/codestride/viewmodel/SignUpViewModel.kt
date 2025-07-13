package com.sanjey.codestride.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {




    private val _signupResult = MutableLiveData<Result<Boolean>?>()
    val signupResult: LiveData<Result<Boolean>?> = _signupResult
    fun clearResult() {
        _signupResult.value = null
    }

    fun signupUser(email: String, password: String, firstName: String, lastName: String, mobile: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        val userMap = hashMapOf(
                            "firstName" to firstName,
                            "lastName" to lastName,
                            "mobile" to mobile,
                            "email" to email
                        )
                        firestore.collection("users").document(uid)
                            .set(userMap)
                            .addOnSuccessListener {
                                _signupResult.value = Result.success(true)
                            }
                            .addOnFailureListener { e ->
                                _signupResult.value = Result.failure(e)
                            }
                    } else {
                        _signupResult.value = Result.failure(Exception("User ID is null"))
                    }
                } else {
                    _signupResult.value = Result.failure(task.exception ?: Exception("Signup failed"))
                }
            }
    }
}
