package com.sanjey.codestride.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import com.sanjey.codestride.ui.theme.CustomBlue
import com.sanjey.codestride.ui.theme.PixelFont
import com.sanjey.codestride.ui.theme.SoraFont
import com.sanjey.codestride.ui.screens.home.BadgePreviewSection
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.sanjey.codestride.common.FireProgressBar
import com.sanjey.codestride.common.UiState
import com.sanjey.codestride.data.model.HomeScreenData
import com.sanjey.codestride.viewmodel.HomeViewModel
import com.sanjey.codestride.viewmodel.UserViewModel


@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel()
) {
    val homeState by viewModel.homeUiState.collectAsState()
    val profileState by userViewModel.profileState.collectAsState()
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val bannerHeight = screenHeight * 0.15f
    var showEditDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        userViewModel.loadUserProfile()
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
                text = "Profile",
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
                // 🔹 User Info Card
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    color = Color.Black
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.avatar_1),
                            contentDescription = "User profile picture",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        val profileName = when (val state = profileState) {
                            is UiState.Success -> state.data.fullName
                            else -> "Loading..."
                        }

                        Text(
                            text = profileName,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 18.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { showEditDialog = true },
                            shape = RoundedCornerShape(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CustomBlue),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Edit Profile",
                                fontFamily = PixelFont,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ✅ 🔥 Reused from HomeScreen — NO fallback
                if (homeState is UiState.Success) {
                    val data = (homeState as UiState.Success<HomeScreenData>).data

                    Text(
                        text = "\uD83D\uDD25 Streak : ${data.userStats.streak} days",
                        fontFamily = PixelFont,
                        fontSize = 16.sp,
                        color = Color.Black,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    FireProgressBar(
                        progress = data.userStats.progressPercent,
                        isOnFire = true
                    )

                }

                Spacer(modifier = Modifier.height(16.dp))

                // 📘 Current Learning
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    color = Color.Black
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Current Learning",
                            fontFamily = PixelFont,
                            fontSize = 14.sp,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        val moduleText = when (val state = profileState) {
                            is UiState.Success -> state.data.currentRoadmapTitle
                            else -> "Loading current module..."
                        }

                        Text(
                            text = moduleText,
                            fontFamily = PixelFont,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 📊 Module Progress
                val progressText = when (val state = profileState) {
                    is UiState.Success -> "Modules completed: ${state.data.completedModulesCount}/${state.data.totalModulesCount}"
                    else -> "Modules completed: Calculating..."
                }

                Text(
                    text = progressText,
                    fontFamily = PixelFont,
                    fontSize = 14.sp,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 🏅 Badge Section
                when (homeState) {
                    is UiState.Success -> {
                        val data = (homeState as UiState.Success<HomeScreenData>).data
                        BadgePreviewSection(data.badges)
                    }
                    else -> {
                        Text(
                            text = "Loading badges...",
                            fontFamily = PixelFont,
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 🤖 Ask CodeBot
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
                        onClick = { navController.navigate("chatbot") },
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
            }
        }

        if (showEditDialog) {
            Dialog(
                onDismissRequest = { showEditDialog = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                EditProfileCard(
                    onSave = { showEditDialog = false },
                    onCancel = { showEditDialog = false }
                )
            }
        }
    }
}


@Composable
fun EditProfileCard(
    onSave: () -> Unit = {},
    onCancel: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp), // Small edge margin
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(36.dp)),
            color = Color.Black
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // 🖼️ Avatar
                Image(
                    painter = painterResource(id = R.drawable.avatar_1),
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { /* TODO: Image picker */ },
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CustomBlue)
                ) {
                    Text(
                        text = "Edit Image",
                        fontFamily = PixelFont,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ✏️ Fields
                EditProfileField("Name", "Sanjey T.S") { /* TODO */ }
                EditProfileField("Email", "sanjey@example.com") { /* TODO */ }
                EditProfileField("Password", "********") { /* TODO */ }

                Spacer(modifier = Modifier.height(24.dp))

                // ✅ Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = onSave,
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CustomBlue),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text(
                            text = "Save",
                            fontFamily = PixelFont,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }

                    Button(
                        onClick = { onCancel() },
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text(
                            text = "Cancel",
                            fontFamily = PixelFont,
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EditProfileField(label: String, value: String, onEditClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = label,
                fontFamily = SoraFont,
                fontSize = 12.sp,
                color = Color.Gray
            )
            Text(
                text = value,
                fontFamily = PixelFont,
                fontSize = 14.sp,
                color = Color.White
            )
        }

        IconButton(onClick = onEditClick) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit",
                tint = Color.White
            )

        }
    }
}

