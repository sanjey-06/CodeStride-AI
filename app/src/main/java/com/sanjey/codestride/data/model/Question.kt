package com.sanjey.codestride.data.model

import com.google.firebase.firestore.PropertyName
import com.google.gson.annotations.SerializedName

data class Question(
    val id: String = "",

    // Maps Firestore "question_text" and AI JSON "question_text" to Kotlin property
    @get:PropertyName("question_text")
    @set:PropertyName("question_text")
    @SerializedName("question_text")
    var questionText: String = "",

    // Maps Firestore/AI JSON "options" array
    @SerializedName("options")
    val options: List<String> = emptyList(),

    // Maps Firestore "correct_answer" and AI JSON "correct_answer" to Kotlin property
    @get:PropertyName("correct_answer")
    @set:PropertyName("correct_answer")
    @SerializedName("correct_answer")
    var correctAnswer: String = ""
)
