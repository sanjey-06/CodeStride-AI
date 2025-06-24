package com.sanjey.codestride.ui.screens.main

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.sanjey.codestride.navigation.BottomNavItem
import com.sanjey.codestride.ui.screens.home.HomeScreen
import com.sanjey.codestride.ui.screens.roadmap.RoadmapScreen
import com.sanjey.codestride.ui.theme.CustomBlue
import com.sanjey.codestride.ui.theme.SoraFont

@Composable
fun MainScreen(navController: NavHostController) { // 👈 This is the global app navController
    val bottomNavController = rememberNavController()

    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Roadmap,
        BottomNavItem.Profile,
        BottomNavItem.Settings
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier.clip(RoundedCornerShape(50.dp)),
                containerColor = Color.Black,
                contentColor = Color.White
            ) {
                val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painter = painterResource(id = item.iconRes),
                                contentDescription = item.label,
                                modifier = Modifier.size(30.dp),
                                tint = Color.Unspecified
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                fontFamily = SoraFont,
                                fontSize = 11.sp
                            )
                        },
                        selected = currentRoute == item.route,
                        onClick = {
                            if (currentRoute != item.route) {
                                bottomNavController.navigate(item.route) {
                                    popUpTo(bottomNavController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CustomBlue,
                            selectedTextColor = CustomBlue,
                            indicatorColor = Color.Black,
                            unselectedIconColor = Color.White,
                            unselectedTextColor = Color.White
                        )
                    )
                }
            }
        },
        containerColor = Color.Black
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) {
                HomeScreen(navController = navController) // ✅ global controller passed
            }
            composable(BottomNavItem.Roadmap.route) {
                RoadmapScreen(appNavController = navController) // ✅ global controller passed
            }
            composable(BottomNavItem.Profile.route) {
                // TODO
            }
            composable(BottomNavItem.Settings.route) {
                // TODO
            }
        }
    }
}
