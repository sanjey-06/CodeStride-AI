package com.sanjey.codestride.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sanjey.codestride.R
import com.sanjey.codestride.ui.theme.CustomBlue
import com.sanjey.codestride.ui.theme.PixelFont
import com.sanjey.codestride.ui.theme.SoraFont
import com.sanjey.codestride.viewmodel.HomeViewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val firstName by viewModel.firstName.observeAsState()
    val quote by viewModel.quoteOfTheDay.observeAsState()
    val scrollState = rememberScrollState()
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val imageHeight = screenHeight * 0.75f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(Color.Black) // so we can see the rounded edge
    ) {
        // Top 75% black background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(imageHeight)
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

            if (firstName != null && quote != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Hello $firstName 👋",
                        fontFamily = PixelFont,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "What will you learn today?",
                        fontFamily = PixelFont,
                        fontSize = 20.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "“$quote”",
                        fontFamily = PixelFont,
                        fontSize = 14.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(horizontal = 32.dp)
                            .fillMaxWidth()
                    )
                }
            }
        }

        // White container overlapping slightly, rounded top corners (50.dp)
        Column(
            modifier = Modifier
                .offset(y = (-30).dp) // lift it into the image
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp)) // clip shape
                .background(Color.White) // shape background
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Text(
                text = "🔥 Current Streak",
                fontFamily = PixelFont,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "3 Days",
                fontFamily = PixelFont,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { 0.6f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(8.dp)),
                color = Color.Black,
                trackColor = Color.LightGray,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "You're 1 day away from earning the Bronze badge!",
                fontFamily = SoraFont,
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { /* TODO */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CustomBlue)
            ) {
                Text(
                    text = "Go to your Roadmap",
                    fontFamily = PixelFont,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(32.dp))

            RoadmapCard(
                iconResId = R.drawable.ic_python,
                title = "Python Programming",
                progressPercent = 50
            )

            Spacer(modifier = Modifier.height(500.dp)) // allows scroll
        }
    }
}
@Composable
fun RoadmapCard(
    iconResId: Int,
    title: String,
    progressPercent: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp) // taller card
        ) {

            // 🔳 Left 35% - Icon Area
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.35f)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = iconResId),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(72.dp) // Larger icon
                )
            }

            // 🟦 Right 65% - Info Area
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.65f)
                    .background(CustomBlue)
                    .padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Your current Learning",
                    fontFamily = SoraFont,
                    fontSize = 10.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = title,
                    fontFamily = PixelFont,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { progressPercent / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color.Black,
                    trackColor = Color.White
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "$progressPercent % completed",
                    fontFamily = PixelFont,
                    fontSize = 12.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RoadmapCardPreview() {
    RoadmapCard(
        iconResId = R.drawable.ic_python, // replace with your actual icon name
        title = "Python Programming",
        progressPercent = 50
    )
}

