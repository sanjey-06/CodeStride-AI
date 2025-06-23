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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sanjey.codestride.R
import com.sanjey.codestride.ui.screens.home.ExploreOtherRoadmapsSection
import com.sanjey.codestride.ui.theme.CustomBlue
import com.sanjey.codestride.ui.theme.PixelFont
import com.sanjey.codestride.ui.theme.SoraFont

@Composable
fun RoadmapScreen(navController: NavController) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val bannerHeight = screenHeight * 0.15f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .verticalScroll(rememberScrollState())
    ) {
        // 🔷 Top Banner with Image + Heading
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

                Surface(
                    modifier = Modifier.size(84.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.Black
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_python),
                            contentDescription = "Python Icon",
                            modifier = Modifier.size(80.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Python Programming",
                    fontFamily = PixelFont,
                    fontSize = 16.sp,
                    color = Color.Black
                )

                Text(
                    text = "🔥 You’re on a 3-day streak!",
                    fontFamily = SoraFont,
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "You left off at :",
                    fontFamily = SoraFont,
                    fontSize = 14.sp,
                    color = Color.Black
                )

                Text(
                    text = "Variables",
                    fontFamily = PixelFont,
                    fontSize = 16.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { /* TODO */ },
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CustomBlue),
//                    modifier = Modifier.fillMaxWidth()
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
                        onClick = { /* TODO */ },
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

                // 🔷 Ask CodeBot Section
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Need any help?",
                        fontFamily = SoraFont,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    Button(
                        onClick = { /*TODO */ },
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Ask CodeBot",
                            fontFamily = PixelFont,
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 🔷 Explore Other Roadmaps (reused)
                ExploreOtherRoadmapsSection()

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
