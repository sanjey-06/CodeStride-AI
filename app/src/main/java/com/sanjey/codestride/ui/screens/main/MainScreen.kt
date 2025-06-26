package com.sanjey.codestride.ui.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sanjey.codestride.navigation.BottomNavItem
import com.sanjey.codestride.ui.theme.CustomBlue
import com.sanjey.codestride.ui.theme.SoraFont

@Composable
fun MainScreen(
    navController: NavHostController,
    currentRoute: String,
    content: @Composable () -> Unit
) {
    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Roadmap,
        BottomNavItem.Profile,
        BottomNavItem.Settings
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Main content area (screen content like Home, Roadmap, etc.)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp) // Leave space for nav bar
        ) {
            content()
        }

        // Bottom Navigation Bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50.dp)),
                color = Color.Black
            ) {
                NavigationBar(
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                ) {
                    bottomNavItems.forEach { item ->
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
                                        popUpTo("home") { inclusive = false }
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
