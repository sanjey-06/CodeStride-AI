package com.sanjey.codestride.ui.screens.roadmap

import android.util.Log
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
import com.sanjey.codestride.common.UiState
import com.sanjey.codestride.data.model.Roadmap
import com.sanjey.codestride.ui.components.AiGeneratorSection
import com.sanjey.codestride.ui.components.RoadmapReplaceDialog
import com.sanjey.codestride.ui.screens.home.ExploreOtherRoadmapsSection
import com.sanjey.codestride.ui.theme.CustomBlue
import com.sanjey.codestride.ui.theme.PixelFont
import com.sanjey.codestride.ui.theme.SoraFont
import com.sanjey.codestride.viewmodel.HomeViewModel
import com.sanjey.codestride.viewmodel.RoadmapViewModel

@Composable
fun RoadmapScreen(appNavController: NavController, roadmapViewModel: RoadmapViewModel, homeViewModel: HomeViewModel) {

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val bannerHeight = screenHeight * 0.15f

    var showDialog by remember { mutableStateOf(false) }
    var newRoadmapId by remember { mutableStateOf<String?>(null) }


    val currentRoadmapId by roadmapViewModel.currentRoadmapId.collectAsState()
    val progressState by roadmapViewModel.progressState.collectAsState()
    val homeState by homeViewModel.homeUiState.collectAsState()



    var currentModule by remember { mutableStateOf("Loading...") }

    LaunchedEffect(progressState) {
        if (progressState is UiState.Success) {
            val data = (progressState as UiState.Success).data
            if (data.currentModuleTitle.isNotBlank()) {
                currentModule = data.currentModuleTitle
            }
            Log.d("ROADMAP_UI_DEBUG", "Updated currentModule → $currentModule")
        }
    }





    LaunchedEffect(progressState) {
        Log.d("ROADMAP_UI_DEBUG", "RoadmapScreen recomposed → progressState=$progressState")
    }


    val exploreRoadmaps: List<Roadmap> = when (val state = homeState) {
        is UiState.Success -> state.data.exploreRoadmaps
        else -> emptyList()
    }




    // ✅ Call this once to start observing roadmap & progress
    LaunchedEffect(Unit) {

        roadmapViewModel.observeCurrentRoadmap()
    }

    // ✅ Derive UI title & icon based on roadmapId
    val (currentTitle, currentIcon) = roadmapViewModel.getRoadmapTitleAndIcon(currentRoadmapId)


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
                        Log.d("ROADMAP_DEBUG", "Start Learning clicked")

                        val roadmapId = currentRoadmapId
                        val completedModules = if (progressState is UiState.Success) {
                            (progressState as UiState.Success).data.completedModules
                        } else {
                            emptyList()
                        }

                        val currentModuleId = (progressState as? UiState.Success)?.data?.currentModuleId

                        // ✅ Update Firestore only if moving forward
                        if (roadmapId != null && currentModuleId != null) {
                            roadmapViewModel.updateCurrentModuleIfForward(
                                roadmapId = roadmapId,
                                moduleId = currentModuleId, // ✅ Correct ID now
                                completedModules = completedModules
                            )

                            // ✅ Navigate to learning screen
                            appNavController.navigate("learning/$roadmapId")
                        }
                    },
                    enabled = currentRoadmapId != null && progressState is UiState.Success,
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CustomBlue)
                ) {
                    Text(
                        text = "Continue Learning",
                        fontFamily = PixelFont,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))


                AiGeneratorSection { topic ->
                    roadmapViewModel.generateAiRoadmap(topic)
                }


                Spacer(modifier = Modifier.height(24.dp))

                ExploreOtherRoadmapsSection(
                    navController = appNavController,
                    roadmaps = exploreRoadmaps, // ✅ Now a List<Roadmap>
                    onRoadmapClick = { selectedRoadmapId ->
                        if (roadmapViewModel.hasActiveRoadmap()) {
                            newRoadmapId = selectedRoadmapId
                            showDialog = true
                        } else {
                            roadmapViewModel.startRoadmap(selectedRoadmapId)
                            appNavController.navigate("learning/$selectedRoadmapId") // ✅ Add this if missing
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                RoadmapReplaceDialog(
                    showDialog = showDialog && newRoadmapId != null,
                    onDismiss = { showDialog = false },
                    onConfirm = {
                        // ✅ Runs in a coroutine (dialog handles loading state)
                        roadmapViewModel.replaceRoadmap(newRoadmapId!!)
                        showDialog = false
                        appNavController.navigate("learning/${newRoadmapId!!}") {
                            popUpTo("roadmap") { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}
