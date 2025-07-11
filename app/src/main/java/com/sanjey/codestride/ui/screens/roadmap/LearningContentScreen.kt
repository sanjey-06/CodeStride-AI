package com.sanjey.codestride.ui.screens.roadmap

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sanjey.codestride.R
import com.sanjey.codestride.ui.theme.PixelFont
import com.sanjey.codestride.ui.theme.SoraFont
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack

@Composable
fun LearningContentScreen(navController: NavController) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val bannerHeight = screenHeight * 0.15f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .verticalScroll(rememberScrollState())
    ) {
        // 🔷 Top Banner with Image + Title and Back Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(bannerHeight)
        ) {
            Image(
                painter = painterResource(id = R.drawable.homescreen_background),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
            )

            // 🔙 Back Button
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Text(
                text = "Module Content",
                fontFamily = PixelFont,
                fontSize = 20.sp,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // 🔷 Main White Rounded Container
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Introduction to Variables in Python",
                    fontFamily = PixelFont,
                    fontSize = 20.sp,
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
    }
}
