package com.sanjey.codestride.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.sanjey.codestride.R

@Composable
fun FireProgressBar(progress: Float, isOnFire: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(16.dp) // ✅ Visible progress bar height
            .clip(RoundedCornerShape(8.dp))
            .background(Color.LightGray)
            .graphicsLayer {
                clip = false // ✅ Let flames overflow outside
            },
        contentAlignment = Alignment.BottomStart
    ) {
        // ✅ Base progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .fillMaxHeight()
                .background(Color.Red)
        )

        // ✅ Flames
        if (isOnFire) {
            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.fire))
            val progressAnim by animateLottieCompositionAsState(
                composition = composition,
                iterations = LottieConstants.IterateForever
            )

            LottieAnimation(
                composition = composition,
                progress = { progressAnim },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp) // ✅ Tall enough for flames
                    .graphicsLayer {
                        scaleX = 1f  // No crazy stretching horizontally
                        scaleY = 1f // 🔥 Reduce height since JSON is 400px tall
                    }
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun FireProgressBarPreview() {
    FireProgressBar(
        progress = 0.8f,
        isOnFire = true
    )
}
