package com.sanjey.codestride.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.sanjey.codestride.common.Constants
import com.sanjey.codestride.data.model.Roadmap
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import javax.inject.Inject


class RoadmapRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    // ✅ Fetch all roadmaps
    suspend fun getAllRoadmaps(): List<Roadmap> {
        return try {
            val snapshot = firestore.collection(Constants.FirestorePaths.ROADMAPS).get().await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Roadmap::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ✅ Initialize roadmap progress for a user
    suspend fun startRoadmap(userId: String, roadmapId: String) {
        val userRef = firestore.collection(Constants.FirestorePaths.USERS).document(userId)
        val progressRef = userRef.collection(Constants.FirestorePaths.PROGRESS).document(roadmapId)

        // Update current roadmap
        userRef.update("currentRoadmapId", roadmapId).await()

        // Initialize progress
        val progressData = mapOf(
            "completed_modules" to emptyList<String>(),
            "current_module" to "module1" // Default first module (can move to Constants)
        )
        progressRef.set(progressData).await()
    }

    suspend fun updateCurrentModule(userId: String, roadmapId: String, moduleId: String) {
        firestore.collection("users")
            .document(userId)
            .collection("progress")
            .document(roadmapId)
            .set(mapOf("current_module" to moduleId), SetOptions.merge())
            .await()
    }


    fun observeCurrentRoadmap(userId: String): Flow<String?> = callbackFlow {
        val docRef = firestore.collection(Constants.FirestorePaths.USERS).document(userId)
        val listener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(null)
                return@addSnapshotListener
            }
            val currentRoadmap = snapshot?.getString("currentRoadmapId")
            Log.d("ROADMAP_DEBUG", "observeCurrentRoadmap → snapshot currentRoadmapId=$currentRoadmap")
            trySend(currentRoadmap)
        }
        awaitClose { listener.remove() }
    }



    suspend fun getModulesCount(roadmapId: String): Int {
        val baseCollection = if (roadmapId.startsWith("ai_")) "ai_roadmaps" else Constants.FirestorePaths.ROADMAPS

        val snapshot = firestore.collection(baseCollection)
            .document(roadmapId)
            .collection(Constants.FirestorePaths.MODULES)
            .get()
            .await()

        return snapshot.size()
    }




    suspend fun getRoadmapById(roadmapId: String): Roadmap? {
        return try {
            val doc = firestore.collection("roadmaps").document(roadmapId).get().await()
            if (doc.exists()) {
                doc.toObject(Roadmap::class.java)?.copy(id = doc.id)
            } else {
                val aiDoc = firestore.collection("ai_roadmaps").document(roadmapId).get().await()
                aiDoc.toObject(Roadmap::class.java)?.copy(id = aiDoc.id)
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getModuleTitle(roadmapId: String, moduleId: String): String {
        val collectionPath = if (roadmapId.startsWith("ai_")) "ai_roadmaps" else "roadmaps"
        return try {
            val snapshot = firestore.collection(collectionPath)
                .document(roadmapId)
                .collection("modules")
                .document(moduleId)
                .get()
                .await()
            snapshot.getString("title") ?: "Module"
        } catch (e: Exception) {
            "Module"
        }
    }





    // ✅ Observe progress as Flow
    fun observeProgress(userId: String, roadmapId: String): Flow<Pair<String?, List<String>>> = callbackFlow {
        val listener = firestore.collection(Constants.FirestorePaths.USERS)
            .document(userId)
            .collection(Constants.FirestorePaths.PROGRESS)
            .document(roadmapId)
            .addSnapshotListener { snapshot, _ ->
                val currentModuleId = snapshot?.getString("current_module")
                val completedModules = (snapshot?.get("completed_modules") as? List<*>)?.filterIsInstance<String>() ?: emptyList()
                trySend(currentModuleId to completedModules)
            }
        awaitClose { listener.remove() }
    }

    fun observeRoadmaps(): Flow<List<Roadmap>> = callbackFlow {
        val listener = firestore.collection(Constants.FirestorePaths.ROADMAPS)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val roadmaps = snapshot?.documents?.mapNotNull { doc ->
                    val roadmap = doc.toObject(Roadmap::class.java)
                    roadmap?.copy(id = doc.id)  // ✅ Include document ID
                } ?: emptyList()
                trySend(roadmaps)
            }
        awaitClose { listener.remove() }
    }






    suspend fun updateProgress(userId: String, roadmapId: String, moduleId: String) {
        Log.d("QUIZ_DEBUG", "updateProgress() START → userId=$userId, roadmapId=$roadmapId, moduleId=$moduleId")

        val docRef = firestore.collection("users")
            .document(userId)
            .collection("progress")
            .document(roadmapId)

        try {
            val snapshot = docRef.get().await()
            val completed = (snapshot.get("completed_modules") as? List<*>)?.filterIsInstance<String>()?.toMutableList() ?: mutableListOf()

            if (!completed.contains(moduleId)) {
                completed.add(moduleId)
            }

            val modulesSnapshot = firestore.collection(Constants.FirestorePaths.ROADMAPS)
                .document(roadmapId)
                .collection(Constants.FirestorePaths.MODULES)
                .get()
                .await()
            val allModules = modulesSnapshot.documents
                .map { it.id }
                .sortedBy { it.removePrefix("module").toIntOrNull() ?: Int.MAX_VALUE }

            val nextModuleId = allModules.firstOrNull { it !in completed }

            val today = LocalDate.now().toString() // ✅ NEW

            docRef.update(
                mapOf(
                    "completed_modules" to completed,
                    "current_module" to (nextModuleId ?: moduleId),
                    "lastLearnedDate" to today // ✅ NEW LINE
                )
            ).await()

            Log.d("PROGRESS_DEBUG", "Updated progress: completed=$completed, next=$nextModuleId")
        } catch (e: Exception) {
            Log.e("QUIZ_DEBUG", "updateProgress() ERROR: ${e.message}", e)
        }

        Log.d("QUIZ_DEBUG", "updateProgress() END for moduleId=$moduleId")
    }


}
