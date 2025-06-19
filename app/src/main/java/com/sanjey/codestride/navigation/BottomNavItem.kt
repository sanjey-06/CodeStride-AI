package com.sanjey.codestride.navigation

import androidx.annotation.DrawableRes
import com.sanjey.codestride.R

sealed class BottomNavItem(
    val route: String,
    @DrawableRes val iconRes: Int,
    val label: String
) {
    object Home : BottomNavItem("home", R.drawable.ic_home, "Home")
    object Roadmap : BottomNavItem("roadmap", R.drawable.ic_roadmap, "Roadmap")
    object Profile : BottomNavItem("profile", R.drawable.ic_profile, "Profile")
    object Settings : BottomNavItem("settings", R.drawable.ic_settings, "Settings")
}
