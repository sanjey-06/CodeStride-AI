package com.sanjey.codestride.ui.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.sanjey.codestride.R

@Composable
fun SplashScreen(navController: NavController) {
    var visibleText by remember { mutableStateOf("") }
    val fullText = "CodeStride"
    val pixelFont = FontFamily(Font(R.font.pixel_font))

    LaunchedEffect(Unit) {
        for (i in fullText.indices) {
            visibleText = fullText.substring(0, i + 1)
            delay(150)
        }
        delay(800)
        navController.navigate("onboarding") {
            popUpTo("splash") { inclusive = true }
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
                fontSize = 32.sp,
                fontFamily = pixelFont,
                color = Color.White
            )
        )
    }
}
