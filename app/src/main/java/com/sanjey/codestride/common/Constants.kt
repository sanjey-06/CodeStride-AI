package com.sanjey.codestride.common

import com.sanjey.codestride.BuildConfig

object Constants {

    const val YOUTUBE_API_KEY = BuildConfig.YOUTUBE_API_KEY



    // ✅ Navigation Routes
    object Routes {
        const val SPLASH = "splash"
        const val ONBOARDING = "onboarding"
        const val LOGIN = "login"
        const val SIGNUP = "signup"
        const val FORGOT_PASSWORD = "forgot_password"
        const val HOME = "home"
        const val ROADMAP = "roadmap"
        const val PROFILE = "profile"
        const val SETTINGS = "settings"
        const val CHATBOT = "chatbot"
        const val QUIZ_SCREEN = "quiz_screen"
        const val LEARNING = "learning"
        const val LEARNING_CONTENT = "learning_content"
        const val EXPLORE_ROADMAPS = "explore_roadmaps"
        const val EXPLORE_CAREER = "explore_career"
    }

    // ✅ Firestore Paths
    object FirestorePaths {
        const val USERS = "users"
        const val ROADMAPS = "roadmaps"
        const val MODULES = "modules"
        const val QUIZZES = "quizzes"
        const val QUESTIONS = "questions"
        const val PROGRESS = "progress"
        const val QUOTES = "quotes"
    }




}
