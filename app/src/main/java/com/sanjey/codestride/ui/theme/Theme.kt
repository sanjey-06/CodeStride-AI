package com.sanjey.codestride.ui.theme

import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private val customTextSelectionColors = TextSelectionColors(
    handleColor = Color.White,
    backgroundColor = Color.White.copy(alpha = 0.3f)
)

@Composable
fun CodeStrideTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
        MaterialTheme(
            colorScheme = MaterialTheme.colorScheme,
            typography = MaterialTheme.typography,
            shapes = MaterialTheme.shapes,
            content = content
        )
    }
}
