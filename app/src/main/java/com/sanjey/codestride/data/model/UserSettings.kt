package com.sanjey.codestride.data.model

data class UserSettings(
    val reminderEnabled: Boolean = true,
    val reminderHour: Int = 17,
    val reminderMinute: Int = 0
)
