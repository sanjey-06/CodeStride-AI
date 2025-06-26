package com.sanjey.codestride.ui.screens.roadmap

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.*
import com.sanjey.codestride.R
import com.sanjey.codestride.data.model.Module
@Composable
fun LearningScreen(roadmapId: String) {
    val modules = listOf(
        Module("1", "Introduction", 1),
        Module("2", "Basics", 2),
        Module("3", "Variables", 3),
        Module("4", "Data Types", 4),
        Module("5", "Loops", 5),
        Module("6", "Functions", 6)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Image(
            painter = painterResource(id = R.drawable.roadmap_bg), // Your curved road image
            contentDescription = null,
            modifier = Modifier.fillMaxHeight(),
            contentScale = ContentScale.FillBounds
        )

        Column(modifier = Modifier.fillMaxSize()) {
            modules.forEachIndexed { index, module ->
                val isLeft = index % 2 == 0
                val verticalOffset = when (index) {
                    0 -> 40.dp
                    1 -> 100.dp
                    2 -> 160.dp
                    3 -> 220.dp
                    4 -> 280.dp
                    5 -> 340.dp
                    else -> 400.dp
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = verticalOffset)
                        .padding(horizontal = 24.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF4CAF50),
                        modifier = Modifier
                            .align(if (isLeft) Alignment.CenterStart else Alignment.CenterEnd)
                            .width(180.dp)
                            .height(48.dp)
                    ) {
                        Text(
                            text = "${module.order}. ${module.title}",
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .padding(12.dp)
                                .wrapContentHeight(Alignment.CenterVertically)
                        )
                    }
                }
            }
        }
    }
}
