package com.sanjey.codestride.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sanjey.codestride.data.model.Question
import javax.inject.Inject

class FirebaseRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    // Existing function for user name
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

    // ✅ New function for fetching questions dynamically
    fun getQuestionsByQuizId(
        quizId: String,
        onSuccess: (List<Question>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestore.collection("quizzes").document(quizId)
            .get()
            .addOnSuccessListener { quizDoc ->
                val questionIds = quizDoc.get("question_ids") as? List<*>
                if (!questionIds.isNullOrEmpty()) {
                    firestore.collection("questions")
                        .whereIn("id", questionIds)
                        .get()
                        .addOnSuccessListener { snapshot ->
                            val questions = snapshot.map { it.toObject(Question::class.java) }
                            onSuccess(questions)
                        }
                        .addOnFailureListener { onFailure(it) }
                } else {
                    onSuccess(emptyList())
                }
            }
            .addOnFailureListener { onFailure(it) }
    }
}
