package com.sanjey.codestride.ui.screens.roadmap

import android.content.Intent
import android.util.Log
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
import androidx.compose.ui.window.Dialog
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sanjey.codestride.R
import com.sanjey.codestride.common.UiState
import com.sanjey.codestride.data.model.Module
import com.sanjey.codestride.data.model.ProgressState
import com.sanjey.codestride.ui.theme.CustomBlue
import com.sanjey.codestride.ui.theme.PixelFont
import com.sanjey.codestride.ui.theme.SoraFont
import com.sanjey.codestride.viewmodel.ModuleViewModel
import com.sanjey.codestride.viewmodel.RoadmapViewModel

@Composable
fun LearningScreen(roadmapId: String, navController: NavController, roadmapViewModel: RoadmapViewModel) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val bannerHeight = screenHeight * 0.15f
    val scrollState = rememberScrollState()

    val moduleViewModel: ModuleViewModel = hiltViewModel()


    val modulesState by moduleViewModel.modulesState.collectAsState()
    val progressState by roadmapViewModel.progressState.collectAsState()
    val roadmapTitle by roadmapViewModel.currentRoadmapTitle

    LaunchedEffect(roadmapId) {
        moduleViewModel.loadModules(roadmapId)
        roadmapViewModel.loadRoadmapTitle(roadmapId)

    }

    LaunchedEffect(modulesState) {
        Log.d("MODULES_STATE", "Current state: $modulesState")
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // 🔷 Top Banner with dynamic title
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
                text = roadmapTitle,
                fontFamily = PixelFont,
                fontSize = 24.sp,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // 🔽 Scrollable White Section with roadmap steps
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                .background(Color.White)
                .verticalScroll(scrollState)
        ) {
            when (modulesState) {
                is UiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = CustomBlue)
                    }
                }

                is UiState.Success -> {
                    val moduleList = (modulesState as UiState.Success<List<Module>>).data

                    Box {
                        Image(
                            painter = painterResource(id = R.drawable.roadmap_bg),
                            contentDescription = "Roadmap Background",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1080f / 3500f),
                            contentScale = ContentScale.Fit
                        )

                        moduleList.forEachIndexed { index, module ->
                            val completedModules =
                                if (progressState is UiState.Success) {
                                    (progressState as UiState.Success<ProgressState>).data.completedModules
                                } else emptyList()

                            val isFirstModule = index == 0
                            val isUnlocked =
                                isFirstModule || completedModules.contains(moduleList[index - 1].id)

                            var showDialog by remember { mutableStateOf(false) }

                            if (showDialog) {
                                Dialog(onDismissRequest = { showDialog = false }) {
                                    Surface(
                                        shape = RoundedCornerShape(32.dp),
                                        color = Color.Black,
                                        modifier = Modifier
                                            .fillMaxWidth(0.95f)
                                            .wrapContentHeight()
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(24.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = module.title,
                                                fontFamily = PixelFont,
                                                color = Color.White
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = module.description,
                                                fontFamily = SoraFont,
                                                color = Color.White
                                            )
                                            Spacer(modifier = Modifier.height(20.dp))

                                            Button(
                                                onClick = {

                                                    roadmapViewModel.updateStreak()


                                                    navController.navigate("learning_content/$roadmapId/${module.id}")
                                                    Log.d("QUIZ_DEBUG", "Start Learning clicked → roadmapId=$roadmapId, moduleId=${module.id}")

                                                },
                                                modifier = Modifier.fillMaxWidth(),
                                                colors = ButtonDefaults.buttonColors(containerColor = CustomBlue),
                                                shape = RoundedCornerShape(12.dp)
                                            ) {
                                                Text(
                                                    "Start Learn ➤",
                                                    fontFamily = PixelFont,
                                                    color = Color.White
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(10.dp))
                                            val context = LocalContext.current


                                            if (module.ytUrl.isNotBlank()) {
                                                Button(
                                                    onClick = {
                                                        roadmapViewModel.updateStreak()
                                                        val intent = Intent(
                                                            Intent.ACTION_VIEW,
                                                            module.ytUrl.toUri()
                                                        )
                                                        context.startActivity(intent)
                                                    },
                                                    modifier = Modifier.fillMaxWidth(),
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = Color.Red
                                                    ),
                                                    shape = RoundedCornerShape(12.dp)
                                                ) {
                                                    Text(
                                                        "Watch on YouTube",
                                                        fontFamily = PixelFont,
                                                        fontSize = 12.sp,
                                                        color = Color.White
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(10.dp))

                                            Button(
                                                onClick = {
                                                    roadmapViewModel.updateStreak()
                                                    moduleViewModel.updateLearningProgress(roadmapId, module.id)
                                                    navController.navigate("quiz_screen/$roadmapId/${module.id}/${module.quizId}")
                                                },
                                                modifier = Modifier.fillMaxWidth(),
                                                colors = ButtonDefaults.buttonColors(containerColor = CustomBlue),
                                                shape = RoundedCornerShape(12.dp)
                                            ) {
                                                Text(
                                                    "Take Quiz",
                                                    fontFamily = PixelFont,
                                                    color = Color.White
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(10.dp))

                                            Button(
                                                onClick = { navController.navigate("chatbot") },
                                                modifier = Modifier.fillMaxWidth(),
                                                colors = ButtonDefaults.buttonColors(containerColor = CustomBlue),
                                                shape = RoundedCornerShape(12.dp)
                                            ) {
                                                Text(
                                                    "CodeBot",
                                                    fontFamily = PixelFont,
                                                    color = Color.White
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // ✅ Module Position + Locking
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .offset(
                                        y = when (index) {
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
                                    )
                                    .padding(horizontal = 24.dp)
                                    .clickable(enabled = isUnlocked) {
                                        if (isUnlocked) {
                                            Log.d("STREAK_DEBUG", "Dialog opened for ${module.id}")

                                            showDialog = true
                                        }
                                    }
                            ) {
                                val backgroundColor = if (isUnlocked) Color(0xFF4CAF50) else Color(0xFFBDBDBD)

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = backgroundColor,
                                    modifier = Modifier
                                        .align(if (index % 2 == 0) Alignment.CenterStart else Alignment.CenterEnd)
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
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(
                                        0xFFBDBDBD
                                    )
                                )
                            ) {
                                Text(
                                    text = "Get Certificate",
                                    fontSize = 14.sp,
                                    fontFamily = PixelFont,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                is UiState.Error -> {
                    Text(
                        "Failed to load modules",
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                else -> {}
            }
        }
    }
}
