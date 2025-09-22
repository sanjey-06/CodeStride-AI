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
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.sanjey.codestride.data.prefs.OnboardingPreferences
import com.sanjey.codestride.ui.screens.onboarding.ConsentScreen
import kotlinx.coroutines.launch
import androidx.core.net.toUri


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // 🔔 Permission launcher
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(
                    this,
                    "✅ Notifications enabled! You'll get streak reminders.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this,
                    "⚠️ Notifications are disabled. Please enable it in the Settings.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }


    private fun askNotificationPermission() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                when {
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        // Already granted
                    }
                    else -> {
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        askNotificationPermission()

        val prefs = getSharedPreferences("reminder_prefs", MODE_PRIVATE)
        val enabled = prefs.getBoolean("reminder_enabled", false)
        if (enabled) {
            val hour = prefs.getInt("reminder_hour", 9)
            val minute = prefs.getInt("reminder_minute", 0)
            ReminderScheduler.scheduleDailyReminder(this, hour, minute)
        }
        setContent {
            val context = LocalContext.current
            val scope = rememberCoroutineScope()

            val termsAcceptedState = OnboardingPreferences.readTermsAccepted(context)
                .collectAsState(initial = null) // null = still loading

            CodeStrideTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    when (termsAcceptedState.value) {
                        null -> {
                            // Still loading → show nothing or a splash placeholder
                            // (prevents flash of ConsentScreen)
                        }
                        false -> {
                            ConsentScreen(
                                onAgree = {
                                    scope.launch {
                                        OnboardingPreferences.setTermsAccepted(context, true)
                                    }
                                },
                                onPrivacyClick = {
                                    val url = "https://codestride.vercel.app/privacy-policy"
                                    context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
                                },
                                onTermsClick = {
                                    val url = "https://codestride.vercel.app/terms-and-conditions"
                                    context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
                                }
                            )
                        }
                        true -> {
                            AppNavigator() // ✅ Launch normal app flow once accepted
                        }
                    }
                }
            }
        }
    }
}
