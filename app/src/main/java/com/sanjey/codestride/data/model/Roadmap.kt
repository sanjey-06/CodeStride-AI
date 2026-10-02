package com.sanjey.codestride.data.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Roadmap(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val icon: String = "",
    val isCustom: Boolean = false
)
