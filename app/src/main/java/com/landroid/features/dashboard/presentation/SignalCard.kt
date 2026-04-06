// app/src/main/java/com/landroid/features/dashboard/presentation/SignalCard.kt
package com.landroid.features.dashboard.presentation

import androidx.compose.foundation.BorderStroke

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.HorizontalRule
import androidx.compose.material.icons.outlined.Opacity
import androidx.compose.material.icons.outlined.Park
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.outlined.Star

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.landroid.core.theme.LandroidColors
import com.landroid.core.theme.LandroidShapes
import com.landroid.core.theme.NewsreaderFont
import com.landroid.core.theme.PlusJakartaSansFont
import com.landroid.shared.models.HealthSignal
import com.landroid.shared.models.SignalType
import com.landroid.shared.models.Trend

@Composable
fun SignalCard(signal: HealthSignal) {
    val (bgTint, iconTint, icon) = when (signal.type) {
        SignalType.NDVI        -> Triple(LandroidColors.Primary.copy(0.05f), LandroidColors.Primary, Icons.Outlined.Park)
        SignalType.RAINFALL    -> Triple(LandroidColors.SecondaryContainer.copy(0.1f), LandroidColors.Secondary, Icons.Outlined.WaterDrop)
        SignalType.TEMPERATURE -> Triple(LandroidColors.TertiaryFixed.copy(0.1f), LandroidColors.Tertiary, Icons.Outlined.Thermostat)
        SignalType.SOIL        -> Triple(Color(0xFFF5F5F4), Color(0xFF78716C), Icons.Outlined.Opacity)
        SignalType.DEVELOPMENT -> Triple(LandroidColors.PrimaryContainer.copy(0.1f), Color(0xFFF39C12), Icons.Outlined.Star)
    }

    val (trendIcon, trendColor) = when (signal.trend) {
        Trend.UP     -> Icons.Outlined.ArrowUpward to LandroidColors.PrimaryContainer
        Trend.DOWN   -> Icons.Outlined.ArrowDownward to LandroidColors.Error
        Trend.STABLE -> Icons.Outlined.HorizontalRule to LandroidColors.Primary
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = LandroidShapes.SmallCard,
        colors = CardDefaults.cardColors(containerColor = LandroidColors.SurfaceContainerLowest),
        border = BorderStroke(1.dp, bgTint),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(bgTint),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
                }
                Icon(imageVector = trendIcon, contentDescription = null, tint = trendColor, modifier = Modifier.size(20.dp))
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = signal.type.name.replace("_", " "),
                fontFamily = PlusJakartaSansFont,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = LandroidColors.OnSurfaceVariant
            )

            Text(
                text = "${signal.value}${signal.unit}",
                fontFamily = NewsreaderFont,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = LandroidColors.OnSurface
            )

            Spacer(Modifier.height(8.dp))

            // Mini sparkline bar chart
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .background(bgTint, RoundedCornerShape(4.dp))
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                val maxVal = signal.historicalData.maxOrNull() ?: 1f
                signal.historicalData.forEach { value ->
                    val fraction = (value / maxVal).coerceIn(0.1f, 1f)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .height((fraction * 24).dp)
                            .background(iconTint.copy(alpha = 0.4f + fraction * 0.4f), RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Confidence
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Confidence:", fontFamily = PlusJakartaSansFont, fontSize = 10.sp, color = LandroidColors.Outline)
                Text("${signal.confidence}%", fontFamily = PlusJakartaSansFont, fontWeight = FontWeight.Bold, fontSize = 10.sp, color = LandroidColors.OnSurface)
            }
            Spacer(Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { signal.confidence / 100f },
                modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                color = iconTint,
                trackColor = LandroidColors.SurfaceContainerHigh
            )
        }
    }
}
