package com.sanjey.codestride.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun FireProgressBar(progress: Float, isOnFire: Boolean) {
    val normalizedProgress = progress.coerceIn(0f, 1f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(16.dp)
            .clip(RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.CenterStart
    ) {
        // ✅ Full gradient background (always visible)
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFFF0000), // Red
                            Color(0xFFFFA500), // Orange
                            Color(0xFFFFFF00)  // Yellow
                        )
                    )
                )
        )

        // ✅ Gray overlay to mask remaining progress (aligned to right)
        if (normalizedProgress < 1f) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(1f - normalizedProgress)
                    .align(Alignment.CenterEnd) // Mask from the right side
                    .background(Color.LightGray)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun FireProgressBarPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        FireProgressBar(progress = 0f, isOnFire = false)
        Spacer(modifier = Modifier.height(12.dp))
        FireProgressBar(progress = 0.3f, isOnFire = false)
        Spacer(modifier = Modifier.height(12.dp))
        FireProgressBar(progress = 0.8f, isOnFire = false)
        Spacer(modifier = Modifier.height(12.dp))
        FireProgressBar(progress = 1f, isOnFire = true) // ✅ Full gradient
    }
}
