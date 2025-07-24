package com.sanjey.codestride.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.sanjey.codestride.common.Constants
import com.sanjey.codestride.data.model.Question
import com.sanjey.codestride.data.model.Quiz
import com.sanjey.codestride.data.model.Quote
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    // ✅ Fetch user first name
    suspend fun getFirstName(): String {
        val uid = auth.currentUser?.uid ?: return "Learner"
        val snapshot = firestore.collection(Constants.FirestorePaths.USERS)
            .document(uid)
            .get()
            .await()
        return snapshot.getString("firstName") ?: "Learner"
    }


    suspend fun sendPasswordReset(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }


    suspend fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    suspend fun signupUser(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        mobile: String
    ) {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val uid = result.user?.uid ?: throw Exception("User ID is null")

        val userMap = hashMapOf(
            "firstName" to firstName,
            "lastName" to lastName,
            "mobile" to mobile,
            "email" to email
        )

        firestore.collection(Constants.FirestorePaths.USERS)
            .document(uid)
            .set(userMap)
            .await()
    }
    fun logout() {
        auth.signOut()
    }


    // ✅ Fetch questions (Suspend)
    suspend fun getQuestionsByQuiz(roadmapId: String, moduleId: String, quizId: String): List<Question> {
        val snapshot = firestore.collection(Constants.FirestorePaths.ROADMAPS)
            .document(roadmapId)
            .collection(Constants.FirestorePaths.MODULES)
            .document(moduleId)
            .collection(Constants.FirestorePaths.QUIZZES)
            .document(quizId)
            .collection(Constants.FirestorePaths.QUESTIONS)
            .get()
            .await()
        return snapshot.toObjects(Question::class.java)
    }

    // ✅ Fetch quotes
    suspend fun getQuotes(): List<Quote> {
        return try {
            firestore.collection(Constants.FirestorePaths.QUOTES)
                .get()
                .await()
                .toObjects(Quote::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ✅ Fetch quiz details
    suspend fun getQuizDetails(roadmapId: String, moduleId: String, quizId: String): Quiz? {
        val doc = firestore.collection(Constants.FirestorePaths.ROADMAPS)
            .document(roadmapId)
            .collection(Constants.FirestorePaths.MODULES)
            .document(moduleId)
            .collection(Constants.FirestorePaths.QUIZZES)
            .document(quizId)
            .get()
            .await()
        return doc.toObject(Quiz::class.java)
    }

    // ✅ Mark module as completed
    suspend fun markModuleCompleted(roadmapId: String, moduleId: String) {
        firestore.collection(Constants.FirestorePaths.ROADMAPS)
            .document(roadmapId)
            .collection(Constants.FirestorePaths.MODULES)
            .document(moduleId)
            .update("completed", true)
            .await()
    }
}
