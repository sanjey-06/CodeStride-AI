package com.sanjey.codestride.data.model

import com.google.firebase.firestore.PropertyName
import com.google.gson.annotations.SerializedName

data class AIBadge(
    val title: String = "",
    val description: String = "",
    @get:PropertyName("image_url")   // Firestore reads via getter
    @field:PropertyName("image_url") // Firestore writes via field
    @SerializedName("image_url")     // Gson for Retrofit
    val imageUrl: String = "",
    val level: Int = 0,
    val theme: String = ""
)
