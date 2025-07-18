package com.sanjey.codestride.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.sanjey.codestride.common.UiState
import com.sanjey.codestride.data.model.Module
import com.sanjey.codestride.data.repository.ModuleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ModuleViewModel @Inject constructor(
    private val repository: ModuleRepository,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _modules = MutableStateFlow<List<Module>>(emptyList())
    val modules: StateFlow<List<Module>> = _modules

    private val _selectedModuleContent = MutableStateFlow<Pair<String, String>?>(null)
    val selectedModuleContent: StateFlow<Pair<String, String>?> = _selectedModuleContent

    // ✅ NEW: StateFlow for HTML content with UiState
    private val _moduleHtmlState = MutableStateFlow<UiState<String>>(UiState.Loading)
    val moduleHtmlState: StateFlow<UiState<String>> = _moduleHtmlState

    fun loadModules(roadmapId: String) {
        viewModelScope.launch {
            val fetchedModules = repository.getModulesForRoadmap(roadmapId)
            Log.d("ModuleDebug", "Loaded modules: ${fetchedModules.size}")
            fetchedModules.forEach {
                Log.d("ModuleDebug", "Module: ${it.id}, ${it.title}, ${it.order}")
            }
            _modules.value = fetchedModules
        }
    }

    // ✅ NEW FUNCTION: Fetch HTML content for a module
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
                Log.e("ModuleViewModel", "Error: ${e.message}")
            }
        }
    }

    // ✅ Keep existing loadModuleContent() intact for title + fallback
    fun loadModuleContent(roadmapId: String, moduleId: String) {
        firestore.collection("roadmaps")
            .document(roadmapId)
            .collection("modules")
            .document(moduleId)
            .get()
            .addOnSuccessListener { doc ->
                val title = doc.getString("title") ?: "No Title"
                val content = doc.getString("custom_content") ?: "No content available."
                _selectedModuleContent.value = title to content
            }
            .addOnFailureListener {
                _selectedModuleContent.value = "Error" to "Failed to load content."
            }
    }
}
