package com.landroid.features.dashboard.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.landroid.features.parcels.presentation.ParcelViewModel
import com.landroid.shared.components.BottomNavBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandownerDashboardScreen(
    navController: NavController,
    viewModel: ParcelViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    // We assume the landowner dashboard picks the first active parcel for them.
    val activeParcel = state.parcels.firstOrNull()

    Scaffold(
        containerColor = LandroidColors.Surface,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Asset Portfolio",
                            fontFamily = NewsreaderFont,
                            fontWeight = FontWeight.Medium,
                            fontSize = 24.sp,
                            color = LandroidColors.OnSurface
                        )
                        Text(
                            text = "Read-Only analytics dashboard",
                            fontFamily = PlusJakartaSansFont,
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp,
                            color = LandroidColors.Outline
                        )
                    }
                },
                actions = {
                    Box {
                        IconButton(
                            onClick = { navController.navigate(Screen.Alerts.route) },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Alerts",
                                tint = LandroidColors.OnSurfaceVariant
                            )
                        }
                    }
                    Spacer(Modifier.width(16.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LandroidColors.Surface)
            )
        },
        bottomBar = {
            BottomNavBar(
                activeRoute = "parcels", // Assuming it conceptually replaces parcels
                onNavigate = { route ->
                    when (route) {
                        "map/default" -> activeParcel?.let { navController.navigate(Screen.Map.createRoute(it.id)) }
                        "alerts"   -> navController.navigate(Screen.Alerts.route)
                        "settings" -> navController.navigate(Screen.Settings.route)
                        else -> {}
                    }
                }
            )
        }
    ) { innerPadding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = LandroidColors.Primary)
            }
        } else if (activeParcel == null) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("No parcels assigned yet.", fontFamily = PlusJakartaSansFont)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Asset Overview & Health Summary
                item {
                    Card(
                        shape = LandroidShapes.Card,
                        colors = CardDefaults.cardColors(containerColor = LandroidColors.SurfaceContainerLowest),
                        border = androidx.compose.foundation.BorderStroke(1.dp, LandroidColors.OutlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(LandroidColors.PrimaryContainer.copy(alpha = 0.1f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = activeParcel.healthScore.toString(),
                                    fontFamily = PlusJakartaSansFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp,
                                    color = LandroidColors.Primary
                                )
                            }
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text("${activeParcel.name}", fontFamily = NewsreaderFont, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                Text("Health Score / ${activeParcel.areaAcres} Acres", fontFamily = PlusJakartaSansFont, fontSize = 12.sp, color = LandroidColors.Secondary)
                            }
                        }
                    }
                }

                // 2. View GIS Map Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().height(160.dp).clickable { navController.navigate(Screen.Map.createRoute(activeParcel.id)) },
                        colors = CardDefaults.cardColors(containerColor = LandroidColors.TertiaryContainer.copy(alpha = 0.4f)),
                        shape = LandroidShapes.Card
                    ) {
                        Box(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                            Column(modifier = Modifier.align(Alignment.BottomStart)) {
                                Icon(Icons.Outlined.Map, contentDescription = null, tint = LandroidColors.OnTertiaryContainer)
                                Spacer(Modifier.height(8.dp))
                                Text("View GIS Map", fontWeight = FontWeight.Bold, fontFamily = PlusJakartaSansFont, fontSize = 18.sp)
                                Text("Orthomosaic, boundary layers", fontSize = 12.sp, color = LandroidColors.OnSurfaceVariant)
                            }
                        }
                    }
                }

                // AI Modules Grid
                item {
                    Text("AI Insights", fontFamily = NewsreaderFont, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        DashboardModuleCard(
                            title = "Land Health",
                            subtitle = "Trends & signals",
                            icon = Icons.Outlined.MonitorHeart,
                            modifier = Modifier.weight(1f),
                            onClick = { navController.navigate(Screen.Dashboard.createRoute(activeParcel.id)) }
                        )

                        DashboardModuleCard(
                            title = "Plant Zones",
                            subtitle = "NDVI classified",
                            icon = Icons.Outlined.Eco,
                            modifier = Modifier.weight(1f),
                            onClick = { navController.navigate(Screen.PlantZones.createRoute(activeParcel.id)) }
                        )
                    }
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        DashboardModuleCard(
                            title = "Tree Count",
                            subtitle = "Canopy detection",
                            icon = Icons.Outlined.Park,
                            modifier = Modifier.weight(1f),
                            onClick = { navController.navigate(Screen.TreeCount.createRoute(activeParcel.id)) }
                        )

                        DashboardModuleCard(
                            title = "Valuation",
                            subtitle = "Estimated ₹ range",
                            icon = Icons.Outlined.Payments,
                            modifier = Modifier.weight(1f),
                            onClick = { navController.navigate(Screen.Valuation.createRoute(activeParcel.id)) }
                        )
                    }
                }
                
                // Documents Vault
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { navController.navigate(Screen.Documents.createRoute(activeParcel.id)) },
                        colors = CardDefaults.cardColors(containerColor = LandroidColors.SurfaceContainer),
                        shape = LandroidShapes.Card
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Folder, contentDescription = null, tint = LandroidColors.Primary)
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text("Document Vault", fontWeight = FontWeight.Bold, fontFamily = PlusJakartaSansFont, fontSize = 16.sp)
                                Text("Patta, FMB, EC access", fontSize = 12.sp, color = LandroidColors.Secondary)
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun DashboardModuleCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.height(110.dp).clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = LandroidColors.SurfaceContainerLowest),
        border = androidx.compose.foundation.BorderStroke(1.dp, LandroidColors.OutlineVariant),
        shape = LandroidShapes.Card
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = LandroidColors.Primary, modifier = Modifier.size(24.dp))
            Spacer(Modifier.weight(1f))
            Text(title, fontFamily = PlusJakartaSansFont, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(subtitle, fontFamily = PlusJakartaSansFont, fontSize = 10.sp, color = LandroidColors.Secondary)
        }
    }
}
