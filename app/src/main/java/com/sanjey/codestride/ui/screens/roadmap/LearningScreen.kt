package com.sanjey.codestride.ui.screens.roadmap

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.sanjey.codestride.data.model.Module
import com.sanjey.codestride.ui.theme.CustomBlue
import com.sanjey.codestride.ui.theme.PixelFont
import com.sanjey.codestride.ui.theme.SoraFont

@Composable
fun LearningScreen(roadmapId: String, navController: NavController) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val bannerHeight = screenHeight * 0.15f
    val scrollState = rememberScrollState()

    val modules = listOf(
        Module("1", "Introduction", 1),
        Module("2", "Basics", 2),
        Module("3", "Variables", 3),
        Module("4", "Data Types", 4),
        Module("5", "Loops", 5),
        Module("6", "Functions", 6),
        Module("7", "Conditionals", 7),
        Module("8", "Arrays", 8),
        Module("9", "OOP", 9),
        Module("10", "Projects", 10)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // 🔷 Top Banner
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
                text = "Python",
                fontFamily = PixelFont,
                fontSize = 28.sp,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // 🔽 Scrollable Area
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                .background(Color.White)
                .verticalScroll(scrollState)
        ) {
            Box {
                Image(
                    painter = painterResource(id = R.drawable.roadmap_bg),
                    contentDescription = "Roadmap Background",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1080f / 3500f),
                    contentScale = ContentScale.Fit
                )

                modules.forEachIndexed { index, module ->
                    val isLeft = index % 2 == 0
                    val verticalOffset = when (index) {
                        0 -> 25.dp
                        1 -> 125.dp
                        2 -> 215.dp
                        3 -> 308.dp
                        4 -> 400.dp
                        5 -> 515.dp
                        6 -> 645.dp
                        7 -> 737.dp
                        8 -> 830.dp
                        9 -> 920.dp
                        else -> 1000.dp
                    }

                    val backgroundColor = when (index) {
                        0, 1 -> Color(0xFF4CAF50) // Green
                        2 -> CustomBlue            // Theme Blue
                        else -> Color(0xFFBDBDBD)  // Gray for locked
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = verticalOffset)
                            .padding(horizontal = 24.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = backgroundColor,
                            modifier = Modifier
                                .align(if (isLeft) Alignment.CenterStart else Alignment.CenterEnd)
                                .width(180.dp)
                                .height(48.dp)
                        ) {
                            Text(
                                text = "${module.order}. ${module.title}",
                                color = Color.White,
                                fontFamily = SoraFont,
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .padding(12.dp)
                                    .wrapContentHeight(Alignment.CenterVertically)
                            )
                        }
                    }
                }

                // 🎓 Final certificate button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = 1150.dp)
                        .padding(horizontal = 32.dp)
                ) {
                    Button(
                        onClick = { /* TODO */ },
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxWidth(0.85f)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBDBDBD))
                    ) {
                        Text(
                            text = " Get Certificate",
                            fontSize = 14.sp,
                            fontFamily = PixelFont,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}





