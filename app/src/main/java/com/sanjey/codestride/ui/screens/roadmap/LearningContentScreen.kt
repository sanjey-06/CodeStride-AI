package com.sanjey.codestride.ui.screens.roadmap

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.sanjey.codestride.R
import com.sanjey.codestride.common.UiState
import com.sanjey.codestride.ui.theme.PixelFont
import com.sanjey.codestride.viewmodel.ModuleViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LearningContentScreen(navController: NavController, roadmapId: String, moduleId: String) {
    val viewModel: ModuleViewModel = hiltViewModel()
    val title by viewModel.moduleTitle.collectAsState()


    // ✅ Collect state for HTML content
    val moduleHtmlState by viewModel.moduleHtmlState.collectAsState()

    // ✅ Fetch content on launch
    LaunchedEffect(moduleId) {
        viewModel.fetchModuleContent(roadmapId, moduleId)
        viewModel.fetchModuleDetails(roadmapId, moduleId)

    }

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val bannerHeight = screenHeight * 0.15f




    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // 🔷 Top Banner
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

            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Text(
                text = title.replaceFirstChar { it.uppercase() },
                fontFamily = PixelFont,
                fontSize = 18.sp,
                color = Color.White,
                lineHeight = 20.sp,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 24.dp)
            )
        }

        // ✅ Content Section with UiState handling
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)),
            color = Color.White
        ) {
            when (moduleHtmlState) {
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.Black)
                    }
                }

                is UiState.Success -> {
                    val htmlContent = (moduleHtmlState as UiState.Success<String>).data
                    AndroidView(
                        factory = { context ->
                            WebView(context).apply {
                                settings.javaScriptEnabled = false
                                webViewClient = WebViewClient()
                                val styledHtml = """
    <html>
    <head>
        <style>
            body {
                font-family: sans-serif;
                padding: 16px;
                color: #333333;
                background-color: #FFFFFF;
                line-height: 1.6;
            }
            h1, h2, h3 {
                color: #1E88E5; /* App theme blue */
                font-family: sans-serif;
            }
            img {
                width: 100%;
                max-width: 700px;
                border-radius: 12px;
                margin: 12px 0;
                display: block;
            }
            ul {
                padding-left: 20px;
            }
            li {
                margin-bottom: 8px;
            }
            pre, code {
                background-color: #F4F4F4;
                padding: 8px;
                border-radius: 8px;
                display: block;
                overflow-x: auto;
            }
        </style>
    </head>
    <body>
        $htmlContent
    </body>
    </html>
""".trimIndent()

                                loadDataWithBaseURL(null, styledHtml, "text/html", "UTF-8", null)
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is UiState.Error -> {
                    val errorMessage = (moduleHtmlState as UiState.Error).message
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            fontSize = 16.sp
                        )
                    }
                }
                UiState.Idle, UiState.Empty -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No content available", color = Color.Gray)
                    }
                }
            }

        }
    }
}
