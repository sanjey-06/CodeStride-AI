package com.sanjey.codestride.ui.screens.main

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.*
import androidx.compose.foundation.layout.size
import com.sanjey.codestride.navigation.BottomNavItem
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import com.sanjey.codestride.ui.screens.home.HomeScreen
import com.sanjey.codestride.ui.theme.CustomBlue
import com.sanjey.codestride.ui.theme.SoraFont

@Composable
fun MainScreen() {
    Surface(
        modifier = Modifier,
        color = Color.Black // 🔥 sets the whole screen background
    ) {
        val navController = rememberNavController()
        val items = listOf(
            BottomNavItem.Home,
            BottomNavItem.Roadmap,
            BottomNavItem.Profile,
            BottomNavItem.Settings
        )

        Scaffold(
            bottomBar = {
                NavigationBar(modifier = Modifier.clip(RoundedCornerShape(50.dp)),
                    containerColor = Color.Black,
                    contentColor = Color.White
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
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
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.startDestinationId) {
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
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = BottomNavItem.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(BottomNavItem.Home.route) { HomeScreen() }
                composable(BottomNavItem.Roadmap.route) { TODO("RoadmapScreen is not implemented yet") }
                composable(BottomNavItem.Profile.route) { TODO("ProfileScreen is not implemented yet") }
                composable(BottomNavItem.Settings.route) { TODO("SettingsScreen is not implemented yet") }
            }
        }
    }
}
