package com.sanjey.codestride.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanjey.codestride.R
import com.sanjey.codestride.common.UiState
import com.sanjey.codestride.data.model.ProgressState
import com.sanjey.codestride.data.model.Roadmap
import com.sanjey.codestride.data.repository.RoadmapRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoadmapViewModel @Inject constructor(
    private val repository: RoadmapRepository
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

    // ✅ Start a roadmap
    fun startRoadmap(userId: String, roadmapId: String) {
        viewModelScope.launch {
            try {
                repository.startRoadmap(userId, roadmapId)
            } catch (e: Exception) {
                // Handle error UI in future if needed
            }
        }
    }

    // ✅ Observe current roadmap as Flow from repository
    fun observeCurrentRoadmap(userId: String) {
        viewModelScope.launch {
            repository.observeCurrentRoadmap(userId).collectLatest { roadmapId ->
                _currentRoadmapId.value = roadmapId
                if (roadmapId != null) observeProgress(userId, roadmapId)
            }
        }
    }

    // ✅ Observe progress from repository
    private fun observeProgress(userId: String, roadmapId: String) {
        viewModelScope.launch {
            repository.observeProgress(userId, roadmapId).collectLatest { currentModuleId ->
                val moduleTitle = if (currentModuleId != null) {
                    repository.getModuleTitle(roadmapId, currentModuleId)
                } else {
                    "Start from Module 1"
                }

                _progressState.value = UiState.Success(
                    ProgressState(
                        completedModules = emptyList(), // Can add completed list later
                        currentModule = moduleTitle // ✅ Now this is the name, not ID
                    )
                )
            }
        }
    }


    // ✅ Update progress
    fun updateProgress(roadmapId: String, moduleId: String) {
        val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                repository.updateProgress(userId, roadmapId, moduleId)
            } catch (e: Exception) {
                // Handle error if needed
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
        return when (roadmapId) {
            "java" -> "Java Programming" to R.drawable.ic_java
            "python" -> "Python Programming" to R.drawable.ic_python
            "cpp" -> "C++ Programming" to R.drawable.ic_cpp
            "kotlin" -> "Kotlin Programming" to R.drawable.ic_kotlin
            "js" -> "JavaScript Programming" to R.drawable.ic_javascript
            else -> "No Roadmap Selected" to R.drawable.ic_none
        }
    }


}
