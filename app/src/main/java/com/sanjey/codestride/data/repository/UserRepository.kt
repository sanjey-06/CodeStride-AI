package com.sanjey.codestride.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.sanjey.codestride.common.Constants
import com.sanjey.codestride.data.model.UserProfileData
import com.sanjey.codestride.data.model.UserSettings
import com.sanjey.codestride.data.model.UserStats
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseRepository: FirebaseRepository
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

    suspend fun markLearnedToday(userId: String, roadmapId: String) {
        val today = LocalDate.now().toString()
        firestore.collection("users")
            .document(userId)
            .collection("progress")
            .document(roadmapId)
            .update("lastLearnedDate", today)
            .await()
    }
    fun updateAvatar(userId: String, avatar: String) {
        firestore.collection(Constants.FirestorePaths.USERS)
            .document(userId)
            .update("avatar", avatar)
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

    suspend fun getUserProfileData(userId: String): UserProfileData {
        val userDoc = firestore.collection("users").document(userId).get().await()
        val firstName = userDoc.getString("firstName") ?: ""
        val lastName = userDoc.getString("lastName") ?: ""
        val avatar = userDoc.getString("avatar") ?: "avatar_1"
        val email = userDoc.getString("email") ?: ""


        val progressSnapshot = firestore.collection("users")
            .document(userId)
            .collection("progress")
            .get()
            .await()

        if (progressSnapshot.isEmpty) {
            return UserProfileData(
                fullName = "$firstName $lastName",
                avatar = avatar,
                email = email, // ✅ if you have email as a variable, use it. Else, ""
                currentRoadmapTitle = "None",
                currentModuleTitle = "None",
                completedModulesCount = 0,
                totalModulesCount = 0
            )        }

        val firstProgress = progressSnapshot.documents.first()
        val roadmapId = firstProgress.id
        val completedModules = firstProgress.get("completed_modules") as? List<*> ?: emptyList<Any>()
        val currentModuleId = firstProgress.getString("current_module") ?: ""

        val roadmapDoc = firestore.collection("roadmaps").document(roadmapId).get().await()
        val roadmapTitle = roadmapDoc.getString("title") ?: roadmapId
            .removePrefix("ai_")
            .split("_")
            .joinToString(" ") { it.replaceFirstChar { c -> c.uppercaseChar() } }

        val modulesSnapshot = firestore.collection("roadmaps")
            .document(roadmapId)
            .collection("modules")
            .get()
            .await()

        val totalModulesCount = modulesSnapshot.size()
        val currentModuleDoc = modulesSnapshot.documents.find { it.id == currentModuleId }
        val currentModuleTitle = currentModuleDoc?.getString("title") ?: currentModuleId

        return UserProfileData(
            fullName = "$firstName $lastName",
            avatar = avatar,
            currentRoadmapTitle = roadmapTitle,
            currentModuleTitle = currentModuleTitle,
            completedModulesCount = completedModules.size,
            totalModulesCount = totalModulesCount
        )
    }


    suspend fun getUserSettings(userId: String): UserSettings {
        val docRef = firestore.collection("users")
            .document(userId)
            .collection("meta")
            .document("settings")

        val snapshot = docRef.get().await()

        return if (snapshot.exists()) {
            snapshot.toObject(UserSettings::class.java) ?: UserSettings()
        } else {
            val defaultSettings = UserSettings()
            docRef.set(defaultSettings).await()
            defaultSettings
        }
    }


    suspend fun updateUserSettings(userId: String, settings: UserSettings) {
        firestore.collection("users")
            .document(userId)
            .collection("meta")
            .document("settings")
            .set(settings)
            .await()
    }


    suspend fun deleteUserSettings(userId: String) {
        firestore.collection("users")
            .document(userId)
            .collection("meta")
            .document("settings")
            .delete()
            .await()
    }

    fun logout() {
        firebaseRepository.logout()
    }


}
