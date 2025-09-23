package com.sanjey.codestride.ui.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sanjey.codestride.ui.theme.PixelFont
import com.sanjey.codestride.viewmodel.UserViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    userViewModel: UserViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val splashTarget by userViewModel.splashNavigationState.observeAsState()

    var visibleText by remember { mutableStateOf("") }
    val fullText = "CodeStride AI"

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp

    val fontSize = when {
        screenWidth < 360 -> 18.sp
        screenWidth < 400 -> 24.sp
        else -> 32.sp
    }


    LaunchedEffect(Unit) {
        for (i in fullText.indices) {
            visibleText = fullText.substring(0, i + 1)
            delay(150)
        }

        userViewModel.handleSplashNavigation(context)
    }

    LaunchedEffect(splashTarget) {
        splashTarget?.let { target ->
            navController.navigate(target) {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = visibleText,
            style = TextStyle(
                fontFamily = PixelFont,
                fontSize = fontSize,
                color = Color.White
            )
        )
    }
}
