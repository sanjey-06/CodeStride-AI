package com.sanjey.codestride.data.model

data class RoadmapItem(
    val title: String,
    val description: String,
    val link: String,
    val html_content: String,
    val quizId: String = "ai_quiz" // default for AI-generated modules


)
