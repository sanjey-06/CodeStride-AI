package com.sanjey.codestride.ui.screens.settings

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sanjey.codestride.R
import com.sanjey.codestride.data.model.UserSettings
import com.sanjey.codestride.ui.theme.CustomBlue
import com.sanjey.codestride.ui.theme.PixelFont
import com.sanjey.codestride.ui.theme.SoraFont
import com.sanjey.codestride.viewmodel.UserViewModel

@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, userViewModel: UserViewModel){
    LaunchedEffect(Unit) {
        userViewModel.loadUserSettings()
    }
    val accountDeleted by userViewModel.accountDeleted.observeAsState()
    LaunchedEffect(accountDeleted) {
        if (accountDeleted == true) {
            navController.navigate("login") {
                popUpTo("main") { inclusive = true }
            }
        }
    }
    val isLoggedOut by userViewModel.isLoggedOut.observeAsState()
    LaunchedEffect(isLoggedOut) {
        if (isLoggedOut == true) {
            navController.navigate("login") {
                popUpTo("main") { inclusive = true }
            }
        }
    }


    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val bannerHeight = screenHeight * 0.15f

    val settings by userViewModel.userSettings.observeAsState()

    val isReminderOn = remember(settings) { mutableStateOf(settings?.reminderEnabled ?: true) }
    val timeState = rememberTimePickerState(
        initialHour = settings?.reminderHour ?: 17,
        initialMinute = settings?.reminderMinute ?: 0,
        is24Hour = false
    )
    var showTimeInput by remember { mutableStateOf(false) }

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
                text = "Settings",
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
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                // Reminder Settings
                Text(
                    text = "Reminder Settings",
                    fontFamily = PixelFont,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = Color.Black,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Reminders", fontFamily = SoraFont, color = Color.White)
                            Switch(
                                checked = isReminderOn.value,
                                onCheckedChange = {
                                    isReminderOn.value = it
                                    userViewModel.saveUserSettings(
                                        UserSettings(
                                            reminderEnabled = it,
                                            reminderHour = timeState.hour,
                                            reminderMinute = timeState.minute
                                        )
                                    )
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = CustomBlue
                                ),
                                interactionSource = remember { MutableInteractionSource() }
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Reminder Timings",
                            fontFamily = SoraFont,
                            color = if (isReminderOn.value) Color.White else Color.Gray
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            color = if (isReminderOn.value) CustomBlue else Color.Gray,
                            shape = RoundedCornerShape(50.dp),
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .clickable(enabled = isReminderOn.value) { showTimeInput = true }
                        ) {
                            Text(
                                text = formatTime(timeState.hour, timeState.minute),
                                fontFamily = PixelFont,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                if (showTimeInput) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.Black
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            TimeInput(state = timeState)

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    showTimeInput = false
                                    userViewModel.saveUserSettings(
                                        UserSettings(
                                            reminderEnabled = isReminderOn.value,
                                            reminderHour = timeState.hour,
                                            reminderMinute = timeState.minute
                                        )
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CustomBlue),
                                shape = RoundedCornerShape(50.dp)
                            ) {
                                Text("Save Time", color = Color.White, fontFamily = PixelFont)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Support Section
                SettingsSection(
                    title = "Support",
                    items = listOf("Ask CodeBot", "Contact Support"),
                    onItemClick = { item ->
                        if (item == "Ask CodeBot") {
                            navController.navigate("chatbot")
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Account Section
                SettingsSection(
                    title = "Account",
                    items = listOf("Delete Account", "Manage your Profile"),
                    onItemClick = { item ->
                        if (item == "Manage your Profile") {
                            navController.navigate("profile")
                        }
                        if (item == "Delete Account"){
                            showDeleteDialog = true
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Logout Button
                Button(
                    onClick = {
                        showLogoutDialog = true

                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    shape = RoundedCornerShape(50.dp),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    Text(
                        text = "Logout",
                        fontFamily = PixelFont,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Account?", fontFamily = PixelFont) },
            text = { Text("Are you sure you want to permanently delete your account?", fontFamily = SoraFont) },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    userViewModel.deleteUserAccount()
                }) {
                    Text("Yes", color = Color.Red, fontFamily = PixelFont)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", fontFamily = PixelFont)
                }
            },
            containerColor = Color.White
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout?", fontFamily = PixelFont) },
            text = { Text("Are you sure you want to logout?", fontFamily = SoraFont) },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    userViewModel.logout()
                }) {
                    Text("Yes", color = CustomBlue, fontFamily = PixelFont)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", fontFamily = PixelFont)
                }
            },
            containerColor = Color.White
        )
    }

}

@Composable
fun SettingsSection(title: String, items: List<String>, onItemClick: (String) -> Unit = {}) {
    Text(
        text = title,
        fontFamily = PixelFont,
        fontSize = 16.sp,
        color = Color.Black
    )

    Spacer(modifier = Modifier.height(12.dp))

    Surface(
        color = Color.Black,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            items.forEachIndexed { index, item ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemClick(item) }
                        .padding(vertical = 12.dp, horizontal = 16.dp)
                ) {
                    Text(
                        text = item,
                        fontFamily = SoraFont,
                        color = Color.White
                    )
                }
                if (index != items.lastIndex) {
                    HorizontalDivider(thickness = 1.dp, color = Color.Gray.copy(alpha = 0.4f))
                }
            }
        }
    }
}

@Composable
fun formatTime(hour: Int, minute: Int): String {
    val period = if (hour < 12) "A.M" else "P.M"
    val hour12 = if (hour == 0 || hour == 12) 12 else hour % 12
    val paddedMinute = minute.toString().padStart(2, '0')
    return "$hour12.$paddedMinute $period"
}
