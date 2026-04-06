// app/src/main/java/com/landroid/features/dashboard/presentation/HealthGaugeCanvas.kt
package com.landroid.features.dashboard.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.landroid.core.theme.LandroidColors
import com.landroid.core.theme.NewsreaderFont
import com.landroid.core.theme.PlusJakartaSansFont
import com.landroid.shared.models.HealthStatus

@Composable
fun HealthGaugeCanvas(
    score: Int,
    status: HealthStatus,
    modifier: Modifier = Modifier
) {
    var animatedTarget by remember { mutableFloatStateOf(0f) }
    val animatedSweep by animateFloatAsState(
        targetValue = animatedTarget,
        animationSpec = tween(durationMillis = 1000),
        label = "gaugeSweep"
    )

    LaunchedEffect(score) {
        animatedTarget = score / 100f * 270f
    }

    val statusLabel = when (status) {
        HealthStatus.HEALTHY  -> "HEALTHY"
        HealthStatus.MODERATE -> "MODERATE"
        HealthStatus.AT_RISK  -> "AT RISK"
    }

    Box(
        modifier = modifier.size(240.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(240.dp)) {
            val strokeWidth = 32.dp.toPx()
            val padding = strokeWidth / 2f
            val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
            val topLeft = Offset(padding, padding)
            val startAngle = 135f

            // Track arc
            drawArc(
                color = LandroidColors.SurfaceContainerHigh,
                startAngle = startAngle,
                sweepAngle = 270f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Animated fill arc with sweep gradient
            if (animatedSweep > 0f) {
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            LandroidColors.Error,
                            LandroidColors.AccentAmber,
                            LandroidColors.PrimaryContainer
                        ),
                        center = Offset(size.width / 2, size.height / 2)
                    ),
                    startAngle = startAngle,
                    sweepAngle = animatedSweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
        }

        // Center text overlay
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$score",
                fontFamily = NewsreaderFont,
                fontWeight = FontWeight.Bold,
                fontSize = 48.sp,
                color = LandroidColors.PrimaryContainer,
                lineHeight = 52.sp
            )
            Text(
                text = statusLabel,
                fontFamily = PlusJakartaSansFont,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = LandroidColors.TertiaryContainer,
                letterSpacing = 0.05.sp
            )
        }
    }
}
