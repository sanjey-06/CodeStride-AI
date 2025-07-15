package com.sanjey.codestride.ui.screens.quiz


import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sanjey.codestride.R
import com.sanjey.codestride.ui.theme.PixelFont
import com.sanjey.codestride.ui.theme.SoraFont
import com.sanjey.codestride.viewmodel.QuizViewModel

@Composable
fun QuizScreen(
    navController: NavController,
    roadmapId: String,
    moduleId: String,
    quizId: String
) {
    val viewModel: QuizViewModel = hiltViewModel()

    val questions by viewModel.questions
    val errorMessage by viewModel.errorMessage

    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var score by remember { mutableIntStateOf(0) }

    LaunchedEffect(roadmapId, moduleId, quizId) {
        viewModel.loadQuestions(roadmapId, moduleId, quizId)
    }

    when {
        errorMessage != null -> {
            Text(
                text = "Error: $errorMessage",
                color = Color.Red,
                modifier = Modifier.padding(16.dp)
            )
        }

        questions.isEmpty() -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.Blue)
            }
        }

        else -> {
            val currentQuestion = questions[currentIndex]

            QuizContentUI(
                navController = navController,
                questionText = currentQuestion.questionText,
                questionNumber = "Question ${currentIndex + 1} of ${questions.size}",
                options = currentQuestion.options,
                selectedOption = selectedOption,
                onOptionSelect = { selectedOption = it },
                onSubmit = {
                    if (selectedOption == currentQuestion.correctAnswer) {
                        score++
                    }
                    if (currentIndex < questions.size - 1) {
                        currentIndex++
                        selectedOption = null
                    } else {
                        navController.navigate("quiz_result/$score/${questions.size}")
                    }
                },
                submitEnabled = selectedOption != null
            )
        }
    }
}


@Composable
fun QuizContentUI(
    navController: NavController,
    questionText: String,
    questionNumber: String,
    options: List<String>,
    selectedOption: String?,
    onOptionSelect: (String) -> Unit,
    onSubmit: () -> Unit,
    submitEnabled: Boolean
) {
//    val context = LocalContext.current
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val bannerHeight = screenHeight * 0.15f
    val scrollState = rememberScrollState()

    Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // 🔼 Top Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(bannerHeight)
        ) {
            Image(
                painter = painterResource(id = R.drawable.quizscreen_background),
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
                text = "Quiz",
                fontFamily = PixelFont,
                fontSize = 28.sp,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // 🔽 Scrollable White Rounded Section
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                .background(Color.White)
                .verticalScroll(scrollState)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = questionText,
                    fontSize = 18.sp,
                    fontFamily = SoraFont,
                    color = Color.Black,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                // 🟦 Question Label
                Surface(
                    color = Color(0xFF03A9F4),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = questionNumber,
                        fontFamily = PixelFont,
                        color = Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 🔘 Dynamic Options
                options.forEach { option ->
                    val isSelected = selectedOption == option
                    val optionBg = if (isSelected) Color(0xFF03A9F4) else Color.White
                    val textColor = if (isSelected) Color.White else Color.Black

                    Surface(
                        color = optionBg,
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, Color.Gray),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable { onOptionSelect(option) }
                    ) {
                        Text(
                            text = option,
                            modifier = Modifier.padding(16.dp),
                            fontFamily = SoraFont,
                            color = textColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ✅ Submit Button
                Button(
                    onClick = onSubmit,
                    enabled = submitEnabled,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50),
                        disabledContainerColor = Color.LightGray
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Submit", fontFamily = PixelFont, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
