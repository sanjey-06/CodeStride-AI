package com.sanjey.codestride.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class FirebaseRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    fun getFirstName(): LiveData<String> {
        val firstNameLiveData = MutableLiveData<String>()
        val uid = auth.currentUser?.uid ?: return firstNameLiveData

        firestore.collection("users").document(uid)
            .get()
            .addOnSuccessListener { document ->
                val name = document.getString("firstName") ?: "Learner"
                firstNameLiveData.value = name
            }

        return firstNameLiveData
    }
}
