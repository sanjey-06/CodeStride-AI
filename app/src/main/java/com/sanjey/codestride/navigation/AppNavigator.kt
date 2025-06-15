package com.sanjey.codestride.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sanjey.codestride.ui.screens.auth.ForgotPasswordScreen
import com.sanjey.codestride.ui.screens.splash.SplashScreen
import com.sanjey.codestride.ui.screens.onboarding.OnboardingScreen
import com.sanjey.codestride.ui.screens.auth.LoginScreen
import com.sanjey.codestride.ui.screens.auth.SignupScreen
import com.sanjey.codestride.ui.screens.home.HomeScreen

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
        composable("login") {
            LoginScreen(navController = navController)

        }
        composable("signup") {
            SignupScreen(navController)
        }
        composable("forgot_password") {
            ForgotPasswordScreen(navController)
        }
        composable("home") {
            HomeScreen()
        }



    }
}
