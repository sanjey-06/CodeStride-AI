package com.sanjey.codestride.ui.screens.home

import android.util.Log
import androidx.compose.foundation.Image
import com.sanjey.codestride.common.FireProgressBar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.sanjey.codestride.R
import com.sanjey.codestride.common.UiState
import com.sanjey.codestride.common.getIconResource
import com.sanjey.codestride.data.model.Badge
import com.sanjey.codestride.ui.theme.CustomBlue
import com.sanjey.codestride.ui.theme.PixelFont
import com.sanjey.codestride.ui.theme.SoraFont
import com.sanjey.codestride.viewmodel.HomeViewModel
import com.sanjey.codestride.data.model.HomeScreenData
import com.sanjey.codestride.data.model.Roadmap
import com.sanjey.codestride.ui.components.BadgeInfoCard
import com.sanjey.codestride.ui.components.RoadmapReplaceDialog
import com.sanjey.codestride.viewmodel.RoadmapViewModel

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = hiltViewModel(), roadmapViewModel: RoadmapViewModel) {
    val homeState by viewModel.homeUiState.collectAsState()
    val scrollState = rememberScrollState()
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val imageHeight = screenHeight * 0.75f

    val currentRoadmapId by roadmapViewModel.currentRoadmapId.collectAsState()
    Log.d("HOME_DEBUG", "HomeScreen currentRoadmapId=$currentRoadmapId")

    LaunchedEffect(homeState) {
        Log.d("HOME_UI_DEBUG", "HomeScreen recomposed → homeState=$homeState, currentRoadmapId=$currentRoadmapId")
    }
    var showDialog by remember { mutableStateOf(false) }
    var newRoadmapId by remember { mutableStateOf<String?>(null) }



    when (homeState) {
        is UiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CustomBlue)
            }
        }

        is UiState.Success -> {
            val data = (homeState as UiState.Success<HomeScreenData>).data

            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .background(Color.Black)
                    .wrapContentHeight()
            ) {
                // ✅ Top section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 200.dp, max = imageHeight)
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

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Hello ${data.firstName} 👋",
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
                            text = "“${data.quote.text}”",
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
                            text = "- ${data.quote.author}",
                            fontFamily = SoraFont,
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // ✅ Bottom white section
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
                        text = "${data.userStats.streak} Days",
                        fontFamily = PixelFont,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    FireProgressBar(
                        progress = data.userStats.progressPercent,
                        isOnFire = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = data.userStats.nextBadgeMsg,
                        fontFamily = SoraFont,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { navController.navigate("roadmap") },
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

                    Spacer(modifier = Modifier.height(16.dp))
                    val (title, iconResId) = roadmapViewModel.getRoadmapTitleAndIcon(currentRoadmapId)

                    RoadmapCard(
                        iconResId = iconResId,
                        title = title,
                        progressPercent = data.currentRoadmap.progressPercent
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    BadgePreviewSection(data.badges)


                    ExploreOtherRoadmapsSection(
                        navController = navController,
                        roadmaps = data.exploreRoadmaps,
                        onRoadmapClick = { roadmapId ->
                            if (roadmapViewModel.hasActiveRoadmap()) {
                                newRoadmapId = roadmapId
                                showDialog = true
                            } else {
                                roadmapViewModel.startRoadmap(roadmapId)
                                navController.navigate("learning/$roadmapId")
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(64.dp))


                    RoadmapReplaceDialog(
                        showDialog = showDialog && newRoadmapId != null,
                        onDismiss = { showDialog = false },
                        onConfirm = {
                            roadmapViewModel.replaceRoadmap(newRoadmapId!!)
                            showDialog = false
                            navController.navigate("learning/${newRoadmapId!!}") {
                                popUpTo("roadmap") { inclusive = false }
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }

        is UiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_failed),
                        contentDescription = "Error Icon",
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Something went wrong",
                        color = Color.Red,
                        fontFamily = PixelFont
                    )
                }
            }
        }


        UiState.Idle -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Welcome!", color = Color.Gray, fontFamily = PixelFont)
            }
        }

        UiState.Empty -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No data available", color = Color.Gray, fontFamily = PixelFont)
            }
        }
    }
}

@Composable
fun RoadmapCard(iconResId: Int, title: String, progressPercent: Int, modifier: Modifier = Modifier) {
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
                .heightIn(min = 120.dp, max = 160.dp)
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
fun BadgePreviewSection(badges: List<Badge>) {
    var selectedBadge by remember { mutableStateOf<Badge?>(null) }

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

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (badges.isEmpty()) {
                item {
                    Text(
                        text = "No achievements yet",
                        fontFamily = SoraFont,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 20.dp)
                    )
                }
            } else {
                items(badges) { badge ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(80.dp)
                            .clickable { selectedBadge = badge }
                    ) {
                        AsyncImage(
                            model = badge.image,
                            contentDescription = null,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(top = 6.dp)
                        ) {
                            Text(
                                text = "Unlocked",
                                fontFamily = SoraFont,
                                fontSize = 12.sp,
                                color = Color(0xFFB4FF63)
                            )
                        }
                    }
                }
            }
        }
    }

    selectedBadge?.let {
        Dialog(onDismissRequest = { selectedBadge = null }) {
            BadgeInfoCard(
                badge = it,
                onClose = { selectedBadge = null }
            )
        }
    }
}




@Composable
fun ExploreOtherRoadmapsSection(navController: NavController, roadmaps: List<Roadmap>, onRoadmapClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
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
            horizontalArrangement = Arrangement.spacedBy(34.dp, Alignment.CenterHorizontally)
        ) {
            roadmaps.forEach { roadmap ->
                val iconRes = getIconResource(roadmap.icon, roadmap.id)

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(4.dp) // optional small padding
                ) {
                    Surface(
                        modifier = Modifier
                            .clickable { onRoadmapClick(roadmap.id) }
                            .size(72.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.Black
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Image(
                                painter = painterResource(id = iconRes),
                                contentDescription = roadmap.title,
                                modifier = Modifier.size(72.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = roadmap.title,
                        fontFamily = PixelFont,
                        fontSize = 10.sp,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}


