package com.sanjey.codestride.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.sanjey.codestride.data.model.Module
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ModuleRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun getModulesForRoadmap(roadmapId: String): List<Module> {
        return try {
            Log.d("ModuleDebug", "Querying modules from path: roadmaps/$roadmapId/modules")

            val snapshot = firestore.collection("roadmaps")
                .document(roadmapId)
                .collection("modules")
                .orderBy("order")
                .get()
                .await()

            Log.d("ModuleDebug", "Documents fetched: ${snapshot.size()}")

            val modules = snapshot.documents.mapNotNull { doc ->
                val module = doc.toObject(Module::class.java)
                module?.copy(id = doc.id) // ✅ Use Firestore doc ID
            }

            modules.forEach {
                Log.d("ModuleDebug", "Mapped Module -> id: ${it.id}, title: ${it.title}")
            }

            modules
        } catch (e: Exception) {
            Log.e("ModuleDebug", "Error fetching modules: ${e.message}")
            emptyList()
        }
    }
    // ✅ NEW FUNCTION: Fetch custom_content for a specific module
    suspend fun getModuleContent(roadmapId: String, moduleId: String): String? {
        return try {
            val docSnapshot = firestore.collection("roadmaps")
                .document(roadmapId)
                .collection("modules")
                .document(moduleId)
                .get()
                .await()

            if (docSnapshot.exists()) {
                docSnapshot.getString("custom_content")
            } else {
                Log.e("ModuleRepository", "Module document not found")
                null
            }
        } catch (e: Exception) {
            Log.e("ModuleRepository", "Error fetching module content: ${e.message}")
            null
        }
    }
}


