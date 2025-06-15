package com.sanjey.codestride.ui.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sanjey.codestride.data.prefs.OnboardingPreferences
import kotlinx.coroutines.delay
import com.sanjey.codestride.ui.theme.PixelFont


@Composable
fun SplashScreen(navController: NavController) {
    val context = LocalContext.current
    val hasSeenOnboarding by OnboardingPreferences.readOnboardingSeen(context).collectAsState(initial = false)
    var visibleText by remember { mutableStateOf("") }
    val fullText = "CodeStride"

    LaunchedEffect(hasSeenOnboarding) {
        for (i in fullText.indices) {
            visibleText = fullText.substring(0, i + 1)
            delay(150)
        }
        delay(800)
        if (hasSeenOnboarding) {
            navController.navigate("login") {
                popUpTo("splash") { inclusive = true }
            }
        } else {
            navController.navigate("onboarding") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = visibleText,
            style = TextStyle(
                fontFamily = PixelFont,
                fontSize = 32.sp,
                color = Color.White
            )
        )

    }
}
