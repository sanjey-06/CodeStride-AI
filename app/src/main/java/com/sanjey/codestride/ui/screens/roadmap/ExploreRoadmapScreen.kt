package com.sanjey.codestride.ui.screens.roadmap

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.navigation.NavController
import com.sanjey.codestride.R
import com.sanjey.codestride.ui.theme.CustomBlue
import com.sanjey.codestride.ui.theme.PixelFont
import com.sanjey.codestride.ui.theme.SoraFont

data class StaticRoadmap(
    val id: String,
    val title: String,
    val icon: Int,
    val description: String
)

val staticRoadmaps = listOf(
    StaticRoadmap("java", "Java", R.drawable.ic_java, "Java is a powerful, object-oriented language used for backend systems, Android apps, and enterprise solutions."),
    StaticRoadmap("python", "Python", R.drawable.ic_python, "Python is an easy-to-learn language used for automation, data science, and web development."),
    StaticRoadmap("cpp", "C++", R.drawable.ic_cpp, "C++ is a high-performance language used in games, embedded systems, and competitive programming."),
    StaticRoadmap("kotlin", "Kotlin", R.drawable.ic_kotlin, "Kotlin is a modern, expressive language used for Android development and server-side applications."),
    StaticRoadmap("js", "JavaScript", R.drawable.ic_javascript, "JavaScript powers interactive web pages and is the backbone of frontend frameworks and web apps.")
)

@Composable
fun ExploreRoadmapsScreen(navController: NavController) {
    var selectedRoadmap by remember { mutableStateOf<StaticRoadmap?>(null) }
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val bannerHeight = screenHeight * 0.15f
    var showDialog by remember { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // 🔷 Top Banner with Centered Heading + Back Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(bannerHeight)
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

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier
                    .padding(16.dp)
                    .size(24.dp)
                    .align(Alignment.TopStart)
                    .clickable { navController.popBackStack() }
            )

            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Explore Roadmaps",
                    fontFamily = PixelFont,
                    fontSize = 22.sp,
                    color = Color.White
                )
            }
        }

        // 🔲 Grid Section
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)),
            color = Color.White
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(28.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(
                    staticRoadmaps,
                    span = { index, _ ->
                        val isLastItem = index == staticRoadmaps.lastIndex && staticRoadmaps.size % 2 != 0
                        if (isLastItem) GridItemSpan(2) else GridItemSpan(1)
                    }
                ) { _, roadmap ->
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        RoadmapIconCard(roadmap) { selectedRoadmap = roadmap }
                    }
                }
                item(span = { GridItemSpan(2) }) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Want to generate your own roadmap?",
                            fontFamily = SoraFont,
                            fontSize = 14.sp,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { showDialog = true },
                            shape = RoundedCornerShape(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CustomBlue)
                        ) {
                            Text(
                                text = "Generate with AI",
                                fontFamily = PixelFont,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }

        }
    }


    // 🔳 Pop-Up Dialog When a Card is Clicked
    if (selectedRoadmap != null) {
        AlertDialog(
            onDismissRequest = { selectedRoadmap = null },
            confirmButton = {
                Button(
                    onClick = {
                        navController.navigate("learning/${selectedRoadmap!!.id}")
                        selectedRoadmap = null
                    },
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text(
                        text = "Start",
                        fontFamily = PixelFont,
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }
            },
            title = {
                Text(
                    text = "Introduction to ${selectedRoadmap!!.title}",
                    fontFamily = PixelFont,
                    fontSize = 18.sp,
                    color = Color.White
                )
            },
            text = {
                Text(
                    text = selectedRoadmap!!.description,
                    fontFamily = SoraFont,
                    fontSize = 14.sp,
                    color = Color.White
                )
            },
            containerColor = Color.Black,
            shape = RoundedCornerShape(20.dp)
        )
    }
    if (showDialog) {
        GenerateRoadmapDialog(
            onDismiss = { showDialog = false },
            navController = navController
        )
    }
}


@Composable
fun RoadmapIconCard(roadmap: StaticRoadmap, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(160.dp)
            .clickable { onClick() }
    ) {
        Surface(
            modifier = Modifier.size(120.dp),
            shape = RoundedCornerShape(20.dp),
            color = Color.Black
        ) {
            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = roadmap.icon),
                    contentDescription = roadmap.title,
                    modifier = Modifier.size(100.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = roadmap.title,
            fontFamily = PixelFont,
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}
