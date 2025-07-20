package com.sanjey.codestride.data.model

data class Roadmap(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val icon: String = "",  // Firestore: could store image URL or icon name
    val isCustom: Boolean = false
)
