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

data class StaticCareer(
    val id: String,
    val title: String,
    val icon: Int,
    val description: String,
    val roles: List<String>
)

val staticCareers = listOf(
    StaticCareer(
        id = "meta",
        title = "Meta",
        icon = R.drawable.ic_meta,
        description = "Explore opportunities at Meta in AR/VR, AI, and large-scale social platforms.",
        roles = listOf("Software Engineer", "QA Engineer", "Frontend Developer", "Backend Developer", "Data Analyst")
    ),
    StaticCareer(
        id = "apple",
        title = "Apple",
        icon = R.drawable.ic_apple,
        description = "Dive into Apple’s world of innovative hardware, iOS development, and elegant design.",
        roles = listOf("Software Engineer", "QA Engineer", "Frontend Developer", "Backend Developer", "Data Analyst")
    ),
    StaticCareer(
        id = "amazon",
        title = "Amazon",
        icon = R.drawable.ic_amazon,
        description = "Build scalable systems and contribute to global e-commerce, AWS, and AI products.",
        roles = listOf("Software Engineer", "QA Engineer", "Frontend Developer", "Backend Developer", "Data Analyst")
    ),
    StaticCareer(
        id = "netflix",
        title = "Netflix",
        icon = R.drawable.ic_netflix,
        description = "Create engaging digital experiences in entertainment, streaming optimization, and data science.",
        roles = listOf("Software Engineer", "QA Engineer", "Frontend Developer", "Backend Developer", "Data Analyst")
    ),
    StaticCareer(
        id = "google",
        title = "Google",
        icon = R.drawable.ic_google,
        description = "Innovate with machine learning, search, Android, and cutting-edge cloud infrastructure.",
        roles = listOf("Software Engineer", "QA Engineer", "Frontend Developer", "Backend Developer", "Data Analyst")
    )
)

@Composable
fun ExploreCareerScreen(navController: NavController) {
    var selectedCareer by remember { mutableStateOf<StaticCareer?>(null) }
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val bannerHeight = screenHeight * 0.15f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // 🔷 Banner
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
                    text = "Explore Careers",
                    fontFamily = PixelFont,
                    fontSize = 22.sp,
                    color = Color.White
                )
            }
        }

        // 🔲 Grid
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
                    staticCareers,
                    span = { index, _ ->
                        val isLastItem = index == staticCareers.lastIndex && staticCareers.size % 2 != 0
                        if (isLastItem) GridItemSpan(2) else GridItemSpan(1)
                    }
                ) { _, career ->
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CareerIconCard(career) { selectedCareer = career }
                    }
                }
            }
        }
    }

    // 🔳 Dialog
    if (selectedCareer != null) {
        AlertDialog(
            onDismissRequest = { selectedCareer = null },
            confirmButton = {},
            title = {
                Text(
                    text = "Explore ${selectedCareer!!.title}",
                    fontFamily = PixelFont,
                    fontSize = 18.sp,
                    color = Color.White
                )
            },
            text = {
                Column {
                    Text(
                        text = selectedCareer!!.description,
                        fontFamily = SoraFont,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    selectedCareer!!.roles.forEach { role ->
                        Button(
                            onClick = {
                                navController.navigate("learning/${selectedCareer!!.id}")
                                selectedCareer = null
                            },
                            shape = RoundedCornerShape(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CustomBlue),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = role,
                                fontFamily = PixelFont,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            },
            containerColor = Color.Black,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
fun CareerIconCard(career: StaticCareer, onClick: () -> Unit) {
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
                    painter = painterResource(id = career.icon),
                    contentDescription = career.title,
                    modifier = Modifier.size(100.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = career.title,
            fontFamily = PixelFont,
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}
