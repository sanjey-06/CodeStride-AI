package com.sanjey.codestride.data.model

data class Question(
    val id: String = "",
    val questionText: String = "",
    val options: List<String> = emptyList(),
    val correctAnswer: String = ""
)
