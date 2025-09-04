package com.sanjey.codestride.data.model

data class RoadmapItem(
    val title: String? = null,          // nullable so missing field won’t crash
    val description: String? = null,    // nullable
    val link: String? = null,           // nullable
    val html_content: String? = null,   // nullable
    val quizId: String = "ai_quiz"      // safe default
)

