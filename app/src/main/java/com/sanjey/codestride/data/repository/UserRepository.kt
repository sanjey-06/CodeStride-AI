package com.sanjey.codestride.data.repository

import android.util.Log
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
        val userRef = firestore.collection(Constants.FirestorePaths.USERS).document(userId)
        val userDoc = userRef.get().await()

        val lastActiveDate = userDoc.getString("lastActiveDate")
        val currentStreak = userDoc.getLong("streak")?.toInt() ?: 0

        val today = LocalDate.now().toString()
        val yesterday = LocalDate.now().minusDays(1).toString()

        val newStreak = when (lastActiveDate) {
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
        Log.d("ROADMAP_DEBUG", "replaceUserRoadmap() → deleting old progress for user=$userId")

        // ✅ Delete all old progress docs
        val progressRef = firestore.collection("users")
            .document(userId)
            .collection("progress")

        val docs = progressRef.get().await()
        val batch = firestore.batch()
        for (doc in docs.documents) {
            batch.delete(doc.reference)
        }
        batch.commit().await()
        Log.d("ROADMAP_DEBUG", "Old progress deleted successfully")

        // ✅ Create new progress doc for new roadmap
        progressRef.document(newRoadmapId).set(
            mapOf(
                "completed_modules" to emptyList<String>(),
                "current_module" to ""
            )
        ).await()
        Log.d("ROADMAP_DEBUG", "New roadmap $newRoadmapId set in progress collection")

        // ✅ Update user document with new roadmap ID
        firestore.collection("users").document(userId)
            .update("currentRoadmapId", newRoadmapId)
            .await()
        Log.d("ROADMAP_DEBUG", "User document updated → currentRoadmapId=$newRoadmapId")
    }







}
