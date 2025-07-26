package com.sanjey.codestride.data.model

data class RoadmapUI(
    val title: String,
    val iconResId: Int,
    val progressPercent: Int,
    val currentModuleTitle: String = "Start from Module 1"
)
