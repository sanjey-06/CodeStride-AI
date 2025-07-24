package com.sanjey.codestride.ui.screens.home

import androidx.compose.foundation.Image
import com.sanjey.codestride.common.FireProgressBar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.sanjey.codestride.R
import com.sanjey.codestride.ui.theme.CustomBlue
import com.sanjey.codestride.ui.theme.PixelFont
import com.sanjey.codestride.ui.theme.SoraFont
import com.sanjey.codestride.viewmodel.HomeViewModel

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = hiltViewModel()) {
    val firstName by viewModel.firstName.observeAsState()
    val userStats by viewModel.userStats.observeAsState()
    val currentRoadmap by viewModel.currentRoadmap.observeAsState()
    val badges by viewModel.badges.observeAsState()
    val exploreRoadmaps by viewModel.exploreRoadmaps.observeAsState()

    val scrollState = rememberScrollState()
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val imageHeight = screenHeight * 0.75f

    val quoteOfTheDay by viewModel.quoteOfTheDay.observeAsState()


    LaunchedEffect(Unit) {
        viewModel.refreshUserStats()
        viewModel.loadQuoteOfTheDay(FirebaseAuth.getInstance().currentUser?.uid)

    }


    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
            .background(Color.Black)
            .wrapContentHeight()
    ) {
        // Top 75% black background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(imageHeight)
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
                    .background(Color.Black.copy(alpha = 0.8f))
            )

            if (firstName != null && quoteOfTheDay != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Hello $firstName 👋",
                        fontFamily = PixelFont,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "“${quoteOfTheDay!!.text}”",
                        fontFamily = SoraFont,
                        fontSize = 14.sp,
                        color = Color.White,
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .padding(horizontal = 32.dp)
                            .fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "- ${quoteOfTheDay!!.author}",
                        fontFamily = SoraFont,
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // ✅ Fixed white section with no extra scroll
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Text(
                text = "🔥 Current Streak",
                fontFamily = PixelFont,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "${userStats?.streak ?: 0} Days",
                fontFamily = PixelFont,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            FireProgressBar(
                progress = userStats?.progressPercent ?: 0f,
                isOnFire = true
            )


            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = userStats?.nextBadgeMsg ?: "",
                fontFamily = SoraFont,
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {  navController.navigate("roadmap") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CustomBlue)
            ) {
                Text(
                    text = "Go to your Roadmap",
                    fontFamily = PixelFont,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            currentRoadmap?.let {
                RoadmapCard(
                    iconResId = it.iconResId,
                    title = it.title,
                    progressPercent = it.progressPercent
                )
            }


            Spacer(modifier = Modifier.height(28.dp))

            BadgePreviewSection(badges)
            ExploreOtherRoadmapsSection(navController, exploreRoadmaps)
        }
    }
}

@Composable
fun RoadmapCard(
    iconResId: Int,
    title: String,
    progressPercent: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.35f)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = iconResId),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(72.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.65f)
                    .background(CustomBlue)
                    .padding(start = 12.dp, top = 16.dp, end = 12.dp, bottom = 20.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Your current Learning",
                    fontFamily = SoraFont,
                    fontSize = 10.sp,
                    color = Color.White,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = title,
                    fontFamily = PixelFont,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 8.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                LinearProgressIndicator(
                    progress = { progressPercent / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color.Black,
                    trackColor = Color.White
                )

                Text(
                    text = "$progressPercent % completed",
                    fontFamily = PixelFont,
                    fontSize = 12.sp,
                    color = Color.White,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun BadgePreviewSection(badges: List<Triple<String, Int, Boolean>>?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text(
            text = "Your Achievements",
            fontFamily = PixelFont,
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            badges?.forEach { (_, imageRes, unlocked) ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(100.dp)
                ) {
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(top = 6.dp)
                    ) {
                        Text(
                            text = if (unlocked) "Unlocked" else "Locked",
                            fontFamily = SoraFont,
                            fontSize = 12.sp,
                            color = if (unlocked) Color(0xFFB4FF63) else Color.Gray
                        )
                        if (!unlocked) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(14.dp).padding(start = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun ExploreOtherRoadmapsSection(navController: NavController, roadmaps: List<Pair<Int, String>>?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Explore Roadmaps",
                fontFamily = PixelFont,
                fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                tint = Color.Black,
                contentDescription = "Go to Explore Roadmaps",
                modifier = Modifier
                    .size(20.dp)
                    .clickable { navController.navigate("explore_roadmaps") }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally)
        ) {
            roadmaps?.forEach { (icon, label) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        modifier = Modifier.size(84.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.Black
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Image(
                                painter = painterResource(id = icon),
                                contentDescription = label,
                                modifier = Modifier.size(80.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = label,
                        fontFamily = PixelFont,
                        fontSize = 12.sp,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

