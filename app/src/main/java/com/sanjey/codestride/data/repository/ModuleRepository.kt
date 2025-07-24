package com.sanjey.codestride.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.sanjey.codestride.common.Constants
import com.sanjey.codestride.data.model.Module
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ModuleRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    // ✅ Fetch modules for a roadmap
    suspend fun getModulesForRoadmap(roadmapId: String): List<Module> {
        return try {
            val snapshot = firestore.collection(Constants.FirestorePaths.ROADMAPS)
                .document(roadmapId)
                .collection(Constants.FirestorePaths.MODULES)
                .orderBy("order")
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Module::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList() // Handle in ViewModel with UiState
        }
    }

    suspend fun getModuleById(roadmapId: String, moduleId: String): Module? {
        return try {
            val snapshot = firestore.collection("roadmaps")
                .document(roadmapId)
                .collection("modules")
                .document(moduleId)
                .get()
                .await()

            snapshot.toObject(Module::class.java)?.copy(id = moduleId)
        } catch (e: Exception) {
            null
        }
    }


    // ✅ Fetch module content
    suspend fun getModuleContent(roadmapId: String, moduleId: String): String? {
        return try {
            val docSnapshot = firestore.collection(Constants.FirestorePaths.ROADMAPS)
                .document(roadmapId)
                .collection(Constants.FirestorePaths.MODULES)
                .document(moduleId)
                .get()
                .await()

            docSnapshot.getString("custom_content")
        } catch (e: Exception) {
            null
        }
    }
}
