package com.sanjey.codestride.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.sanjey.codestride.common.Constants
import com.sanjey.codestride.data.model.UserStats
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    suspend fun updateStreakOnLearning(userId: String): UserStats {
        val progressDocs = firestore.collection("users")
            .document(userId)
            .collection("progress")
            .get()
            .await()

        var mostRecentDate: String? = null

        for (doc in progressDocs.documents) {
            val date = doc.getString("lastLearnedDate")
            if (date != null && (mostRecentDate == null || date > mostRecentDate)) {
                mostRecentDate = date
            }
        }

        val today = LocalDate.now().toString()
        val yesterday = LocalDate.now().minusDays(1).toString()

        val userRef = firestore.collection("users").document(userId)
        val userDoc = userRef.get().await()
        val currentStreak = userDoc.getLong("streak")?.toInt() ?: 0

        val newStreak = when (mostRecentDate) {
            today -> currentStreak
            yesterday -> currentStreak + 1
            else -> 1
        }

        userRef.update(
            mapOf(
                "streak" to newStreak,
                "lastActiveDate" to today
            )
        ).await()

        val progress = (newStreak / 10f).coerceAtMost(1f)
        val nextBadgeMsg = if (newStreak >= 10) {
            "🔥 Amazing! You're on fire with $newStreak days streak!"
        } else {
            "You're ${10 - newStreak} day(s) away from hitting 10 days!"
        }

        return UserStats(newStreak, progress, nextBadgeMsg)
    }


    suspend fun getUserStats(userId: String): UserStats {
        val snapshot = firestore.collection(Constants.FirestorePaths.USERS)
            .document(userId)
            .get()
            .await()

        val streak = snapshot.getLong("streak")?.toInt() ?: 0
        val progress = (streak / 10f).coerceAtMost(1f)
        val nextBadgeMsg = if (streak >= 10) {
            "🔥 Amazing! You're on fire with $streak days streak!"
        } else {
            "You're ${10 - streak} day(s) away from hitting 10 days!"
        }

        return UserStats(streak, progress, nextBadgeMsg)
    }

    suspend fun replaceUserRoadmap(userId: String, newRoadmapId: String) {
        val userRef = firestore.collection("users").document(userId)

        // Step 1: Delete all old progress
        val progressRef = userRef.collection("progress")
        val docs = progressRef.get().await()
        val batch = firestore.batch()
        for (doc in docs.documents) {
            batch.delete(doc.reference)
        }
        batch.commit().await()

        // Step 2: Get the first module ID from the new roadmap
        val firstModuleId = firestore.collection("roadmaps")
            .document(newRoadmapId)
            .collection("modules")
            .orderBy("order")
            .limit(1)
            .get()
            .await()
            .documents
            .firstOrNull()
            ?.id ?: ""

        // Step 3: Set new progress with first module
        progressRef.document(newRoadmapId).set(
            mapOf(
                "completed_modules" to emptyList<String>(),
                "current_module" to firstModuleId
            )
        ).await()

        // Step 4: Update current roadmap ID
        userRef.update("currentRoadmapId", newRoadmapId).await()
    }








}
