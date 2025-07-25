package com.sanjey.codestride.data.model

data class ProgressState(
    val completedModules: List<String> = emptyList(),
    val currentModuleTitle: String = "Start from Module 1"
)
