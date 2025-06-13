package com.sanjey.codestride.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sanjey.codestride.ui.screens.splash.SplashScreen
import androidx.compose.material3.Text
import com.sanjey.codestride.ui.screens.onboarding.OnboardingScreen

@Composable
fun AppNavigator() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(navController)
        }
        composable("onboarding") {
            OnboardingScreen(navController)
        }

        // ADD NEXT SCREEN:
        composable("login") {
            Text("Login Screen Placeholder") // replace with actual LoginScreen later
        }
    }
}
