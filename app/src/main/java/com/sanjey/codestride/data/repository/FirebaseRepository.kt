package com.sanjey.codestride.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sanjey.codestride.common.Constants
import com.sanjey.codestride.data.model.AIBadge
import com.sanjey.codestride.data.model.Question
import com.sanjey.codestride.data.model.Quiz
import com.sanjey.codestride.data.model.Quote
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private fun pathFor(roadmapId: String) =
        if (roadmapId.startsWith("ai_")) "ai_roadmaps" else Constants.FirestorePaths.ROADMAPS

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
        val base = pathFor(roadmapId)
        val snapshot = firestore.collection(base)
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
        val base = pathFor(roadmapId)
        val doc = firestore.collection(base)
            .document(roadmapId)
            .collection(Constants.FirestorePaths.MODULES)
            .document(moduleId)
            .collection(Constants.FirestorePaths.QUIZZES)
            .document(quizId)
            .get()
            .await()
        return doc.toObject(Quiz::class.java)
    }

    suspend fun saveBadge(userId: String, title: String, image: String, roadmapId: String, moduleId: String) {
        val badgeData = mapOf(
            "title" to title,
            "image" to image,
            "roadmapId" to roadmapId,
            "moduleId" to moduleId,
            "dateEarned" to System.currentTimeMillis()
        )
        firestore.collection("users")
            .document(userId)
            .collection("badges")
            .document("${roadmapId}_$moduleId") // 🧠 deterministic doc ID
            .set(badgeData)
            .await()
    }

    suspend fun getUserBadges(userId: String): List<com.sanjey.codestride.data.model.Badge> {
        val snapshot = firestore.collection("users")
            .document(userId)
            .collection("badges")
            .get()
            .await()

        return snapshot.toObjects(com.sanjey.codestride.data.model.Badge::class.java)
    }

    suspend fun saveAIQuiz(
        roadmapId: String,
        moduleId: String,
        quizId: String,
        quiz: Quiz,
        questions: List<Question>
    ) {
        // ✅ Select correct top-level collection for AI roadmaps
        val topCollection = if (roadmapId.startsWith("ai_")) {
            "ai_roadmaps"
        } else {
            Constants.FirestorePaths.ROADMAPS
        }

        val quizRef = firestore.collection(topCollection)
            .document(roadmapId)
            .collection(Constants.FirestorePaths.MODULES)
            .document(moduleId)
            .collection(Constants.FirestorePaths.QUIZZES)
            .document(quizId)

        // ✅ Save quiz metadata
        quizRef.set(quiz).await()

        // ✅ Save questions with explicit field mapping
        val batch = firestore.batch()
        questions.forEachIndexed { index, q ->
            val qRef = quizRef.collection(Constants.FirestorePaths.QUESTIONS).document("q${index + 1}")
            val mappedQuestion = mapOf(
                "id" to "q${index + 1}",
                "question_text" to q.questionText,
                "options" to q.options,
                "correct_answer" to q.correctAnswer
            )
            batch.set(qRef, mappedQuestion)
        }
        batch.commit().await()
    }



    suspend fun getBadgeById(badgeId: String): AIBadge? {
        return try {
            val snapshot = firestore.collection("default_ai_badges")
                .document(badgeId)
                .get()
                .await()
            snapshot.toObject(AIBadge::class.java)
        } catch (e: Exception) {
            Log.e("FIREBASE_REPO", "❌ Failed to fetch badge: ${e.message}")
            null
        }
    }




}
