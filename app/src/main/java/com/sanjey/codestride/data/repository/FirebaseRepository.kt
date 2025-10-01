package com.sanjey.codestride.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sanjey.codestride.BuildConfig
import com.sanjey.codestride.common.Constants
import com.sanjey.codestride.data.model.AIBadge
import com.sanjey.codestride.data.model.Badge
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

    ) {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val uid = result.user?.uid ?: throw Exception("User ID is null")

        val userMap = hashMapOf(
            "firstName" to firstName,
            "lastName" to lastName,
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


    // Read - Firebase Firestore
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


    // Read Simple Collection
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
    // Write - Firebase Firestore
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
            .document("${roadmapId}_$moduleId")
            .set(badgeData)
            .await()
    }

    suspend fun getUserBadges(userId: String): List<Badge> {
        val userBadgeSnapshot = firestore.collection("users")
            .document(userId)
            .collection("badges")
            .get()
            .await()

        val mergedBadges = mutableListOf<Badge>()

        for (doc in userBadgeSnapshot.documents) {
            val basicBadge = doc.toObject(Badge::class.java)
            if (basicBadge != null) {
                // Try to enrich with AI badge details if available
                val aiBadgeSnapshot = firestore.collection("default_ai_badges")
                    .document(doc.id) // Use same doc.id to match AI badge ID
                    .get()
                    .await()

                if (aiBadgeSnapshot.exists()) {
                    val aiBadge = aiBadgeSnapshot.toObject(AIBadge::class.java)
                    mergedBadges.add(
                        Badge(
                            title = aiBadge?.title ?: basicBadge.title,
                            image = aiBadge?.imageUrl ?: basicBadge.image,
                            roadmapId = basicBadge.roadmapId,
                            moduleId = basicBadge.moduleId,
                            dateEarned = basicBadge.dateEarned
                        )
                    )
                } else {
                    mergedBadges.add(basicBadge)
                }
            }
        }
        return mergedBadges
    }




    suspend fun saveAIQuiz(
        roadmapId: String,
        moduleId: String,
        quizId: String,
        quiz: Quiz,
        questions: List<Question>
    ) {

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

    suspend fun getAiBadgeByIndex(index: Int): AIBadge? {
        return try {
            val snapshot = firestore.collection("default_ai_badges")
                .document("badge_$index")
                .get()
                .await()
            snapshot.toObject(AIBadge::class.java)
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                Log.e("FIREBASE_REPO", "❌ Failed to fetch AI badge: ${e.message}")
            }
            null
        }
    }




    suspend fun getBadgeById(badgeId: String): Badge? {
        return try {
            val snapshot = firestore.collection("badges")
                .document(badgeId)
                .get()
                .await()
            snapshot.toObject(Badge::class.java)
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                Log.e("FIREBASE_REPO", "❌ Failed to fetch static badge: ${e.message}")
            }
            null
        }
    }




}
