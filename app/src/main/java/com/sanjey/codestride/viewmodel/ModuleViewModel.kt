package com.sanjey.codestride.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanjey.codestride.common.UiState
import com.sanjey.codestride.data.model.Module
import com.sanjey.codestride.data.repository.AiGenerationRepository
import com.sanjey.codestride.data.repository.ModuleRepository
import com.sanjey.codestride.data.repository.RoadmapRepository
import com.sanjey.codestride.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ModuleViewModel @Inject constructor(
    private val repository: ModuleRepository,
    private val userRepository: UserRepository,
    private val roadmapRepository: RoadmapRepository,
    private val aiGenerationRepository: AiGenerationRepository
) : ViewModel() {

    private val _modulesState = MutableStateFlow<UiState<List<Module>>>(UiState.Loading)
    val modulesState: StateFlow<UiState<List<Module>>> = _modulesState

    private val _moduleHtmlState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val moduleHtmlState: StateFlow<UiState<String>> = _moduleHtmlState


    private val _moduleTitle = MutableStateFlow("")
    val moduleTitle: StateFlow<String> = _moduleTitle

    fun fetchModuleDetails(roadmapId: String, moduleId: String) {
        viewModelScope.launch {
            try {
                val module = repository.getModuleById(roadmapId, moduleId)
                _moduleTitle.value = module?.title ?: "Module"
            } catch (e: Exception) {
                _moduleTitle.value = "Module"
            }
        }
    }


    // ✅ Load all modules for a roadmap
    fun loadModules(roadmapId: String) {
        viewModelScope.launch {
            _modulesState.value = UiState.Loading
            try {
                val fetchedModules = repository.getModulesForRoadmap(roadmapId)
                if (fetchedModules.isNotEmpty()) {
                    _modulesState.value = UiState.Success(fetchedModules)
                    Log.d("MODULES_FETCH", "Loading modules for: $roadmapId")

                } else {
                    _modulesState.value = UiState.Empty
                }
            } catch (e: Exception) {
                _modulesState.value = UiState.Error("Failed to load modules")
            }
        }
    }

    // ✅ Fetch HTML content for a specific module
    fun fetchModuleContent(roadmapId: String, moduleId: String) {
        viewModelScope.launch {
            _moduleHtmlState.value = UiState.Loading
            try {
                val htmlContent = repository.getModuleContent(roadmapId, moduleId)
                if (!htmlContent.isNullOrBlank()) {
                    _moduleHtmlState.value = UiState.Success(htmlContent)
                } else {
                    _moduleHtmlState.value = UiState.Error("Content not found")
                }
            } catch (e: Exception) {
                _moduleHtmlState.value = UiState.Error("Failed to load content")
            }
        }
    }
    fun updateLearningProgress(roadmapId: String, moduleId: String) {
        viewModelScope.launch {
            val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            try {
                userRepository.updateStreakOnLearning(userId, roadmapId)
                roadmapRepository.updateProgress(userId, roadmapId, moduleId)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }




    fun generateContentIfNeeded(topic: String, roadmapId: String, moduleId: String) {
        viewModelScope.launch {
            _moduleHtmlState.value = UiState.Loading

            try {
                val module = repository.getModuleById(roadmapId, moduleId)

                if (module?.customContent.isNullOrBlank()) {
                    val html = aiGenerationRepository.generateModuleContent(topic, module?.title ?: "Learning")
                    if (html.isNotBlank()) {
                        repository.updateModuleContent(roadmapId, moduleId, html)
                        _moduleHtmlState.value = UiState.Success(html)
                    } else {
                        _moduleHtmlState.value = UiState.Error("Failed to generate content")
                    }
                } else {
                    _moduleHtmlState.value = UiState.Success(module?.customContent ?: "")
                }

            } catch (e: Exception) {
                _moduleHtmlState.value = UiState.Error("Something went wrong: ${e.message}")
            }
        }
    }



}
