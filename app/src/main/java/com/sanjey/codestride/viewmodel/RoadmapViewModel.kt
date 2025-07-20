package com.sanjey.codestride.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.sanjey.codestride.data.repository.RoadmapRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class ProgressState(
    val completedModules: List<String> = emptyList(),
    val currentModule: String = "Start from Module 1"
)

@HiltViewModel
class RoadmapViewModel @Inject constructor(
    private val repository: RoadmapRepository,
    private val firestore: FirebaseFirestore,

) : ViewModel() {

    private val _currentRoadmapId = MutableStateFlow<String?>(null)
    val currentRoadmapId: StateFlow<String?> = _currentRoadmapId

    private val _currentModule = MutableStateFlow("Start from Module 1")
    val currentModule: StateFlow<String> = _currentModule

    private val _progressState = MutableStateFlow(ProgressState())
    val progressState: StateFlow<ProgressState> = _progressState

    private val _roadmaps = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val roadmaps: StateFlow<List<Map<String, Any>>> = _roadmaps

    fun loadRoadmaps() {
        viewModelScope.launch {
            try {
                _roadmaps.value = repository.getAllRoadmaps()
            } catch (e: Exception) {
                Log.e("RoadmapViewModel", "Error loading roadmaps: ${e.message}")
            }
        }
    }

    fun startRoadmap(roadmapId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                repository.startRoadmap(userId, roadmapId)
            } catch (e: Exception) {
                Log.e("RoadmapViewModel", "Error starting roadmap: ${e.message}")
            }
        }
    }

    fun observeCurrentRoadmap() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        repository.observeCurrentRoadmap(userId) { roadmapId ->
            _currentRoadmapId.value = roadmapId
            if (roadmapId != null) observeProgress(roadmapId)
        }
    }

    private fun observeProgress(roadmapId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        firestore.collection("users")
            .document(userId)
            .collection("progress")
            .document(roadmapId)
            .addSnapshotListener { snapshot, _ ->
                Log.d("RoadmapViewModel", "Listening to progress for $roadmapId")

                if (snapshot != null && snapshot.exists()) {
                    Log.d("RoadmapViewModel", "Progress snapshot updated: ${snapshot.data}")

                    val completed = (snapshot.get("completed_modules") as? List<*>)?.filterIsInstance<String>() ?: emptyList()
                    val currentModuleId = snapshot.getString("current_module") ?: ""

                    if (currentModuleId.isNotEmpty()) {
                        // Fetch the title from modules collection
                        firestore.collection("roadmaps")
                            .document(roadmapId)
                            .collection("modules")
                            .document(currentModuleId)
                            .get()
                            .addOnSuccessListener { moduleDoc ->
                                val moduleTitle = moduleDoc.getString("title") ?: "Start from Module 1"
                                _progressState.value = ProgressState(completedModules = completed, currentModule = moduleTitle)
                                _currentModule.value = moduleTitle
                            }
                    } else {
                        _progressState.value = ProgressState(completedModules = completed, currentModule = "Start from Module 1")
                        _currentModule.value = "Start from Module 1"
                    }
                }
            }
    }


    fun updateProgress(roadmapId: String, moduleId: String) {
        viewModelScope.launch {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
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

                // ✅ Fetch all modules in order
                val modulesSnapshot = firestore.collection("roadmaps")
                    .document(roadmapId)
                    .collection("modules")
                    .orderBy("order")
                    .get()
                    .await()

                val allModules = modulesSnapshot.documents.map { it.id }
                val currentIndex = allModules.indexOf(moduleId)
                val nextModuleId = if (currentIndex != -1 && currentIndex + 1 < allModules.size) {
                    allModules[currentIndex + 1]
                } else {
                    null
                }

                docRef.set(
                    mapOf(
                        "completed_modules" to completed,
                        "current_module" to (nextModuleId ?: moduleId)
                    ),
                    SetOptions.merge()
                ).await()

                Log.d("RoadmapViewModel", "✅ Progress updated! Next: ${nextModuleId ?: moduleId}")
            } catch (e: Exception) {
                Log.e("RoadmapViewModel", "❌ Error updating progress: ${e.message}")
            }
        }
    }
}
