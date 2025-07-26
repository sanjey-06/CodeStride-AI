package com.sanjey.codestride.data.model

data class ProgressState(
    val completedModules: List<String> = emptyList(),
    val currentModuleId: String? = null,
    val currentModuleTitle: String = "Start from Module 1"
)
