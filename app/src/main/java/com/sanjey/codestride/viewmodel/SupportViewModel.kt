package com.sanjey.codestride.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class SupportViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun sendIssueReport(message: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val userEmail = auth.currentUser?.email ?: "anonymous@codestride.app"
                val issueData = hashMapOf(
                    "message" to message,
                    "userEmail" to userEmail,
                    "timestamp" to System.currentTimeMillis()
                )

                db.collection("support_issues")
                    .add(issueData)
                    .addOnSuccessListener {
                        Log.d("SUPPORT_VM", "Issue saved: ${it.id}")
                        onResult(true)
                    }
                    .addOnFailureListener { e ->
                        Log.e("SUPPORT_VM", "Failed to save issue", e)
                        onResult(false)
                    }

            } catch (e: Exception) {
                Log.e("SUPPORT_VM", "Exception: ", e)
                onResult(false)
            }
        }
    }
}
