package com.sanjey.codestride.ui.screens.roadmap

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sanjey.codestride.ui.theme.PixelFont
import com.sanjey.codestride.ui.theme.SoraFont

@Composable
fun LearningContentScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Introduction to Variables in Python",
            fontFamily = PixelFont,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = """
                🔹 What is a Variable?

                Variables are containers for storing data values. In Python, you don't need to declare the type of variable. It figures it out on its own.

                🧪 Example:
                -------------------
                x = 5
                name = "John"
                isActive = True
                -------------------

                🌀 Python is dynamically typed:
                x = "Hello"  # Now x is a string

                ✅ Tip: Use meaningful variable names like `userName` instead of `x`.

                We'll explore how data types work next. For now, try creating a few variables on your own!
            """.trimIndent(),
            fontFamily = SoraFont,
            fontSize = 16.sp,
            color = Color.DarkGray
        )
    }
}
