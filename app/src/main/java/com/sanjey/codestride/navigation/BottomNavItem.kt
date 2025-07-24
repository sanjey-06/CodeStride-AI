package com.sanjey.codestride.navigation

import androidx.annotation.DrawableRes
import com.sanjey.codestride.R
import com.sanjey.codestride.common.Constants.Routes

sealed class BottomNavItem(
    val route: String,
    @DrawableRes val iconRes: Int,
    val label: String
) {
    data object Home : BottomNavItem(Routes.HOME, R.drawable.ic_home, "Home")
    data object Roadmap : BottomNavItem(Routes.ROADMAP, R.drawable.ic_roadmap, "Roadmap")
    data object Profile : BottomNavItem(Routes.PROFILE, R.drawable.ic_profile, "Profile")
    data object Settings : BottomNavItem(Routes.SETTINGS, R.drawable.ic_settings, "Settings")
}
