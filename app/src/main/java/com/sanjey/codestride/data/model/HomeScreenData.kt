package com.sanjey.codestride.data.model

data class HomeScreenData(
    val firstName: String,
    val userStats: UserStats,
    val currentRoadmap: RoadmapUI,
    val badges: List<Badge>,
    val exploreRoadmaps: List<Roadmap>,
    val quote: Quote,

)
