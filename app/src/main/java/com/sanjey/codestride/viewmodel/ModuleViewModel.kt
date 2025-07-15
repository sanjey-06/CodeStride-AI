package com.sanjey.codestride.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
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

    fun loadModuleContent(roadmapId: String, moduleId: String) {
        Log.d("ModuleContentDebug", "Fetching content for $roadmapId -> $moduleId")

        firestore.collection("roadmaps")
            .document(roadmapId)
            .collection("modules")
            .document(moduleId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    Log.d("ModuleContentDebug", "Document Data: ${doc.data}")
                } else {
                    Log.d("ModuleContentDebug", "Document does not exist!")
                }

                val title = doc.getString("title") ?: "No Title"
                val content = doc.getString("custom_content") ?: "No content available."
                Log.d("ModuleContentDebug", "Mapped Title: $title | Content: $content")

                _selectedModuleContent.value = title to content
            }
            .addOnFailureListener {
                Log.e("ModuleContentDebug", "Failed to fetch: ${it.message}")
                _selectedModuleContent.value = "Error" to "Failed to load content."
            }
    }

}
