package com.sanjey.codestride.data.repository


import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RoadmapRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    // ✅ Fetch roadmaps from Firestore (or keep static for now)
    suspend fun getAllRoadmaps(): List<Map<String, Any>> {
        return try {
            val snapshot = firestore.collection("roadmaps").get().await()
            snapshot.documents.mapNotNull { doc ->
                doc.data?.plus("id" to doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ✅ Initialize roadmap progress for user
    suspend fun startRoadmap(userId: String, roadmapId: String) {
        try {
            val userRef = firestore.collection("users").document(userId)
            val progressRef = userRef.collection("progress").document(roadmapId)

            userRef.update("current_roadmap", roadmapId).await()

            val progressData = mapOf(
                "completed_modules" to emptyList<String>(),
                "current_module" to "module1" // default to first module
            )
            progressRef.set(progressData).await()
        } catch (e: Exception) {
            throw e
        }
    }

    // ✅ Observe current roadmap
    fun observeCurrentRoadmap(userId: String, onChange: (String?) -> Unit) {
        firestore.collection("users").document(userId)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    onChange(snapshot.getString("current_roadmap"))
                } else {
                    onChange(null)
                }
            }
    }

    // ✅ Observe progress for roadmap
    fun observeProgress(userId: String, roadmapId: String, onChange: (String?) -> Unit) {
        firestore.collection("users").document(userId)
            .collection("progress").document(roadmapId)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    onChange(snapshot.getString("current_module"))
                } else {
                    onChange(null)
                }
            }
    }
}
