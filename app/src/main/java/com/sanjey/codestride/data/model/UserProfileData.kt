package com.sanjey.codestride.data.model

data class UserProfileData(
    val fullName: String,
    val avatar: String,
    val currentRoadmapTitle: String,
    val currentModuleTitle: String,
    val completedModulesCount: Int,
    val totalModulesCount: Int
)
