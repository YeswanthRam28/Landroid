// app/src/main/java/com/landroid/features/auth/presentation/OnboardingScreen.kt
package com.landroid.features.auth.presentation

import androidx.compose.foundation.BorderStroke

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.landroid.core.navigation.Screen
import com.landroid.core.theme.LandroidColors
import com.landroid.core.theme.NewsreaderFont
import com.landroid.core.theme.PlusJakartaSansFont
import kotlin.math.sin

@Composable
fun OnboardingScreen(navController: NavController) {
    val infiniteTransition = rememberInfiniteTransition(label = "topo")
    val offsetX by infiniteTransition.animateFloat(
        initialValue = -30f,
        targetValue = 30f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "topoOffsetX"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LandroidColors.Surface)
    ) {
        // Topographic background canvas
        TopoBackgroundCanvas(offsetX = offsetX)

        // Center brand cluster
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Landroid",
                fontFamily = NewsreaderFont,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                fontSize = 36.sp,
                color = LandroidColors.PrimaryContainer
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "AI LAND INTELLIGENCE",
                fontFamily = PlusJakartaSansFont,
                fontSize = 14.sp,
                letterSpacing = 0.05.em,
                color = Color(0xFF4A5568)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Spacer(
                modifier = Modifier
                    .width(96.dp)
                    .height(1.dp)
                    .background(LandroidColors.PrimaryContainer.copy(alpha = 0.2f))
            )
        }

        // Bottom action buttons
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { navController.navigate(Screen.Otp.createRoute("consultant")) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = LandroidColors.PrimaryContainer,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "I'm a Land Consultant",
                    fontFamily = PlusJakartaSansFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { navController.navigate(Screen.Otp.createRoute("landowner")) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = CircleShape,
                border = BorderStroke(2.dp, LandroidColors.PrimaryContainer),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = LandroidColors.PrimaryContainer
                )
            ) {
                Text(
                    text = "I'm a Landowner",
                    fontFamily = PlusJakartaSansFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Role selection is permanent after sign-up",
                fontFamily = PlusJakartaSansFont,
                fontSize = 11.sp,
                color = Color(0xFF4A5568),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun TopoBackgroundCanvas(offsetX: Float) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val waveCount = 5
        val paint = androidx.compose.ui.graphics.Paint().apply {
            color = LandroidColors.PrimaryContainer.copy(alpha = 0.08f)
            strokeWidth = 1f
        }

        repeat(waveCount) { i ->
            val path = Path()
            val yBase = height * (0.3f + i * 0.12f)
            path.moveTo(0f + offsetX, yBase)

            var x = 0f
            while (x <= width + 100f) {
                val y = yBase + sin((x + offsetX) / (width / 3f) * Math.PI.toFloat()) * 30f
                path.lineTo(x + offsetX, y)
                x += 4f
            }
            drawPath(
                path = path,
                color = LandroidColors.PrimaryContainer.copy(alpha = 0.08f),
                style = Stroke(width = 1f)
            )
        }
    }
}
