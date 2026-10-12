package com.sanjey.codestride.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sanjey.codestride.common.UiState
import com.sanjey.codestride.data.model.Module
import com.sanjey.codestride.data.repository.AiGenerationRepository
import com.sanjey.codestride.data.repository.ModuleRepository
import com.sanjey.codestride.data.remote.backend.BackendError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ModuleViewModel @Inject constructor(
    private val repository: ModuleRepository,
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
                } else {
                    _modulesState.value = UiState.Empty
                }
            } catch (e: Exception) {
                _modulesState.value = UiState.Error("Failed to load modules")
            }
        }
    }

    fun loadCompletedModules(roadmapId: String) {
        viewModelScope.launch {
            _modulesState.value = UiState.Loading
            try {
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
                val firestore = FirebaseFirestore.getInstance()

                val snapshot = firestore.collection("users")
                    .document(userId)
                    .collection("completed_roadmaps")
                    .document(roadmapId)
                    .collection("modules")
                    .get()
                    .await()

                val modules = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Module::class.java)?.copy(id = doc.id)
                }

                _modulesState.value = UiState.Success(modules)
            } catch (e: Exception) {
                _modulesState.value = UiState.Error("Failed to load completed modules: ${e.message}")
            }
        }
    }


    fun generateContentIfNeeded(topic: String, roadmapId: String, moduleId: String) {
        viewModelScope.launch {
            _moduleHtmlState.value = UiState.Loading

            try {
                val module = repository.getModuleById(roadmapId, moduleId)

                if (module?.customContent.isNullOrBlank()) {
                    // Not generated yet -> ask the Worker, then save it so it's only generated once
                    aiGenerationRepository.generateModuleContent(topic, module?.title ?: "Learning")
                        .onSuccess { html ->
                            repository.updateModuleContent(roadmapId, moduleId, html)
                            _moduleHtmlState.value = UiState.Success(html)
                        }
                        .onFailure { e ->
                            // Limit reached / AI busy / no internet -> shown by the screen's existing error text
                            _moduleHtmlState.value = UiState.Error(
                                (e as? BackendError)?.userMessage ?: "Failed to generate content"
                            )
                        }
                } else {
                    // Already generated earlier -> show the saved HTML (no AI call, no limit used)
                    _moduleHtmlState.value = UiState.Success(module?.customContent ?: "")
                }

            } catch (e: CancellationException) {
                throw e // screen closed - not an error
            } catch (e: Exception) {
                _moduleHtmlState.value = UiState.Error("Something went wrong. Please try again.")
            }
        }
    }



}
