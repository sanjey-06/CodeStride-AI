package com.sanjey.codestride

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.sanjey.codestride.navigation.AppNavigator
import com.sanjey.codestride.ui.theme.CodeStrideTheme
import com.google.firebase.FirebaseApp
import com.sanjey.codestride.workers.ReminderScheduler
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        // ✅ Schedule daily reminder if enabled
        val prefs = getSharedPreferences("reminder_prefs", MODE_PRIVATE)
        val enabled = prefs.getBoolean("reminder_enabled", false)
        if (enabled) {
            val hour = prefs.getInt("reminder_hour", 9)
            val minute = prefs.getInt("reminder_minute", 0)
            ReminderScheduler.scheduleDailyReminder(this, hour, minute)
        }
        setContent {
            CodeStrideTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppNavigator()
                }
            }
        }
    }
}
