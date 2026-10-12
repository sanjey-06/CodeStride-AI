package com.sanjey.codestride.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sanjey.codestride.ui.theme.CustomBlue
import com.sanjey.codestride.ui.theme.PixelFont
import com.sanjey.codestride.ui.theme.SoraFont
import com.sanjey.codestride.viewmodel.UsageViewModel

@Composable
fun AiGeneratorSection(onGenerate: (String) -> Unit) {
    var showPromptDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Text(
            text = "🚀 Want a custom learning path?",
            fontFamily = PixelFont,
            fontSize = 16.sp,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Let AI generate a roadmap just for you!",
            fontFamily = SoraFont,
            fontSize = 14.sp,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = { showPromptDialog = true },
            shape = RoundedCornerShape(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CustomBlue),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Generate with AI",
                fontFamily = PixelFont,
                fontSize = 14.sp,
                color = Color.White
            )
        }
    }

    if (showPromptDialog) {
        GenerateAiPromptDialog(
            onDismiss = { showPromptDialog = false },
            onGenerate = { topic ->
                onGenerate(topic)
                showPromptDialog = false
            }
        )
    }
}

@Composable
private fun GenerateAiPromptDialog(
    onDismiss: () -> Unit,
    onGenerate: (String) -> Unit
) {
    var topic by remember { mutableStateOf("") }

    // Remaining AI roadmaps today (loaded each time the popup opens)
    val usageViewModel: UsageViewModel = hiltViewModel()
    val quota by usageViewModel.roadmapQuota.collectAsState()
    LaunchedEffect(Unit) { usageViewModel.refresh() }
    val limitReached = quota?.remaining == 0

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            // "3 of 5 AI roadmaps left today" / "Daily limit reached · resets at 1:00 AM"
            quota?.let { q ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (q.remaining > 0)
                        "${q.remaining} of ${q.limit} AI roadmaps left today"
                    else
                        "Daily limit reached · resets at ${q.resetTime}",
                    fontFamily = SoraFont,
                    fontSize = 12.sp,
                    color = if (q.remaining > 0) Color.White.copy(alpha = 0.7f) else Color(0xFFFF8A80)
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", fontFamily = PixelFont, color = Color.White)
            }
        },
        title = {
            Text(
                text = "🚀 Generate AI Roadmap",
                fontFamily = PixelFont,
                fontSize = 18.sp,
                color = Color.White
            )
        },
        text = {
            Column {
                Text(
                    text = "Enter a topic you're interested in:",
                    fontFamily = SoraFont,
                    fontSize = 14.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = topic,
                    onValueChange = { topic = it },
                    label = { Text("Enter only Topic") },
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(color = Color.White)
                )

                // "3 of 5 AI roadmaps left today" / "Daily limit reached · resets at 1:00 AM"
                quota?.let { q ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (q.remaining > 0)
                            "${q.remaining} of ${q.limit} AI roadmaps left today"
                        else
                            "Daily limit reached · resets at ${q.resetTime}",
                        fontFamily = SoraFont,
                        fontSize = 12.sp,
                        color = if (q.remaining > 0) Color.White.copy(alpha = 0.7f) else Color(0xFFFF8A80)
                    )
                }
            }
        },
        containerColor = Color.Black,
        shape = RoundedCornerShape(20.dp)
    )
}
