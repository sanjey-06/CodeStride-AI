package com.sanjey.codestride.ui.screens.roadmap

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sanjey.codestride.R
import com.sanjey.codestride.ui.screens.home.ExploreOtherRoadmapsSection
import com.sanjey.codestride.ui.theme.CustomBlue
import com.sanjey.codestride.ui.theme.PixelFont
import com.sanjey.codestride.ui.theme.SoraFont
import com.sanjey.codestride.viewmodel.RoadmapViewModel

@Composable
fun RoadmapScreen(appNavController: NavController, roadmapViewModel: RoadmapViewModel) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val bannerHeight = screenHeight * 0.15f

    val currentRoadmapId by roadmapViewModel.currentRoadmapId.collectAsState()
    val currentModule by roadmapViewModel.currentModule.collectAsState()

    // ✅ Call this once to start observing roadmap & progress
    LaunchedEffect(Unit) {
        roadmapViewModel.observeCurrentRoadmap()
    }

    // ✅ Derive UI title & icon based on roadmapId
    val (currentTitle, currentIcon) = when (currentRoadmapId) {
        "java" -> "Java Programming" to R.drawable.ic_java
        "python" -> "Python Programming" to R.drawable.ic_python
        "cpp" -> "C++ Programming" to R.drawable.ic_cpp
        "kotlin" -> "Kotlin Programming" to R.drawable.ic_kotlin
        "js" -> "JavaScript Programming" to R.drawable.ic_javascript
        else -> "No Roadmap Selected" to R.drawable.ic_none
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .verticalScroll(rememberScrollState())
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

            Text(
                text = "RoadMap",
                fontFamily = PixelFont,
                fontSize = 28.sp,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // 🔷 Main White Container
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Current Learning",
                    fontFamily = SoraFont,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ✅ Dynamic Icon
                Surface(
                    modifier = Modifier.size(84.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.Black
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(id = currentIcon),
                            contentDescription = "Roadmap Icon",
                            modifier = Modifier.size(80.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ✅ Dynamic Title
                Text(
                    text = currentTitle,
                    fontFamily = PixelFont,
                    fontSize = 16.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                // ✅ Current Module
                Text(
                    text = "You left off at:",
                    fontFamily = SoraFont,
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Text(
                    text = currentModule,
                    fontFamily = PixelFont,
                    fontSize = 16.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        currentRoadmapId?.let {
                            appNavController.navigate("learning/$it")
                        }
                    },
                    enabled = currentRoadmapId != null,
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CustomBlue),
                ) {
                    Text(
                        text = "Continue Learning",
                        fontFamily = PixelFont,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 🔷 AI Generator Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "🚀 Want a custom learning path?",
                        fontFamily = PixelFont,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Let AI generate a roadmap just for you!",
                        fontFamily = SoraFont,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { /* Future AI logic */ },
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CustomBlue),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Generate with AI",
                            fontFamily = PixelFont,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                ExploreOtherRoadmapsSection(navController = appNavController)

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
