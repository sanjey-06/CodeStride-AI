package com.sanjey.codestride.data.model

import com.google.firebase.firestore.PropertyName


data class Module(
    val id: String = "",
    val title: String = "",
    val order: Int = 0,
    @get:PropertyName("custom_content") @set:PropertyName("custom_content")
    var customContent: String = "",
    @get:PropertyName("yt_url") @set:PropertyName("yt_url")
    var ytUrl: String = "",
    val description: String = "",
    @get:PropertyName("quiz_id") @set:PropertyName("quiz_id")
    var quizId: String = ""
)

