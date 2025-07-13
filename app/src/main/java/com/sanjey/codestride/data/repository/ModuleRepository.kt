package com.sanjey.codestride.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.sanjey.codestride.data.model.Module
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ModuleRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun getModulesForRoadmap(roadmapId: String): List<Module> {
        return try {
            val snapshot = firestore.collection("modules")
                .whereEqualTo("roadmap_id", roadmapId)
                .orderBy("order")
                .get()
                .await()

            snapshot.documents.mapNotNull { it.toObject(Module::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }
}

