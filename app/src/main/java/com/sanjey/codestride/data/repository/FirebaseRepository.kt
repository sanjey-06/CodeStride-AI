package com.sanjey.codestride.data.repository

import android.util.Log
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
    fun getQuestionsByQuiz(
        roadmapId: String,
        moduleId: String,
        quizId: String,
        onSuccess: (List<Question>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        Log.d("QuizDebug", "Fetching questions for $roadmapId -> $moduleId -> $quizId")

        firestore.collection("roadmaps")
            .document(roadmapId)
            .collection("modules")
            .document(moduleId)
            .collection("quizzes")
            .document(quizId)
            .collection("questions")
            .get()
            .addOnSuccessListener { snapshot ->
                Log.d("QuizDebug", "Documents fetched: ${snapshot.size()}")
                snapshot.documents.forEach {
                    Log.d("QuizDebug", "Question Doc: ${it.id}, Data: ${it.data}")
                }

                val questions = snapshot.map { it.toObject(Question::class.java) }
                Log.d("QuizDebug", "Mapped questions: ${questions.size}")
                onSuccess(questions)
            }
            .addOnFailureListener {
                Log.e("QuizDebug", "Failed to fetch: ${it.message}")
                onFailure(it)
            }
    }
}
