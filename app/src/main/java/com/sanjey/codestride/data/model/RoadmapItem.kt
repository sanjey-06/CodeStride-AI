package com.sanjey.codestride.data.model

import com.google.gson.annotations.SerializedName

data class RoadmapItem(
    @SerializedName("title")
    val title: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("link")
    val link: String? = null,

    @SerializedName("html_content")
    val html_content: String? = null,   // keep same name here, no changes elsewhere

    @SerializedName("quizId")
    val quizId: String = "ai_quiz"
)

