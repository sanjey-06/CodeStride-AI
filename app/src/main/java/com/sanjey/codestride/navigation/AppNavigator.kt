package com.sanjey.codestride.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sanjey.codestride.common.Constants.Routes
import com.sanjey.codestride.ui.screens.auth.*
import com.sanjey.codestride.ui.screens.chatbot.ChatbotScreen
import com.sanjey.codestride.ui.screens.home.HomeScreen
import com.sanjey.codestride.ui.screens.main.MainScreen
import com.sanjey.codestride.ui.screens.onboarding.OnboardingScreen
import com.sanjey.codestride.ui.screens.profile.ProfileScreen
import com.sanjey.codestride.ui.screens.quiz.QuizScreen
import com.sanjey.codestride.ui.screens.roadmap.ExploreCareerScreen
import com.sanjey.codestride.ui.screens.roadmap.ExploreRoadmapsScreen
import com.sanjey.codestride.ui.screens.roadmap.LearningContentScreen
import com.sanjey.codestride.ui.screens.roadmap.LearningScreen
import com.sanjey.codestride.ui.screens.roadmap.RoadmapScreen
import com.sanjey.codestride.ui.screens.settings.SettingsScreen
import com.sanjey.codestride.ui.screens.splash.SplashScreen
import com.sanjey.codestride.viewmodel.RoadmapViewModel

@Composable
fun AppNavigator() {
    val navController = rememberNavController()
    val roadmapViewModel: RoadmapViewModel = hiltViewModel()

    LaunchedEffect(Unit) {
        roadmapViewModel.observeCurrentRoadmap()
    }

    NavHost(navController = navController, startDestination = Routes.SPLASH) {
        composable(Routes.SPLASH) { SplashScreen(navController) }
        composable(Routes.ONBOARDING) { OnboardingScreen(navController) }
        composable(Routes.LOGIN) { LoginScreen(navController) }
        composable(Routes.SIGNUP) { SignupScreen(navController) }
        composable(Routes.FORGOT_PASSWORD) { ForgotPasswordScreen(navController) }

        composable(Routes.HOME) {
            MainScreen(navController = navController, currentRoute = Routes.HOME) {
                HomeScreen(navController = navController, roadmapViewModel = roadmapViewModel)
            }
        }


        composable(Routes.ROADMAP) {
            MainScreen(navController = navController, currentRoute = Routes.ROADMAP) {
                RoadmapScreen(
                    appNavController = navController,
                    roadmapViewModel = roadmapViewModel,
                    homeViewModel = hiltViewModel()
                )
            }
        }

        composable(Routes.PROFILE) {
            MainScreen(navController = navController, currentRoute = Routes.PROFILE) {
                ProfileScreen(navController)
            }
        }

        composable(Routes.SETTINGS) {
            MainScreen(navController = navController, currentRoute = Routes.SETTINGS) {
                SettingsScreen(navController)
            }
        }

        composable("${Routes.LEARNING}/{roadmapId}") { backStackEntry ->
            val roadmapId = backStackEntry.arguments?.getString("roadmapId") ?: ""
            LearningScreen(
                roadmapId = roadmapId,
                navController = navController,
                roadmapViewModel = roadmapViewModel
            )
        }

        composable(Routes.CHATBOT) { ChatbotScreen(navController) }

        composable("${Routes.QUIZ_SCREEN}/{roadmapId}/{moduleId}/{quizId}") { backStackEntry ->
            val roadmapId = backStackEntry.arguments?.getString("roadmapId") ?: ""
            val moduleId = backStackEntry.arguments?.getString("moduleId") ?: ""
            val quizId = backStackEntry.arguments?.getString("quizId") ?: ""
            QuizScreen(
                navController = navController,
                roadmapId = roadmapId,
                moduleId = moduleId,
                quizId = quizId,
                roadmapViewModel = roadmapViewModel,
            )
        }

        composable(Routes.EXPLORE_ROADMAPS) { ExploreRoadmapsScreen(navController) }
        composable(Routes.EXPLORE_CAREER) { ExploreCareerScreen(navController) }

        composable("${Routes.LEARNING_CONTENT}/{roadmapId}/{moduleId}") { backStackEntry ->
            val roadmapId = backStackEntry.arguments?.getString("roadmapId") ?: ""
            val moduleId = backStackEntry.arguments?.getString("moduleId") ?: ""
            LearningContentScreen(
                navController = navController,
                roadmapId = roadmapId,
                moduleId = moduleId
            )
        }
    }
}
