// app/src/main/java/com/landroid/features/map/presentation/GisMapScreen.kt
package com.landroid.features.map.presentation

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
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.outlined.Park
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.landroid.core.navigation.Screen
import com.landroid.core.theme.LandroidColors
import com.landroid.core.theme.LandroidShapes
import com.landroid.core.theme.NewsreaderFont
import com.landroid.core.theme.PlusJakartaSansFont
import com.landroid.shared.components.GlassTopBar
import com.landroid.shared.components.LayerToggleRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GisMapScreen(
    parcelId: String,
    navController: NavController,
    viewModel: MapViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scaffoldState = rememberBottomSheetScaffoldState()

    LaunchedEffect(parcelId) { viewModel.loadParcel(parcelId) }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 180.dp,
        sheetShape = LandroidShapes.BottomDrawer,
        containerColor = Color.Transparent,
        sheetContainerColor = LandroidColors.Surface,
        sheetContent = {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                // Pull tab
                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .height(6.dp)
                        .align(Alignment.CenterHorizontally)
                        .clip(CircleShape)
                        .background(LandroidColors.SurfaceContainerHighest)
                )
                Spacer(Modifier.height(12.dp))

                // Parcel title
                val parcel = state.parcel
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = parcel?.name ?: "Loading…",
                        fontFamily = NewsreaderFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = LandroidColors.OnSurface,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${parcel?.areaAcres ?: "—"} ACRES",
                    fontFamily = PlusJakartaSansFont,
                    fontSize = 14.sp,
                    color = LandroidColors.Outline
                )
                Spacer(Modifier.height(16.dp))

                // Quick action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionButton(
                        label = "Dashboard",
                        icon = Icons.Outlined.Dashboard,
                        onClick = { navController.navigate(Screen.Dashboard.createRoute(parcelId)) },
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionButton(
                        label = "Trees",
                        icon = Icons.Outlined.Park,
                        onClick = { navController.navigate(Screen.TreeCount.createRoute(parcelId)) },
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionButton(
                        label = "Docs",
                        icon = Icons.Outlined.Description,
                        onClick = { navController.navigate(Screen.Documents.createRoute(parcelId)) },
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = { navController.navigate(Screen.PlantZones.createRoute(parcelId)) },
                        colors = ButtonDefaults.buttonColors(containerColor = LandroidColors.PrimaryContainer),
                        shape = LandroidShapes.Button,
                        modifier = Modifier.height(64.dp)
                    ) {
                        Text("Analyze", fontFamily = PlusJakartaSansFont, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
                Spacer(Modifier.height(20.dp))
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Full-screen map
            MapLibreView(
                parcel = state.parcel,
                activeLayer = state.activeLayer,
                modifier = Modifier.fillMaxSize()
            )

            // Glass top bar
            GlassTopBar(
                title = state.parcel?.name ?: "",
                onBack = { navController.popBackStack() },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 48.dp, start = 16.dp, end = 16.dp)
            )

            // Layer toggle row
            LayerToggleRow(
                layers = MAP_LAYERS,
                activeLayer = state.activeLayer,
                onLayerSelect = { viewModel.toggleLayer(it) },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 112.dp)
            )

            // Health score badge — top end
            state.parcel?.let { parcel ->
                Column(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 160.dp, end = 16.dp)
                        .clip(LandroidShapes.SmallCard)
                        .background(LandroidColors.SurfaceContainerLowest.copy(alpha = 0.85f))
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "HEALTH SCORE",
                        fontFamily = PlusJakartaSansFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = LandroidColors.Outline
                    )
                    Text(
                        text = "${parcel.healthScore}",
                        fontFamily = NewsreaderFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp,
                        color = LandroidColors.PrimaryContainer
                    )
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(LandroidColors.TertiaryContainer.copy(alpha = 0.1f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = parcel.healthStatus.name,
                            fontFamily = PlusJakartaSansFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = LandroidColors.OnTertiaryContainer
                        )
                    }
                }
            }

            // Map tool buttons — center end
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MapToolButton(icon = Icons.Outlined.Layers, contentDescription = "Layers", onClick = {})
                MapToolButton(icon = Icons.Outlined.MyLocation, contentDescription = "Location", onClick = {})
            }
        }
    }
}

@Composable
private fun GlassTopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    com.landroid.shared.components.GlassTopBar(
        title = title,
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
private fun MapToolButton(icon: ImageVector, contentDescription: String, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(48.dp)
            .clip(LandroidShapes.ToolButton)
            .background(LandroidColors.SurfaceContainerLowest.copy(alpha = 0.9f))
    ) {
        Icon(imageVector = icon, contentDescription = contentDescription, tint = LandroidColors.PrimaryContainer)
    }
}

@Composable
private fun QuickActionButton(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(LandroidShapes.Button)
            .background(LandroidColors.SurfaceContainerLow)
            .padding(12.dp)
            .height(64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IconButton(onClick = onClick, modifier = Modifier.size(28.dp)) {
            Icon(imageVector = icon, contentDescription = label, tint = LandroidColors.PrimaryContainer)
        }
        Text(
            text = label.uppercase(),
            fontFamily = PlusJakartaSansFont,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            color = LandroidColors.OnSurface
        )
    }
}
