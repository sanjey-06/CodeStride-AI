package com.sanjey.codestride.ui.screens.chatbot

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sanjey.codestride.R
import com.sanjey.codestride.ui.theme.CustomBlue
import com.sanjey.codestride.ui.theme.PixelFont


@Composable

fun ChatbotScreen(navController: NavHostController) {
    val backgroundColor = colorResource(id = R.color.ChatBotBlue)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        // Back Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp) // ✅ adjust this if needed
                .background(color = backgroundColor)
        ) {
            Image(
                painter = painterResource(id = R.drawable.chatbot_background),
                contentDescription = "AI Bot",
                modifier = Modifier
                    .fillMaxSize(),
//                contentScale = ContentScale.Crop,
                alignment = Alignment.Center
            )

        IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .padding(start = 12.dp, top = 12.dp)
                    .size(32.dp)
                    .align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        }


        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "CodeBot",
                fontFamily = PixelFont,
                fontSize = 20.sp,
                color = Color.Black
            )
        }


        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                UserMessage("What is meant by constructor?")
                Spacer(modifier = Modifier.height(8.dp))
                BotMessage("A constructor is a special method used to initialize objects.")
            }
        }

        // Input
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = "", onValueChange = {},
                placeholder = { Text("Ask a question...") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    disabledContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            // ✅ Themed button
            Button(
                onClick = { /* TODO: Send logic */ },
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CustomBlue // your app's theme color
                )
            ) {
                Text("Send",
                    fontFamily = PixelFont,
                    fontSize = 12.sp)
            }
        }


    }
}

@Composable
fun UserMessage(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 48.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = message,
            color = Color.Black,
            modifier = Modifier
                .background(Color(0xFF57C5E0), RoundedCornerShape(8.dp))
                .padding(12.dp)
        )
    }
}

@Composable
fun BotMessage(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 48.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = message,
            color = Color.Black,
            modifier = Modifier
                .background(Color.LightGray, RoundedCornerShape(8.dp))
                .padding(12.dp)
        )
    }
}
