package com.sanjey.codestride.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sanjey.codestride.ui.theme.PixelFont
import com.sanjey.codestride.ui.theme.SoraFont
import kotlinx.coroutines.launch

@Composable
fun RoadmapReplaceDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: suspend () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { if (!isLoading) onDismiss() },
            title = {
                Text(
                    text = "Replace Roadmap?",
                    fontFamily = PixelFont,
                    fontSize = 18.sp,
                    color = Color.White
                )
            },
            text = {
                Text(
                    text = "You already have an active roadmap. Starting a new one will reset your progress. Do you want to continue?",
                    fontFamily = SoraFont,
                    fontSize = 14.sp,
                    color = Color.White
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        isLoading = true
                        coroutineScope.launch {
                            try {
                                onConfirm() // ✅ Suspend function called safely
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(50.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.Black,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(16.dp)
                        )
                    } else {
                        Text(
                            text = "Yes",
                            fontFamily = PixelFont,
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { if (!isLoading) onDismiss() }) {
                    Text(
                        text = "No",
                        fontFamily = PixelFont,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            },
            containerColor = Color.Black,
            shape = RoundedCornerShape(20.dp)
        )
    }
}
