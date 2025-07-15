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
import com.sanjey.codestride.ui.screens.chatbot.ChatbotScreen
import com.sanjey.codestride.ui.screens.home.HomeScreen
import com.sanjey.codestride.ui.screens.main.MainScreen
import com.sanjey.codestride.ui.screens.roadmap.LearningScreen
import com.sanjey.codestride.ui.screens.roadmap.RoadmapScreen
import com.sanjey.codestride.ui.screens.quiz.QuizScreen
import com.sanjey.codestride.ui.screens.profile.ProfileScreen
import com.sanjey.codestride.ui.screens.roadmap.ExploreCareerScreen
import com.sanjey.codestride.ui.screens.roadmap.ExploreRoadmapsScreen
import com.sanjey.codestride.ui.screens.roadmap.LearningContentScreen
import com.sanjey.codestride.ui.screens.settings.SettingsScreen


@Composable
fun AppNavigator() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("onboarding") { OnboardingScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("signup") { SignupScreen(navController) }
        composable("forgot_password") { ForgotPasswordScreen(navController) }

        // ✅ Now here’s your main tab routes
        composable("home") {
            MainScreen(navController = navController, currentRoute = "home") {
                HomeScreen(navController)
            }
        }

        composable("roadmap") {
            MainScreen(navController = navController, currentRoute = "roadmap") {
                RoadmapScreen(appNavController = navController)
            }
        }
        composable("profile") {
            MainScreen(navController = navController, currentRoute = "profile") {
                ProfileScreen(navController)
            }
        }

        composable("settings") { MainScreen(navController = navController, currentRoute = "settings") {
            SettingsScreen(navController)
        } }

        composable("learning/{roadmapId}") { backStackEntry ->
            val roadmapId = backStackEntry.arguments?.getString("roadmapId") ?: ""
            LearningScreen(roadmapId = roadmapId,
                navController = navController)
        }
        composable("chatbot") { ChatbotScreen(navController) }

        composable("quiz_screen/{roadmapId}/{moduleId}/{quizId}") { backStackEntry ->
            val roadmapId = backStackEntry.arguments?.getString("roadmapId") ?: ""
            val moduleId = backStackEntry.arguments?.getString("moduleId") ?: ""
            val quizId = backStackEntry.arguments?.getString("quizId") ?: ""
            QuizScreen(navController = navController, roadmapId = roadmapId, moduleId = moduleId, quizId = quizId)
        }

        // ✅ QuizResultScreen Route (for score)
//        composable("quiz_result/{score}/{total}") { backStackEntry ->
//            val score = backStackEntry.arguments?.getString("score")?.toInt() ?: 0
//            val total = backStackEntry.arguments?.getString("total")?.toInt() ?: 0
//            QuizResultScreen(score = score, total = total, navController = navController)
//        }
        composable("explore_roadmaps") {
            ExploreRoadmapsScreen(navController)
        }
        composable("explore_career"){
            ExploreCareerScreen(navController)
        }
        composable("learning_content/{roadmapId}/{moduleId}") { backStackEntry ->
            val roadmapId = backStackEntry.arguments?.getString("roadmapId") ?: ""
            val moduleId = backStackEntry.arguments?.getString("moduleId") ?: ""
            LearningContentScreen(navController = navController, roadmapId = roadmapId, moduleId = moduleId)
        }






    }
}
