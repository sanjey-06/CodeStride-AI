package com.sanjey.codestride.data.model

import com.google.firebase.firestore.PropertyName

data class Quiz(
    val id: String = "",

    @get:PropertyName("passing_score")
    @set:PropertyName("passing_score")
    var passingScore: Int = 0,

    @get:PropertyName("total_questions")
    @set:PropertyName("total_questions")
    var totalQuestions: Int = 0,

    @get:PropertyName("badge_image")
    @set:PropertyName("badge_image")
    var badgeImage: String = "",

    @get:PropertyName("badge_title")
    @set:PropertyName("badge_title")
    var badgeTitle: String = "",

    @get:PropertyName("badge_description")
    @set:PropertyName("badge_description")
    var badgeDescription: String = ""
)

