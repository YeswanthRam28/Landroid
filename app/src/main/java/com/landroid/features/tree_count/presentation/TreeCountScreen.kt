// app/src/main/java/com/landroid/features/tree_count/presentation/TreeCountScreen.kt
package com.landroid.features.tree_count.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.landroid.core.theme.NewsreaderFont
import com.landroid.core.theme.PlusJakartaSansFont
import com.landroid.features.map.presentation.MapLibreView
import com.landroid.shared.components.ConfidenceBar
import com.landroid.shared.components.GlassTopBar

@Composable
fun TreeCountScreen(
    parcelId: String,
    navController: NavController,
    viewModel: TreeCountViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(parcelId) { viewModel.load(parcelId) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Map with canopy markers
        MapLibreView(
            parcel = state.parcel,
            activeLayer = "Ortho",
            modifier = Modifier.fillMaxSize()
        )

        // Glass top bar
        GlassTopBar(
            title = "Tree Count",
            onBack = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 48.dp, start = 16.dp, end = 16.dp)
        )

        // Floating count pill
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 112.dp)
                .clip(CircleShape)
                .background(LandroidColors.PrimaryContainer)
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Text(
                text = "${state.totalCount} Trees Detected",
                fontFamily = PlusJakartaSansFont,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.White
            )
        }

        // Bottom info card
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .clip(LandroidShapes.BottomDrawer)
                .background(LandroidColors.Surface)
                .padding(20.dp)
        ) {
            // Total count
            Text(
                text = "${state.totalCount}",
                fontFamily = NewsreaderFont,
                fontWeight = FontWeight.Bold,
                fontSize = 48.sp,
                color = LandroidColors.PrimaryContainer
            )
            Text(
                text = "trees detected",
                fontFamily = PlusJakartaSansFont,
                fontSize = 14.sp,
                color = LandroidColors.OnSurfaceVariant
            )

            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "${"%.1f".format(state.densityPerAcre)} / acre",
                    fontFamily = PlusJakartaSansFont,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = LandroidColors.OnSurface
                )
                Text(
                    text = "${state.stressedCount} stressed",
                    fontFamily = PlusJakartaSansFont,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = LandroidColors.Error
                )
            }

            Spacer(Modifier.height(12.dp))

            // View type toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(LandroidColors.SurfaceContainerHigh)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ViewType.entries.forEach { type ->
                    val isActive = state.viewType == type
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isActive) LandroidColors.PrimaryContainer else Color.Transparent)
                            .clickable { viewModel.setViewType(type) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = type.name.replaceFirstChar { it.uppercase() },
                            fontFamily = PlusJakartaSansFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (isActive) Color.White else LandroidColors.OnSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            ConfidenceBar(confidence = state.confidence)

            Spacer(Modifier.height(8.dp))
        }
    }
}
