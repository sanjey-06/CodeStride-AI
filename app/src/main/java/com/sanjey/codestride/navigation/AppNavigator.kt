package com.sanjey.codestride.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.sanjey.codestride.ui.screens.auth.*
import com.sanjey.codestride.ui.screens.splash.SplashScreen
import com.sanjey.codestride.ui.screens.onboarding.OnboardingScreen
import com.sanjey.codestride.ui.screens.chatbot.ChatbotScreen
import com.sanjey.codestride.ui.screens.home.HomeScreen
import com.sanjey.codestride.ui.screens.main.MainScreen
import com.sanjey.codestride.ui.screens.roadmap.*
import com.sanjey.codestride.ui.screens.quiz.QuizScreen
import com.sanjey.codestride.ui.screens.profile.ProfileScreen
import com.sanjey.codestride.ui.screens.settings.SettingsScreen
import com.sanjey.codestride.viewmodel.RoadmapViewModel

@Composable
fun AppNavigator() {
    val navController = rememberNavController()

    // ✅ Single instance shared across screens
    val roadmapViewModel: RoadmapViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("onboarding") { OnboardingScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("signup") { SignupScreen(navController) }
        composable("forgot_password") { ForgotPasswordScreen(navController) }

        composable("home") {
            MainScreen(navController = navController, currentRoute = "home") {
                HomeScreen(navController)
            }
        }

        composable("roadmap") {
            MainScreen(navController = navController, currentRoute = "roadmap") {
                RoadmapScreen(appNavController = navController, roadmapViewModel = roadmapViewModel)
            }
        }

        composable("profile") {
            MainScreen(navController = navController, currentRoute = "profile") {
                ProfileScreen(navController)
            }
        }

        composable("settings") {
            MainScreen(navController = navController, currentRoute = "settings") {
                SettingsScreen(navController)
            }
        }

        composable("learning/{roadmapId}") { backStackEntry ->
            val roadmapId = backStackEntry.arguments?.getString("roadmapId") ?: ""
            LearningScreen(
                roadmapId = roadmapId,
                navController = navController,
                roadmapViewModel = roadmapViewModel
            )
        }

        composable("chatbot") { ChatbotScreen(navController) }

        composable("quiz_screen/{roadmapId}/{moduleId}/{quizId}") { backStackEntry ->
            val roadmapId = backStackEntry.arguments?.getString("roadmapId") ?: ""
            val moduleId = backStackEntry.arguments?.getString("moduleId") ?: ""
            val quizId = backStackEntry.arguments?.getString("quizId") ?: ""
            QuizScreen(
                navController = navController,
                roadmapId = roadmapId,
                moduleId = moduleId,
                quizId = quizId,
                roadmapViewModel = roadmapViewModel
            )
        }

        composable("explore_roadmaps") { ExploreRoadmapsScreen(navController) }

        composable("explore_career") { ExploreCareerScreen(navController) }

        composable("learning_content/{roadmapId}/{moduleId}") { backStackEntry ->
            val roadmapId = backStackEntry.arguments?.getString("roadmapId") ?: ""
            val moduleId = backStackEntry.arguments?.getString("moduleId") ?: ""
            LearningContentScreen(navController = navController, roadmapId = roadmapId, moduleId = moduleId)
        }
    }
}
