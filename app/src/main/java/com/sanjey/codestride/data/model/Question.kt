package com.sanjey.codestride.data.model

import com.google.firebase.firestore.PropertyName

data class Question(
    val id: String = "",

    @get:PropertyName("question_text")
    @set:PropertyName("question_text")
    var questionText: String = "",

    val options: List<String> = emptyList(),

    @get:PropertyName("correct_answer")
    @set:PropertyName("correct_answer")
    var correctAnswer: String = ""
)
