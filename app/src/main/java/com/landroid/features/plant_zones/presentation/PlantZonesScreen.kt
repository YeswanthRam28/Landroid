// app/src/main/java/com/landroid/features/plant_zones/presentation/PlantZonesScreen.kt
package com.landroid.features.plant_zones.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.landroid.core.theme.LandroidColors
import com.landroid.core.theme.LandroidShapes
import com.landroid.core.theme.PlusJakartaSansFont
import com.landroid.features.map.presentation.MapLibreView
import com.landroid.shared.components.GlassTopBar
import com.landroid.shared.models.NdviZone

@Composable
fun PlantZonesScreen(
    parcelId: String,
    navController: NavController,
    viewModel: PlantZonesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(parcelId) { viewModel.load(parcelId) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Full-screen map base (NDVI layers would be applied via MapLibre FillLayers)
        MapLibreView(
            parcel = state.parcel,
            activeLayer = "NDVI",
            modifier = Modifier.fillMaxSize()
        )

        // Glass top bar
        GlassTopBar(
            title = "Plant Zones",
            onBack = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 48.dp, start = 16.dp, end = 16.dp)
        )

        // Bottom info card
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .clip(LandroidShapes.BottomDrawer)
                .background(LandroidColors.Surface)
                .padding(20.dp)
        ) {
            // Stress warning
            if (state.stressedZoneIncreased) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(LandroidColors.TertiaryFixed.copy(alpha = 0.3f))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Warning,
                        contentDescription = null,
                        tint = LandroidColors.OnTertiaryFixedVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Stressed zone increased by 4% since last month. Consider soil moisture intervention.",
                        fontFamily = PlusJakartaSansFont,
                        fontSize = 12.sp,
                        color = LandroidColors.OnTertiaryFixedVariant
                    )
                }
                Spacer(Modifier.height(12.dp))
            }

            // Zone legend rows
            state.zones.forEach { zone -> ZoneLegendRow(zone = zone) }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ZoneLegendRow(zone: NdviZone) {
    val label = zone.id.replaceFirstChar { it.uppercase() }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(zone.color)
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = label,
            fontFamily = PlusJakartaSansFont,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = LandroidColors.OnSurface,
            modifier = Modifier.width(64.dp)
        )
        LinearProgressIndicator(
            progress = { zone.areaPercent / 100f },
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(CircleShape),
            color = zone.color,
            trackColor = LandroidColors.SurfaceContainerHigh
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = "${zone.areaPercent.toInt()}%",
            fontFamily = PlusJakartaSansFont,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = LandroidColors.OnSurface
        )
    }
}
