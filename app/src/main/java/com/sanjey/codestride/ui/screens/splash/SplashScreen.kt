package com.sanjey.codestride.ui.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sanjey.codestride.data.prefs.OnboardingPreferences
import com.sanjey.codestride.ui.theme.PixelFont
import com.sanjey.codestride.viewmodel.UserViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    userViewModel: UserViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val hasSeenOnboarding by OnboardingPreferences.readOnboardingSeen(context)
        .collectAsState(initial = false)

    var visibleText by remember { mutableStateOf("") }
    val fullText = "CodeStride"

    // Observe login state from ViewModel
    val isUserLoggedIn by userViewModel.isUserLoggedIn.observeAsState()

    LaunchedEffect(Unit) {
        // Start text animation
        for (i in fullText.indices) {
            visibleText = fullText.substring(0, i + 1)
            delay(150)
        }

        // Check login state after splash animation
        userViewModel.checkUserLogin()
    }

    // React to login state changes
    LaunchedEffect(isUserLoggedIn) {
        if (isUserLoggedIn != null) {
            delay(800) // Short delay after text animation
            when {
                isUserLoggedIn == true -> {
                    // ✅ Already logged in → Go to Main/Home
                    navController.navigate("home") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
                else -> {
                    // ✅ Not logged in → Check onboarding
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
            }
        }
    }

    // UI for Splash Screen
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
