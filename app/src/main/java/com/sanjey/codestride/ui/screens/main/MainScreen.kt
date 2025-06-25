package com.sanjey.codestride.ui.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import com.sanjey.codestride.navigation.BottomNavItem
import com.sanjey.codestride.ui.screens.home.HomeScreen
import com.sanjey.codestride.ui.screens.roadmap.RoadmapScreen
import com.sanjey.codestride.ui.theme.CustomBlue
import com.sanjey.codestride.ui.theme.SoraFont

@Composable
fun MainScreen(navController: NavHostController) {
    val bottomNavController = rememberNavController()

    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Roadmap,
        BottomNavItem.Profile,
        BottomNavItem.Settings
    )

    Box(modifier = Modifier.fillMaxSize()) {

        // 🧠 Main content with padding to avoid overlap
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp)) { // 👈 enough to avoid overlay

            NavHost(
                navController = bottomNavController,
                startDestination = BottomNavItem.Home.route,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(BottomNavItem.Home.route) {
                    HomeScreen(navController = bottomNavController)
                }
                composable(BottomNavItem.Roadmap.route) {
                    RoadmapScreen(appNavController = navController)
                }
                composable(BottomNavItem.Profile.route) { /* TODO */ }
                composable(BottomNavItem.Settings.route) { /* TODO */ }
            }
        }

        // 🎯 Floating Bottom Navigation
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50.dp)),
                color = Color.Black,
                tonalElevation = 0.dp
            ) {
                NavigationBar(
                    containerColor = Color.Transparent,
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
                                indicatorColor = Color.Transparent,
                                unselectedIconColor = Color.White,
                                unselectedTextColor = Color.White
                            )
                        )
                    }
                }
            }
        }
    }
}
