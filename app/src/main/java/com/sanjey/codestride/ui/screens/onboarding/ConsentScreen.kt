package com.sanjey.codestride.ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sanjey.codestride.ui.theme.CustomBlue
import com.sanjey.codestride.ui.theme.PixelFont

@Composable
fun ConsentScreen(
    onAgree: () -> Unit,
    onPrivacyClick: () -> Unit,
    onTermsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome!",
            fontFamily = PixelFont,
            fontSize = 24.sp,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "By continuing, you agree to our Terms & Conditions and Privacy Policy.",
            fontSize = 14.sp,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.Center) {
            TextButton(onClick = onTermsClick) {
                Text("Terms & Conditions", color = CustomBlue, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            TextButton(onClick = onPrivacyClick) {
                Text("Privacy Policy", color = CustomBlue, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onAgree,
            colors = ButtonDefaults.buttonColors(containerColor = CustomBlue),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("I Agree & Continue", color = Color.White, fontSize = 16.sp)
        }
    }
}
