package com.sanjey.codestride.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.sanjey.codestride.data.model.Question
import com.sanjey.codestride.data.model.Quiz
import com.sanjey.codestride.data.model.Quote
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {


    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }


    // ✅ Fetch user first name
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

    // ✅ Fetch questions for the quiz
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

    // Quotes
    suspend fun getQuotes(): List<Quote> {
        return try {
            firestore.collection("quotes")
                .get()
                .await() // requires 'kotlinx-coroutines-play-services'
                .documents
                .mapNotNull { it.toObject(Quote::class.java) }
        } catch (e: Exception) {
            emptyList() // return empty list on failure
        }
    }


    // ✅ Fetch full quiz details (with badge info)
    fun getQuizDetails(
        roadmapId: String,
        moduleId: String,
        quizId: String,
        onSuccess: (Quiz) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestore.collection("roadmaps")
            .document(roadmapId)
            .collection("modules")
            .document(moduleId)
            .collection("quizzes")
            .document(quizId)
            .get()
            .addOnSuccessListener { doc ->
                val quiz = doc.toObject(Quiz::class.java)
                if (quiz != null) {
                    onSuccess(quiz)
                } else {
                    onFailure(Exception("Quiz not found"))
                }
            }
            .addOnFailureListener { onFailure(it) }
    }

    // ✅ Mark module as completed
    fun markModuleCompleted(
        roadmapId: String,
        moduleId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestore.collection("roadmaps")
            .document(roadmapId)
            .collection("modules")
            .document(moduleId)
            .update("completed", true)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }
}
