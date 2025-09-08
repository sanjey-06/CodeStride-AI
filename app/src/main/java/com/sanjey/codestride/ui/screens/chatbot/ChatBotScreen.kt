package com.sanjey.codestride.ui.screens.chatbot

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sanjey.codestride.R
import com.sanjey.codestride.data.model.ai.Message
import com.sanjey.codestride.ui.theme.CustomBlue
import com.sanjey.codestride.ui.theme.PixelFont
import com.sanjey.codestride.viewmodel.ChatViewModel

@Composable
fun ChatbotScreen(
    navController: NavHostController,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val backgroundColor = colorResource(id = R.color.ChatBotBlue)
    var input by remember { mutableStateOf("") }
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

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
//              contentScale = ContentScale.Crop,
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
        val listState = rememberLazyListState()

        // 🔹 Dynamic message list from ViewModel
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            items(messages) { msg: Message ->
                if (msg.role == "user") {
                    UserMessage(msg.content)
                } else {
                    BotMessage(msg.content)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (isLoading) {
                item {
                    BotMessage("Thinking...")
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        LaunchedEffect(messages, isLoading) {
            if (listState.layoutInfo.totalItemsCount > 0) {
                listState.animateScrollToItem(listState.layoutInfo.totalItemsCount - 1)
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
                value = input,
                onValueChange = { input = it },
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
                onClick = {
                    if (input.isNotBlank()) {
                        viewModel.sendMessage(input)
                        input = ""
                    }
                },
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CustomBlue // your app's theme color
                )
            ) {
                Text(
                    "Send",
                    fontFamily = PixelFont,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun UserMessage(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 48.dp), // push it away from left
        contentAlignment = Alignment.CenterEnd // align to right
    ) {
        Text(
            text = message,
            color = Color.White,
            modifier = Modifier
                .background(CustomBlue, RoundedCornerShape(8.dp))
                .padding(12.dp)
        )
    }
}

@Composable
fun BotMessage(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 48.dp), // push it away from right
        contentAlignment = Alignment.CenterStart // align to left
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
