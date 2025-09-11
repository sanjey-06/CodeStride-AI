package com.sanjey.codestride.ui.screens.profile

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.scale
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.sanjey.codestride.common.FireProgressBar
import com.sanjey.codestride.common.UiState
import com.sanjey.codestride.common.getAvatarResourceId
import com.sanjey.codestride.data.model.HomeScreenData
import com.sanjey.codestride.viewmodel.HomeViewModel
import com.sanjey.codestride.viewmodel.UserViewModel
import kotlinx.coroutines.launch


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


    var showPasswordDialog by remember { mutableStateOf(false) }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var passwordChangeMessage by remember { mutableStateOf<String?>(null) }


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
                fontSize = 24.sp,
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
                        val currentAvatar = when (val state = profileState) {
                            is UiState.Success -> state.data.avatar
                            else -> "ic_none"
                        }

                        Image(
                            painter = painterResource(id = getAvatarResourceId(currentAvatar)),
                            contentDescription = "User profile picture",
                            modifier = Modifier
                                .sizeIn(minWidth = 72.dp, minHeight = 72.dp, maxWidth = 100.dp, maxHeight = 100.dp)                        )

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

                        val scale = remember { Animatable(1f) }
                        val scope = rememberCoroutineScope()

                        Button(
                            onClick = {
                                scope.launch {
                                    scale.animateTo(0.9f, tween(100))
                                    scale.animateTo(1f, tween(100))
                                }
                                showEditDialog = true
                            },
                            shape = RoundedCornerShape(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CustomBlue),
                            modifier = Modifier
                                .fillMaxWidth()
                                .scale(scale.value)
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
                    is UiState.Success -> "Modules completed: ${state.data.completedModulesCount}/10"
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
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        if (showEditDialog) {
            Dialog(
                onDismissRequest = { showEditDialog = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                EditProfileCard(
                    userViewModel = userViewModel,
                    onPasswordEditClick = { showPasswordDialog = true } // ✅ now works
                )
            }
        }


        if (showPasswordDialog) {
            Dialog(
                onDismissRequest = { showPasswordDialog = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .wrapContentHeight(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.Black
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Change Password", color = Color.White, fontFamily = PixelFont)

                        OutlinedTextField(
                            value = currentPassword,
                            onValueChange = { currentPassword = it },
                            label = { Text("Current Password") }
                        )

                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            label = { Text("New Password") }
                        )

                        passwordChangeMessage?.let {
                            Text(it, color = Color.Red, fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                userViewModel.changePassword(
                                    currentPassword,
                                    newPassword
                                ) { success, error ->
                                    if (success) {
                                        passwordChangeMessage = "Password updated successfully"
                                        showPasswordDialog = false
                                    } else {
                                        passwordChangeMessage = error
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CustomBlue)
                        ) {
                            Text("Update", color = Color.White, fontFamily = PixelFont)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        TextButton(
                            onClick = {
                                showPasswordDialog = false
                                navController.navigate("forgot_password")
                            }
                        ) {
                            Text(
                                text = "Forgot your current password? Click here",
                                fontFamily = PixelFont,
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

    }
}


@Composable
fun EditProfileCard(userViewModel: UserViewModel,
                    onPasswordEditClick: () -> Unit) {
    val avatarOptions = listOf("avatar_1", "avatar_2")
    var showAvatarDialog by remember { mutableStateOf(false) }
    val selectedAvatar = userViewModel.avatar
    val profileEmail = userViewModel.email

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(36.dp))
            .background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // 🖼 Avatar - only 1 visible
        Image(
            painter = painterResource(id = getAvatarResourceId(selectedAvatar)),
            contentDescription = "Profile Image",
            modifier = Modifier
                .sizeIn(minWidth = 72.dp, minHeight = 72.dp, maxWidth = 100.dp, maxHeight = 100.dp)                .clip(CircleShape)
                .clickable { showAvatarDialog = true }
        )

        Spacer(modifier = Modifier.height(16.dp))

        val profileName = (userViewModel.profileState.collectAsState().value as? UiState.Success)?.data?.fullName ?: ""

        ReadOnlyProfileField(
            label = "Name",
            value = profileName
        )


        // 📧 Email (wrapped properly)
        ReadOnlyProfileField(
            label = "Email",
            value = profileEmail
        )


        // 🔒 Password
        EditProfileField(
            label = "Password",
            value = "********",
            onEditClick = onPasswordEditClick
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Dialog to change avatar
        if (showAvatarDialog) {
            Dialog(
                onDismissRequest = { showAvatarDialog = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .wrapContentHeight(),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.Black
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Choose Avatar", color = Color.White, fontFamily = PixelFont)

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            avatarOptions.forEach { avatar ->
                                val isSelected = avatar == userViewModel.avatar
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) CustomBlue else Color.Transparent,
                                            shape = CircleShape
                                        )
                                        .padding(2.dp)
                                ) {
                                    Image(
                                        painter = painterResource(id = getAvatarResourceId(avatar)),
                                        contentDescription = avatar,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                            .clickable { userViewModel.updateAvatar(avatar) }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                userViewModel.saveAvatar()
                                showAvatarDialog = false
                                userViewModel.loadUserProfile() // 🔁 refresh profile to update UI
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CustomBlue),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Save Avatar", color = Color.White, fontFamily = PixelFont)
                        }
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
            .padding(vertical = 16.dp, horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontFamily = SoraFont,
                fontSize = 12.sp,
                color = Color.Gray
            )
            Text(
                text = value,
                fontFamily = PixelFont,
                fontSize = 12.sp,
                color = Color.White,
                lineHeight = 16.sp,
                maxLines = 1
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
@Composable
fun ReadOnlyProfileField(label: String, value: String) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 16.dp, horizontal = 10.dp)) {
        Text(
            text = label,
            fontFamily = SoraFont,
            fontSize = 12.sp,
            color = Color.Gray
        )
        Text(
            text = value,
            fontFamily = PixelFont,
            fontSize = 12.sp,
            color = Color.White,
            lineHeight = 16.sp,
            maxLines = 1
        )
    }
}
