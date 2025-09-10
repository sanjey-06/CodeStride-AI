package com.sanjey.codestride.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sanjey.codestride.R
import com.sanjey.codestride.common.UiState
import com.sanjey.codestride.ui.theme.PixelFont
import com.sanjey.codestride.ui.theme.SoraFont
import com.sanjey.codestride.ui.theme.CustomBlue
import com.sanjey.codestride.viewmodel.AuthViewModel
import kotlinx.coroutines.delay
import androidx.compose.runtime.livedata.observeAsState


@Composable
fun ForgotPasswordScreen(navController: NavController) {
    val viewModel: AuthViewModel = hiltViewModel()
    var email by remember { mutableStateOf("") }
    val context = LocalContext.current
    val resetState by viewModel.resetPasswordState.observeAsState()

    var titleText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val fullText = "FORGOT\n\nPASSWORD"
        for (i in fullText.indices) {
            titleText = fullText.substring(0, i + 1)
            delay(120)
        }
    }

    // ✅ Handle state changes
    LaunchedEffect(resetState) {
        when (resetState) {
            is UiState.Success -> {
                Toast.makeText(context, "Reset email sent!", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
                viewModel.clearResetState()
            }
            is UiState.Error -> {
                Toast.makeText(context, (resetState as UiState.Error).message, Toast.LENGTH_SHORT).show()
                viewModel.clearResetState()
            }
            else -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.forgotpassword_backgroundimage),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = titleText,
                fontFamily = PixelFont,
                fontSize = 26.sp,
                textAlign = TextAlign.Center,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Enter your email", fontFamily = PixelFont, color = Color.White) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CustomBlue,
                    unfocusedBorderColor = Color.White,
                    focusedLabelColor = CustomBlue,
                    unfocusedLabelColor = Color.White,
                    cursorColor = CustomBlue,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                textStyle = TextStyle(fontFamily = SoraFont, fontSize = 18.sp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (email.isBlank()) {
                        Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.sendPasswordReset(email)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("SEND RESET LINK", color = Color.Black, fontFamily = PixelFont, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Back to Login",
                fontFamily = PixelFont,
                fontSize = 14.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.clickable {
                    navController.popBackStack()
                }
            )
        }
    }

    // ✅ Show Loader if UiState.Loading
    if (resetState is UiState.Loading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color.White)
        }
    }
}

