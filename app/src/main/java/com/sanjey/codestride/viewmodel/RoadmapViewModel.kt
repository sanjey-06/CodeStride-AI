package com.sanjey.codestride.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sanjey.codestride.R
import com.sanjey.codestride.common.UiState
import com.sanjey.codestride.common.getIconResource
import com.sanjey.codestride.data.model.ProgressState
import com.sanjey.codestride.data.model.Roadmap
import com.sanjey.codestride.data.repository.AiGenerationRepository
import com.sanjey.codestride.data.repository.RoadmapRepository
import com.sanjey.codestride.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RoadmapViewModel @Inject constructor(
    private val repository: RoadmapRepository,
    private val userRepository: UserRepository,
    private val aiGenerationRepository: AiGenerationRepository
) : ViewModel() {

    private val _roadmapsState = MutableStateFlow<UiState<List<Roadmap>>>(UiState.Idle)
    val roadmapsState: StateFlow<UiState<List<Roadmap>>> = _roadmapsState

    private val _currentRoadmapId = MutableStateFlow<String?>(null)
    val currentRoadmapId: StateFlow<String?> = _currentRoadmapId

    private val _progressState = MutableStateFlow(UiState.Idle as UiState<ProgressState>)
    val progressState: StateFlow<UiState<ProgressState>> = _progressState

    private val _currentRoadmapTitle = mutableStateOf("Learning")
    val currentRoadmapTitle: State<String> = _currentRoadmapTitle


    // ✅ Load all roadmaps
    fun loadRoadmaps() {
        viewModelScope.launch {
            _roadmapsState.value = UiState.Loading
            try {
                val roadmaps = repository.getAllRoadmaps()
                if (roadmaps.isNotEmpty()) {
                    _roadmapsState.value = UiState.Success(roadmaps)
                } else {
                    _roadmapsState.value = UiState.Empty
                }
            } catch (e: Exception) {
                _roadmapsState.value = UiState.Error(e.message ?: "Failed to load roadmaps")
            }
        }
    }



    fun startRoadmap(roadmapId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            repository.startRoadmap(userId, roadmapId)
        }
    }





    fun hasActiveRoadmap(): Boolean {
        return _currentRoadmapId.value != null
    }



    suspend fun replaceRoadmap(newRoadmapId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        try {
            Log.d("ROADMAP_DEBUG", "replaceRoadmap() started for $newRoadmapId")
            userRepository.replaceUserRoadmap(userId, newRoadmapId)
            Log.d("ROADMAP_DEBUG", "replaceRoadmap() complete → $newRoadmapId")
        } catch (e: Exception) {
            Log.e("ROADMAP_DEBUG", "replaceRoadmap ERROR: ${e.message}")
        }
    }







    // ✅ Observe current roadmap as Flow from repository
    fun observeCurrentRoadmap() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            Log.d("ROADMAP_DEBUG", "observeCurrentRoadmap() started for user=$userId")

            repository.observeCurrentRoadmap(userId).collectLatest { roadmapId ->
                Log.d("ROADMAP_DEBUG", "Firestore emitted roadmapId=$roadmapId")

                _currentRoadmapId.value = roadmapId
                if (roadmapId != null) observeProgress(userId, roadmapId)
            }
        }
    }

    fun updateCurrentModuleIfForward(roadmapId: String, moduleId: String, completedModules: List<String>) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            // ✅ Only update if this module is not already completed
            if (!completedModules.contains(moduleId)) {
                repository.updateCurrentModule(userId, roadmapId, moduleId)
            }

        }
    }



    // ✅ Observe progress from repository
    private fun observeProgress(userId: String, roadmapId: String) {
        viewModelScope.launch {
            repository.observeProgress(userId, roadmapId).collectLatest { (currentModuleId, completedModules) ->
                val moduleTitle = if (currentModuleId != null) {
                    repository.getModuleTitle(roadmapId, currentModuleId)

                } else {
                    "Start from Module 1"
                }

                _progressState.value = UiState.Success(

                    ProgressState(
                        completedModules = completedModules,
                        currentModuleId = currentModuleId,
                        currentModuleTitle = moduleTitle
                    )

                )
                Log.d("ROADMAP_DEBUG", "observeProgress() → currentModuleId=$currentModuleId, title=$moduleTitle, completed=$completedModules")

            }
        }
    }
    fun updateStreak(roadmapId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val stats = userRepository.updateStreakOnLearning(userId) // ✅ now ignores roadmap
                userRepository.markLearnedToday(userId, roadmapId)        // keeps per-roadmap date

                Log.d("STREAK_DEBUG", "✅ New streak = ${stats.streak}, progress = ${stats.progressPercent}")
            } catch (e: Exception) {
                Log.e("STREAK_DEBUG", "❌ Failed to update streak: ${e.message}")
            }
        }
    }


    suspend fun generateAiRoadmapAndReturnId(topic: String): String? = withContext(Dispatchers.IO) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@withContext null
        val roadmapId = "ai_" + topic.lowercase().replace(" ", "_")

        // Save roadmap metadata first
        val roadmapData = mapOf(
            "title" to topic.replaceFirstChar { it.uppercase() },
            "description" to "Custom roadmap for $topic",
            "icon" to "ic_none",
            "created_by" to userId,
            "isCustom" to true
        )

        try {
            val firestore = FirebaseFirestore.getInstance()
            val roadmapRef = firestore.collection("ai_roadmaps").document(roadmapId)
            roadmapRef.set(roadmapData).await()

            // 👇 Call new function to generate + store enriched modules
            aiGenerationRepository.generateAndStoreRoadmap(topic, roadmapId) {
                Log.d("ROADMAP_AI", "All AI modules uploaded for $roadmapId")
            }

            return@withContext roadmapId

        } catch (e: Exception) {
            Log.e("ROADMAP_AI", "Error creating AI roadmap: ${e.message}")
            return@withContext null
        }
    }







    // ✅ Update progress
    fun updateProgress(roadmapId: String, moduleId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                Log.d("ROADMAP_DEBUG", "updateProgress() START → roadmapId=$roadmapId, moduleId=$moduleId")
                repository.updateProgress(userId, roadmapId, moduleId)

                // ✅ Update UI immediately
                val moduleTitle = repository.getModuleTitle(roadmapId, moduleId)
                Log.d("ROADMAP_DEBUG", "updateProgress() Firestore update COMPLETE")

                val completedModules = (progressState.value as? UiState.Success)?.data?.completedModules ?: emptyList()
                _progressState.value = UiState.Success(
                    ProgressState(
                        completedModules = completedModules + moduleId,
                        currentModuleTitle = moduleTitle
                    )
                )
            } catch (e: Exception) {
                Log.e("ROADMAP_DEBUG", "updateProgress() ERROR: ${e.message}")
            }
        }
    }



    fun loadRoadmapTitle(roadmapId: String) {
        viewModelScope.launch {
            val roadmap = repository.getRoadmapById(roadmapId)
            _currentRoadmapTitle.value = roadmap?.title ?: "Learning"
        }
    }

    fun getRoadmapTitleAndIcon(roadmapId: String?): Pair<String, Int> {
        val normalizedId = roadmapId?.trim()?.lowercase() ?: ""
        Log.d("DEBUG_ICON", "normalizedId = $normalizedId")

        return when {
            normalizedId.startsWith("ai_") -> {
                val title = normalizedId.removePrefix("ai_").replace("_", " ")
                    .replaceFirstChar { it.uppercase() }
                title to getIconResource(null, roadmapId)
            }
            normalizedId == "java" -> "Java Programming" to R.drawable.ic_java
            normalizedId == "python" -> "Python Programming" to R.drawable.ic_python
            normalizedId == "cpp" -> "C++ Programming" to R.drawable.ic_cpp
            normalizedId == "kotlin" -> "Kotlin Programming" to R.drawable.ic_kotlin
            normalizedId in listOf("js", "javascript") -> "JavaScript Programming" to R.drawable.ic_javascript
            else -> "No Roadmap Selected" to R.drawable.ic_none
        }
    }



}
