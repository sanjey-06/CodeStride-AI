package com.sanjey.codestride.ui.screens.auth

import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.lifecycle.viewmodel.compose.viewModel
import android.widget.Toast
import androidx.activity.compose.BackHandler
import com.sanjey.codestride.viewmodel.SignupViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sanjey.codestride.R
import com.sanjey.codestride.ui.theme.PixelFont
import com.sanjey.codestride.ui.theme.SoraFont
import com.sanjey.codestride.ui.theme.CustomBlue
import kotlinx.coroutines.delay

@Composable
fun SignupScreen(navController: NavController) {
    val viewModel: SignupViewModel = viewModel()
    val signupResult by viewModel.signupResult.observeAsState()
    val context = LocalContext.current

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val fullText = "SIGN UP"
    var visibleText by remember { mutableStateOf("") }
    var showValidationErrors by remember { mutableStateOf(false) }
    val mobileRegex = Regex("^\\+?[0-9\\s-]{7,}$")

    val firstNameError = showValidationErrors && firstName.isBlank()
    val lastNameError = showValidationErrors && lastName.isBlank()
    val mobileError = showValidationErrors && !mobile.matches(mobileRegex)
    val emailError = showValidationErrors && !Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val passwordError = showValidationErrors && password.length < 6
    val confirmPasswordError = showValidationErrors && password != confirmPassword


    LaunchedEffect(Unit) {
        for (i in fullText.indices) {
            visibleText = fullText.substring(0, i + 1)
            delay(120)
        }
    }

    signupResult?.let { result ->
        result.onSuccess {
            navController.navigate("home") {
                popUpTo("signup") { inclusive = true }
                launchSingleTop = true
            }
            viewModel.clearResult()
        }.onFailure { error ->
            Toast.makeText(context, error.message ?: "Signup failed", Toast.LENGTH_LONG).show()
            viewModel.clearResult()
        }
    }

    BackHandler {
        navController.navigate("login") {
            popUpTo("signup") { inclusive = true }
        }
    }


    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.signupscreen_background),
            contentDescription = "Signup Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 64.dp)
                .verticalScroll(scrollState)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = visibleText,
                fontFamily = PixelFont,
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(40.dp))

            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name", fontFamily = PixelFont, fontSize = 15.sp, color = Color.White) },
                isError = firstNameError,
                supportingText = {
                    if (firstNameError) Text("First name is required", color = Color.Red, fontSize = 12.sp) },
                singleLine = true,
                textStyle = TextStyle(fontFamily = SoraFont, fontSize = 18.sp),
                colors = inputColors(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name", fontFamily = PixelFont, fontSize = 15.sp, color = Color.White) },
                isError = lastNameError,
                supportingText = {
                    if (lastNameError) Text("Last name is required", color = Color.Red, fontSize = 12.sp) },
                singleLine = true,
                textStyle = TextStyle(fontFamily = SoraFont, fontSize = 18.sp),
                colors = inputColors(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = mobile,
                onValueChange = { mobile = it },
                label = { Text("Mobile Number", fontFamily = PixelFont, fontSize = 15.sp, color = Color.White) },
                isError = mobileError,
                supportingText = {
                    if (mobileError) Text("Enter valid phone number", color = Color.Red, fontSize = 12.sp) },
                singleLine = true,
                textStyle = TextStyle(fontFamily = SoraFont, fontSize = 18.sp),
                colors = inputColors(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", fontFamily = PixelFont, fontSize = 15.sp, color = Color.White) },
                isError = emailError,
                supportingText = {
                    if (emailError) Text("Enter a valid email", color = Color.Red, fontSize = 12.sp) },
                singleLine = true,
                textStyle = TextStyle(fontFamily = SoraFont, fontSize = 18.sp),
                colors = inputColors(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", fontFamily = PixelFont, fontSize = 15.sp, color = Color.White) },
                isError = passwordError,
                supportingText = {
                    if (passwordError) Text("Password must be at least 6 characters", color = Color.Red, fontSize = 12.sp) },
                singleLine = true,
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(icon, contentDescription = null, tint = Color.White)
                    }
                },
                textStyle = TextStyle(fontFamily = SoraFont, fontSize = 18.sp),
                colors = inputColors(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password", fontFamily = PixelFont, fontSize = 15.sp, color = Color.White) },
                isError = confirmPasswordError,
                supportingText = {
                    if (confirmPasswordError) Text("Passwords do not match", color = Color.Red, fontSize = 12.sp) },
                singleLine = true,
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(icon, contentDescription = null, tint = Color.White)
                    }
                },
                textStyle = TextStyle(fontFamily = SoraFont, fontSize = 16.sp),
                colors = inputColors(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = {
                    showValidationErrors = true

                    if (!firstNameError && !lastNameError && !mobileError && !emailError && !passwordError && !confirmPasswordError) {
                        errorMessage = ""
                        viewModel.signupUser(email, password, firstName, lastName, mobile)
                    } else {
                        errorMessage = "Please fix the above errors"
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("CREATE ACCOUNT", color = Color.Black, fontFamily = PixelFont, fontSize = 18.sp)
            }

            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(errorMessage, color = Color.Red, fontFamily = PixelFont)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Already have an account?",
                fontSize = 13.sp,
                color = Color.White,
                fontFamily = PixelFont
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    navController.navigate("login") {
                        popUpTo("signup") { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Log In", color = Color.Black, fontFamily = PixelFont, fontSize = 16.sp)
            }
        }
    }
}

// Shared input field color style
@Composable
private fun inputColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = CustomBlue,
    unfocusedBorderColor = Color.White,
    focusedLabelColor = CustomBlue,
    unfocusedLabelColor = Color.White,
    cursorColor = CustomBlue,
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White
)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignupScreenPreview() {
    val navController = rememberNavController()


    SignupScreen(
        navController = navController
    )
}





