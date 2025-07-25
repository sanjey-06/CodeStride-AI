package com.sanjey.codestride.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.sanjey.codestride.common.Constants
import com.sanjey.codestride.data.model.Roadmap
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
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
        userRef.update("current_roadmap", roadmapId).await()

        // Initialize progress
        val progressData = mapOf(
            "completed_modules" to emptyList<String>(),
            "current_module" to "module1" // Default first module (can move to Constants)
        )
        progressRef.set(progressData).await()
    }

    // ✅ Observe current roadmap as Flow
    fun observeCurrentRoadmap(userId: String): Flow<String?> = callbackFlow {
        val listener = firestore.collection(Constants.FirestorePaths.USERS)
            .document(userId)
            .addSnapshotListener { snapshot, _ ->
                trySend(snapshot?.getString("current_roadmap"))
            }
        awaitClose { listener.remove() }
    }

    suspend fun getRoadmapById(roadmapId: String): Roadmap? {
        return try {
            val doc = firestore.collection("roadmaps")
                .document(roadmapId)
                .get()
                .await()
            doc.toObject(Roadmap::class.java)?.copy(id = doc.id)
        } catch (e: Exception) {
            null
        }
    }
    suspend fun getModuleTitle(roadmapId: String, moduleId: String): String {
        return try {
            val snapshot = firestore.collection("roadmaps")
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
                val completedModules = snapshot?.get("completed_modules") as? List<String> ?: emptyList()
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
                val roadmaps = snapshot?.toObjects(Roadmap::class.java) ?: emptyList()
                trySend(roadmaps)
            }
        awaitClose { listener.remove() }
    }





    suspend fun updateProgress(userId: String, roadmapId: String, moduleId: String) {
        val docRef = firestore.collection(Constants.FirestorePaths.USERS)
            .document(userId)
            .collection(Constants.FirestorePaths.PROGRESS)
            .document(roadmapId)

        try {
            val snapshot = docRef.get().await()
            val completed = (snapshot.get("completed_modules") as? List<*>)?.filterIsInstance<String>()?.toMutableList()
                ?: mutableListOf()

            Log.d("PROGRESS_DEBUG", "Before Update → Completed Modules: $completed, Current Module: ${snapshot.get("current_module")}")

            if (!completed.contains(moduleId)) {
                completed.add(moduleId)
                Log.d("PROGRESS_DEBUG", "Added $moduleId to completed list")
            }

            // ✅ Fetch all modules in order
            val modulesSnapshot = firestore.collection(Constants.FirestorePaths.ROADMAPS)
                .document(roadmapId)
                .collection(Constants.FirestorePaths.MODULES)
                .orderBy("order")
                .get()
                .await()

            val allModules = modulesSnapshot.documents.map { it.id }
            val nextModuleId = allModules.firstOrNull { !completed.contains(it) }

            Log.d("PROGRESS_DEBUG", "All Modules: $allModules, Next Module: $nextModuleId")

            docRef.set(
                mapOf(
                    "completed_modules" to completed,
                    "current_module" to nextModuleId
                ),
                com.google.firebase.firestore.SetOptions.merge()
            ).await()

            Log.d("PROGRESS_DEBUG", "Firestore Updated → Completed: $completed, Current: $nextModuleId")
        } catch (e: Exception) {
            Log.e("PROGRESS_DEBUG", "Error updating progress: ${e.message}", e)
        }
    }




}
