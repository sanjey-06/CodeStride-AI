package com.sanjey.codestride.data.model

data class ProgressState(
    val completedModules: List<String> = emptyList(),
    val currentModule: String = "Start from Module 1"
)
