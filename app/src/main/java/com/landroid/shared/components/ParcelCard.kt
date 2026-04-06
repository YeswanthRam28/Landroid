// app/src/main/java/com/landroid/shared/components/ParcelCard.kt
package com.landroid.shared.components

import androidx.compose.material.icons.filled.ChevronRight

import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.landroid.core.theme.LandroidColors
import com.landroid.core.theme.LandroidShapes
import com.landroid.core.theme.NewsreaderFont
import com.landroid.core.theme.PlusJakartaSansFont
import com.landroid.shared.models.HealthStatus
import com.landroid.shared.models.Parcel

@Composable
fun ParcelCard(
    parcel: Parcel,
    onClick: () -> Unit = {}
) {
    val borderColor = when (parcel.healthStatus) {
        HealthStatus.HEALTHY  -> LandroidColors.PrimaryContainer
        HealthStatus.MODERATE -> LandroidColors.AccentAmber
        HealthStatus.AT_RISK  -> LandroidColors.Error
    }

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                shadowElevation = 8f
                shape = LandroidShapes.Card
                clip = false
            }
            .drawBehind {
                drawRect(
                    color = borderColor,
                    size = Size(4.dp.toPx(), size.height)
                )
            },
        shape = LandroidShapes.Card,
        colors = CardDefaults.cardColors(containerColor = LandroidColors.SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            // Row 1: name + status badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = parcel.name,
                    fontFamily = PlusJakartaSansFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = LandroidColors.OnSurface,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                HealthBadge(status = parcel.healthStatus)
            }

            Spacer(Modifier.height(6.dp))

            // Row 2: location
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = LandroidColors.Secondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "${parcel.location}, ${parcel.district}",
                    fontFamily = PlusJakartaSansFont,
                    fontSize = 12.sp,
                    color = LandroidColors.Secondary
                )
            }

            Spacer(Modifier.height(16.dp))

            // Health score section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "LAND HEALTH SCORE",
                    fontFamily = PlusJakartaSansFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = LandroidColors.Outline,
                    letterSpacing = 0.08.sp
                )
                Text(
                    text = "${parcel.healthScore} / 100",
                    fontFamily = NewsreaderFont,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    fontSize = 16.sp,
                    color = LandroidColors.OnSurface
                )
            }

            Spacer(Modifier.height(6.dp))

            // Segmented health score bar
            HealthScoreBar(score = parcel.healthScore)

            Spacer(Modifier.height(12.dp))

            // 3-column signals
            HorizontalDivider(color = LandroidColors.SurfaceContainer.copy(alpha = 0.5f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                SignalColumn(label = "NDVI", value = "%.2f".format(parcel.ndvi), icon = {
                    Icon(Icons.Outlined.Layers, null, tint = LandroidColors.Outline, modifier = Modifier.size(14.dp))
                })
                SignalColumn(label = "RAIN", value = "${parcel.rainfall.toInt()}mm", icon = {
                    Icon(Icons.Outlined.WaterDrop, null, tint = LandroidColors.Outline, modifier = Modifier.size(14.dp))
                })
                SignalColumn(label = "SOIL", value = parcel.soilType, icon = {
                    Icon(Icons.Outlined.Layers, null, tint = LandroidColors.Outline, modifier = Modifier.size(14.dp))
                })
            }
            HorizontalDivider(color = LandroidColors.SurfaceContainer.copy(alpha = 0.5f))

            Spacer(Modifier.height(10.dp))

            // Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Assigned to: ${parcel.assignedTo}",
                    fontFamily = PlusJakartaSansFont,
                    fontStyle = FontStyle.Italic,
                    fontSize = 11.sp,
                    color = LandroidColors.Secondary
                )
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = null,
                    tint = LandroidColors.OnSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun HealthScoreBar(score: Int) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
    ) {
        val total = size.width
        val errorFraction = 0.33f
        val amberFraction = 0.25f
        val scoreFraction = score / 100f
        val cornerRadius = CornerRadius(3f, 3f)

        // Background
        drawRoundRect(
            color = LandroidColors.SurfaceContainerHigh,
            size = size,
            cornerRadius = cornerRadius
        )

        // Error portion
        drawRoundRect(
            color = LandroidColors.Error,
            size = Size(total * minOf(scoreFraction, errorFraction), size.height),
            cornerRadius = cornerRadius
        )

        // Amber portion
        if (scoreFraction > errorFraction) {
            drawRoundRect(
                color = LandroidColors.AccentAmber,
                topLeft = androidx.compose.ui.geometry.Offset(total * errorFraction, 0f),
                size = Size(total * minOf(scoreFraction - errorFraction, amberFraction), size.height),
                cornerRadius = cornerRadius
            )
        }

        // Green portion
        if (scoreFraction > errorFraction + amberFraction) {
            drawRoundRect(
                color = LandroidColors.PrimaryContainer,
                topLeft = androidx.compose.ui.geometry.Offset(total * (errorFraction + amberFraction), 0f),
                size = Size(total * (scoreFraction - errorFraction - amberFraction), size.height),
                cornerRadius = cornerRadius
            )
        }
    }
}

@Composable
private fun SignalColumn(label: String, value: String, icon: @Composable () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            icon()
            Spacer(Modifier.width(3.dp))
            Text(
                text = label,
                fontFamily = PlusJakartaSansFont,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp,
                color = LandroidColors.Outline
            )
        }
        Spacer(Modifier.height(2.dp))
        Text(
            text = value,
            fontFamily = PlusJakartaSansFont,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = LandroidColors.OnSurface
        )
    }
}
