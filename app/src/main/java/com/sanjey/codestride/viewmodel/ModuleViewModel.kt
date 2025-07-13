package com.sanjey.codestride.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanjey.codestride.data.model.Module
import com.sanjey.codestride.data.repository.ModuleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ModuleViewModel @Inject constructor(
    private val repository: ModuleRepository
) : ViewModel() {

    private val _modules = MutableStateFlow<List<Module>>(emptyList())
    val modules: StateFlow<List<Module>> = _modules

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
}
