package com.sanjey.codestride.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sanjey.codestride.R
import com.sanjey.codestride.common.UiState
import com.sanjey.codestride.data.model.Roadmap
import com.sanjey.codestride.ui.screens.roadmap.RoadmapIconCard
import com.sanjey.codestride.ui.theme.CustomBlue
import com.sanjey.codestride.ui.theme.PixelFont
import com.sanjey.codestride.ui.theme.SoraFont
import com.sanjey.codestride.viewmodel.RoadmapViewModel

@Composable
fun CompletedRoadmapsScreen(navController: NavController) {
    val roadmapViewModel: RoadmapViewModel = hiltViewModel()
    val roadmapsState by roadmapViewModel.roadmapsState.collectAsState()

    LaunchedEffect(Unit) {
        roadmapViewModel.loadCompletedRoadmaps()
    }

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val bannerHeight = screenHeight * 0.15f

    Box(modifier = Modifier.fillMaxSize()) {
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

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier
                        .padding(16.dp)
                        .statusBarsPadding()
                        .size(24.dp)
                        .align(Alignment.TopStart)
                        .clickable { navController.popBackStack() }
                )

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Completed Roadmaps",
                        fontFamily = PixelFont,
                        fontSize = 22.sp,
                        color = Color.White
                    )
                }
            }

            // 🔲 Grid of Completed Roadmaps
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)),
                color = Color.White
            ) {
                when (roadmapsState) {
                    is UiState.Loading -> Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator(color = CustomBlue) }

                    is UiState.Error -> Text(
                        "Failed to load completed roadmaps",
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )

                    is UiState.Empty -> Text(
                        "No completed roadmaps yet!",
                        color = Color.Gray,
                        fontFamily = SoraFont,
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.CenterHorizontally)
                    )

                    is UiState.Success -> {
                        val completedRoadmaps =
                            (roadmapsState as UiState.Success<List<Roadmap>>).data

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(20.dp),
                            verticalArrangement = Arrangement.spacedBy(28.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(completedRoadmaps) { roadmap ->
                                RoadmapIconCard(roadmap = roadmap) {
                                    navController.navigate("learning/${roadmap.id}?isCompleted=true")
                                }
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🚧 This feature is currently under development. It will be out soon.",
                                color = Color.Gray,
                                fontFamily = SoraFont,
                                fontSize = 14.sp
                            )
                        }

                    }

                    else -> {}
                }
            }
        }
    }
}
