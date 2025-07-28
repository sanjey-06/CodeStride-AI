package com.sanjey.codestride.data.model

data class HomeScreenData(
    val firstName: String,
    val userStats: UserStats,
    val currentRoadmap: RoadmapUI,
    val badges: List<Triple<String, Int, Boolean>>,
    val exploreRoadmaps: List<Roadmap>,
    val quote: Quote
)
