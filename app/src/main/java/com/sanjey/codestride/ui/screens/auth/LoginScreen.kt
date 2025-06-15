package com.sanjey.codestride.ui.screens.auth

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sanjey.codestride.R
import com.sanjey.codestride.ui.theme.CustomBlue
import com.sanjey.codestride.ui.theme.PixelFont
import com.sanjey.codestride.ui.theme.SoraFont
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun LoginScreen(navController: NavController, onLogin: (String, String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var titleText by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val shakeOffset = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        val fullText = "LOGIN"
        for (i in 1..fullText.length) {
            titleText = fullText.substring(0, i)
            delay(150)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.loginscreen_background),
            contentDescription = "Login Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(shakeOffset.value.roundToInt(), 0) }
                .padding(horizontal = 32.dp, vertical = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            Text(
                text = titleText,
                fontFamily = PixelFont,
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(64.dp))

            val emailInteraction = remember { MutableInteractionSource() }
            val isEmailFocused by emailInteraction.collectIsFocusedAsState()

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Username", fontFamily = PixelFont, fontSize = 18.sp, color = Color.White) },
                singleLine = true,
                interactionSource = emailInteraction,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CustomBlue,
                    unfocusedBorderColor = Color.White,
                    focusedLabelColor = CustomBlue,
                    unfocusedLabelColor = Color.White,
                    cursorColor = CustomBlue,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontFamily = SoraFont, fontSize = 18.sp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            val passwordInteraction = remember { MutableInteractionSource() }
            val isPasswordFocused by passwordInteraction.collectIsFocusedAsState()

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", fontFamily = PixelFont, fontSize = 18.sp, color = Color.White) },
                singleLine = true,
                interactionSource = passwordInteraction,
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                    val description = if (showPassword) "Hide password" else "Show password"
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(imageVector = icon, contentDescription = description, tint = Color.White)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CustomBlue,
                    unfocusedBorderColor = Color.White,
                    focusedLabelColor = CustomBlue,
                    unfocusedLabelColor = Color.White,
                    cursorColor = CustomBlue,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontFamily = SoraFont, fontSize = 18.sp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = {
                    if (email.isNotBlank() && password.isNotBlank()) {
                        onLogin(email, password)
                    } else {
                        coroutineScope.launch {
                            errorMessage = "Please enter all fields"
                            shakeOffset.animateTo(-16f, animationSpec = tween(100))
                            shakeOffset.animateTo(16f, animationSpec = tween(100))
                            shakeOffset.animateTo(-8f, animationSpec = tween(100))
                            shakeOffset.animateTo(8f, animationSpec = tween(100))
                            shakeOffset.animateTo(0f, animationSpec = tween(100))
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Text("LOG IN", color = Color.Black, fontFamily = PixelFont, fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Forgot Password ?",
                fontFamily = PixelFont,
                fontSize = 16.sp,
                color = Color.White,
                modifier = Modifier.clickable {
                    navController.navigate("forgot_password")
                }
            )

            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = "Don't have an account ?",
                fontSize = 14.sp,
                color = Color.White,
                fontFamily = PixelFont,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Sign up here",
                fontSize = 14.sp,
                color = Color.White,
                fontFamily = PixelFont,
                textAlign = TextAlign.Center,
                modifier = Modifier.clickable {
                    navController.navigate("signup")
                }
            )

            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = errorMessage, color = Color.Red, fontFamily = PixelFont, fontSize = 14.sp)
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    // Fake NavController for preview only
    val navController = rememberNavController()

    LoginScreen(navController = navController, onLogin = { _, _ -> })
}

