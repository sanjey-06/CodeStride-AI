package com.sanjey.codestride.ui.screens.quiz

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sanjey.codestride.R
import com.sanjey.codestride.ui.theme.PixelFont
import com.sanjey.codestride.ui.theme.SoraFont

@Composable
fun QuizScreen(navController: NavController) {
    val context = LocalContext.current
    var selectedOption by remember { mutableStateOf<String?>(null) }

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val bannerHeight = screenHeight * 0.15f
    val scrollState = rememberScrollState()

    val options = listOf(
        "A. Integrated Development Environment",
        "B. Internal Device Emulator",
        "C. Intelligent Debug Executor",
        "D. Interactive Deployment Engine"
    )

    Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // 🔼 Top Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(bannerHeight)
        ) {
            Image(
                painter = painterResource(id = R.drawable.quizscreen_background),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
            )
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 12.dp, top = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Text(
                text = "Quiz",
                fontFamily = PixelFont,
                fontSize = 28.sp,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // 🔽 Scrollable White Rounded Section
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                .background(Color.White)
                .verticalScroll(scrollState)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Introduction",
                    fontFamily = PixelFont,
                    fontSize = 20.sp,
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 🟦 Question Label
                Surface(
                    color = Color(0xFF03A9F4),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "Question 1 of 5",
                        fontFamily = PixelFont,
                        color = Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ❓ Question Text
                Text(
                    text = "What does IDE stand for?",
                    fontSize = 18.sp,
                    fontFamily = SoraFont,
                    color = Color.Black,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                // 🔘 Options
                options.forEach { option ->
                    val isSelected = selectedOption == option
                    val optionBg = if (isSelected) Color(0xFF03A9F4) else Color.White
                    val textColor = if (isSelected) Color.White else Color.Black

                    Surface(
                        color = optionBg,
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, Color.Gray),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable { selectedOption = option }
                    ) {
                        Text(
                            text = option,
                            modifier = Modifier.padding(16.dp),
                            fontFamily = SoraFont,
                            color = textColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ✅ Submit Button
                Button(
                    onClick = {
                        if (selectedOption == "A. Integrated Development Environment") {
                            Toast.makeText(context, "Correct!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Wrong Answer!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = selectedOption != null,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50),
                        disabledContainerColor = Color.LightGray
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Submit", fontFamily = PixelFont, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
