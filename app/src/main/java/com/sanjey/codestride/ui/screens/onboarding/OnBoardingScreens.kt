package com.sanjey.codestride.ui.screens.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sanjey.codestride.R
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import com.sanjey.codestride.data.prefs.OnboardingPreferences
import com.sanjey.codestride.ui.theme.PixelFont


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(navController: NavController) {
    val context = LocalContext.current

    val pages = listOf(
        OnboardPage(R.drawable.onboardingscreen1, "Start Learning", "Master concepts through structured modules."),
        OnboardPage(R.drawable.onboardingscreen2, "Follow Your Roadmap", "Visualize your progress step by step."),
        OnboardPage(R.drawable.onboardingscreen3, "Get AI Assistance", "Ask questions anytime during learning.")
    )

    val pagerState = rememberPagerState { pages.size }
    val scope = rememberCoroutineScope()

    HorizontalPager(
        state = pagerState,
        flingBehavior = PagerDefaults.flingBehavior(pagerState),
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) { page ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Image(
                    painter = painterResource(id = pages[page].imageRes),
                    contentDescription = null,
                    modifier = Modifier.sizeIn(maxHeight = 200.dp, maxWidth = 200.dp)
                )
                Text(
                    text = pages[page].title,
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontFamily = PixelFont,
                        color = Color.White
                    )
                )
                Text(
                    text = pages[page].description,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = PixelFont,
                        color = Color.White
                    )
                )
                Button(
                    onClick = {
                        scope.launch {
                            if (page == pages.lastIndex) {
                                OnboardingPreferences.setOnboardingSeen(context, true)
                                navController.navigate("login") {
                                    popUpTo("onboarding") { inclusive = true }
                                }
                            } else {
                                pagerState.animateScrollToPage(page + 1)
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = if (page == pages.lastIndex) "Finish ➤" else "Next ➤",
                        fontFamily = PixelFont,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

data class OnboardPage(
    val imageRes: Int,
    val title: String,
    val description: String
)
