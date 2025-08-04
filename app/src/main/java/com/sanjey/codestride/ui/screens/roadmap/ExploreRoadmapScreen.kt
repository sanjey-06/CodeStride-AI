package com.sanjey.codestride.ui.screens.roadmap

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sanjey.codestride.R
import com.sanjey.codestride.common.UiState
import com.sanjey.codestride.common.getIconResource
import com.sanjey.codestride.data.model.Roadmap
import com.sanjey.codestride.ui.components.AiGeneratorSection
import com.sanjey.codestride.ui.components.RoadmapReplaceDialog
import com.sanjey.codestride.ui.theme.CustomBlue
import com.sanjey.codestride.ui.theme.PixelFont
import com.sanjey.codestride.ui.theme.SoraFont
import com.sanjey.codestride.viewmodel.RoadmapViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ExploreRoadmapsScreen(navController: NavController) {
    val roadmapViewModel: RoadmapViewModel = hiltViewModel()

    // ✅ Load roadmaps on first launch
    LaunchedEffect(Unit) {
        roadmapViewModel.loadRoadmaps()
        roadmapViewModel.observeCurrentRoadmap()
    }

    var selectedRoadmap by remember { mutableStateOf<Roadmap?>(null) }
    var showPreviewDialog by remember { mutableStateOf(false) }
    var showReplaceDialog by remember { mutableStateOf(false) }
    var newRoadmapId by remember { mutableStateOf<String?>(null) }
    val roadmapsState by roadmapViewModel.roadmapsState.collectAsState()

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val bannerHeight = screenHeight * 0.15f

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
                    .size(24.dp)
                    .align(Alignment.TopStart)
                    .clickable { navController.popBackStack() }
            )

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Explore Roadmaps",
                    fontFamily = PixelFont,
                    fontSize = 22.sp,
                    color = Color.White
                )
            }
        }


        // 🔲 Grid of Roadmaps
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)),
            color = Color.White
        ) {
            when (roadmapsState) {
                is UiState.Idle -> {}
                is UiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = CustomBlue)
                    }
                }

                is UiState.Error -> {
                    Text("Failed to load roadmaps", color = Color.Red, modifier = Modifier.padding(16.dp))
                }

                is UiState.Empty -> {
                    Text("No roadmaps available", color = Color.Gray, modifier = Modifier.padding(16.dp))
                }

                is UiState.Success -> {
                    val roadmaps = (roadmapsState as UiState.Success).data

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(20.dp),
                        verticalArrangement = Arrangement.spacedBy(28.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(roadmaps) { roadmap ->
                            RoadmapIconCard(roadmap = roadmap) {
                                selectedRoadmap = roadmap
                                showPreviewDialog = true
                            }
                        }

                        // ✅ Full-width item for the AI Generator
                        item(span = { GridItemSpan(2) }) {
                            AiGeneratorSection { topic ->
                                CoroutineScope(Dispatchers.Main).launch {
                                    val newId = roadmapViewModel.generateAiRoadmapAndReturnId(topic)
                                    if (newId != null) {
                                        if (roadmapViewModel.hasActiveRoadmap()) {
                                            newRoadmapId = newId           // ✅ Reuse existing var
                                            showReplaceDialog = true       // ✅ Reuse existing dialog
                                        } else {
                                            roadmapViewModel.startRoadmap(newId)
                                            navController.navigate("learning/$newId")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }

    }

    // ✅ Preview Dialog for roadmap details
    if (showPreviewDialog && selectedRoadmap != null) {
        AlertDialog(
            onDismissRequest = { showPreviewDialog = false },
            title = {
                Text(
                    text = "Introduction to ${selectedRoadmap!!.title}",
                    fontFamily = PixelFont,
                    fontSize = 18.sp,
                    color = Color.White
                )
            },
            text = {
                Text(
                    text = selectedRoadmap!!.description,
                    fontFamily = SoraFont,
                    fontSize = 14.sp,
                    color = Color.White
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (roadmapViewModel.hasActiveRoadmap()) {
                            newRoadmapId = selectedRoadmap!!.id
                            showReplaceDialog = true
                        } else {
                            roadmapViewModel.startRoadmap(selectedRoadmap!!.id)
                            navController.navigate("learning/${selectedRoadmap!!.id}")
                        }
                        showPreviewDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text("Start", fontFamily = PixelFont, color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPreviewDialog = false }) {
                    Text("Cancel", fontFamily = PixelFont, fontSize = 14.sp, color = Color.White)
                }
            },
            containerColor = Color.Black,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // ✅ Replace Roadmap Confirmation Dialog
    RoadmapReplaceDialog(
        showDialog = showReplaceDialog && newRoadmapId != null,
        onDismiss = { showReplaceDialog = false },
        onConfirm = {
            roadmapViewModel.replaceRoadmap(newRoadmapId!!)
            showReplaceDialog = false
            navController.navigate("learning/${newRoadmapId!!}") {
                popUpTo("roadmap") { inclusive = false }
                launchSingleTop = true
            }
        }
    )


}

@Composable
fun RoadmapIconCard(roadmap: Roadmap, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(160.dp)
            .clickable { onClick() }
    ) {
        Surface(
            modifier = Modifier.size(120.dp),
            shape = RoundedCornerShape(20.dp),
            color = Color.Black
        ) {
            Box(contentAlignment = Alignment.Center) {
                val iconRes = getIconResource(roadmap.icon, roadmap.id)

                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = roadmap.title,
                    modifier = Modifier.size(100.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = roadmap.title,
            fontFamily = PixelFont,
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}
