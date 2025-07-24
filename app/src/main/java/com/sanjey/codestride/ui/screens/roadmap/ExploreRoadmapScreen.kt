package com.sanjey.codestride.ui.screens.roadmap

import android.net.Uri
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
import coil.compose.AsyncImage
import com.sanjey.codestride.R
import com.sanjey.codestride.common.UiState
import com.sanjey.codestride.data.model.Roadmap
import com.sanjey.codestride.ui.theme.CustomBlue
import com.sanjey.codestride.ui.theme.PixelFont
import com.sanjey.codestride.ui.theme.SoraFont
import com.sanjey.codestride.viewmodel.RoadmapViewModel



@Composable
fun ExploreRoadmapsScreen(navController: NavController) {
    val roadmapViewModel: RoadmapViewModel = hiltViewModel()
    LaunchedEffect(Unit) {
        roadmapViewModel.loadRoadmaps()
    }

    var selectedRoadmap by remember { mutableStateOf<Roadmap?>(null) }
    val roadmapsState by roadmapViewModel.roadmapsState.collectAsState()
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val bannerHeight = screenHeight * 0.15f
    var showDialog by remember { mutableStateOf(false) }

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
                is UiState.Idle -> { /* Optional: show nothing */ }
                is UiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = CustomBlue)
                    }
                }

                is UiState.Error -> {
                    Text(
                        "Failed to load roadmaps",
                        color = Color.Red, modifier = Modifier.padding(16.dp))
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
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(roadmaps) { roadmap ->
                            RoadmapIconCard(roadmap.title, roadmap.icon) { selectedRoadmap = roadmap }
                        }
                    }
                }
            }
        }
    }

    // 🔳 Pop-Up Dialog When a Card is Clicked
    if (selectedRoadmap != null) {
        AlertDialog(
            onDismissRequest = { selectedRoadmap = null },
            confirmButton = {
                Button(
                    onClick = {
                        val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: return@Button
                        roadmapViewModel.startRoadmap(userId, selectedRoadmap!!.id)
                        navController.navigate("learning/${selectedRoadmap!!.id}")
                        selectedRoadmap = null
                    },
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text("Start", fontFamily = PixelFont, fontSize = 14.sp, color = Color.Black)
                }
            },
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
            containerColor = Color.Black,
            shape = RoundedCornerShape(20.dp)
        )
    }

    if (showDialog) {
        GenerateRoadmapDialog(
            onDismiss = { showDialog = false },
            navController = navController
        )
    }
}

@Composable
fun RoadmapIconCard(title: String, icon: String, onClick: () -> Unit) {
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
                AsyncImage(
                    model = icon,
                    contentDescription = title,
                    modifier = Modifier.size(100.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            fontFamily = PixelFont,
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateRoadmapDialog(
    onDismiss: () -> Unit,
    navController: NavController
) {
    var userInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(
                onClick = {
                    if (userInput.isNotBlank()) {
                        onDismiss()
                        navController.navigate("learning/${Uri.encode(userInput)}")
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CustomBlue)
            ) {
                Text("Generate", fontFamily = PixelFont, color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = { onDismiss() },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
            ) {
                Text("Cancel", fontFamily = PixelFont)
            }
        },
        title = {
            Text("Generate Your Own Roadmap", fontFamily = PixelFont, fontSize = 20.sp, color = Color.White)
        },
        text = {
            Column {
                Text("Enter a topic to generate your AI roadmap.", fontFamily = SoraFont, fontSize = 14.sp, color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = userInput,
                    onValueChange = { userInput = it },
                    placeholder = {
                        Text("e.g., Kotlin Basics", fontFamily = SoraFont, color = Color.Gray)
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.DarkGray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedIndicatorColor = CustomBlue,
                        unfocusedIndicatorColor = Color.Gray,
                        cursorColor = CustomBlue
                    )
                )
            }
        },
        containerColor = Color.Black
    )
}
