// app/src/main/java/com/landroid/features/parcels/presentation/ParcelListScreen.kt
package com.landroid.features.parcels.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.landroid.core.navigation.Screen
import com.landroid.core.theme.LandroidColors
import com.landroid.core.theme.NewsreaderFont
import com.landroid.core.theme.PlusJakartaSansFont
import com.landroid.shared.components.BottomNavBar
import com.landroid.shared.components.ParcelCard
import com.landroid.shared.components.SearchBar
import com.landroid.shared.components.SummaryStrip
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.ui.draw.clip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParcelListScreen(
    navController: NavController,
    viewModel: ParcelViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        containerColor = LandroidColors.Surface,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (state.userRole == "consultant") "Consultant Admin" else "My Parcels",
                            fontFamily = NewsreaderFont,
                            fontWeight = FontWeight.Medium,
                            fontSize = 22.sp,
                            color = LandroidColors.OnSurface
                        )
                        Text(
                            text = if (state.userRole == "consultant") "Manage and assign boundaries" else "Read-only view",
                            fontFamily = PlusJakartaSansFont,
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp,
                            color = LandroidColors.Outline
                        )
                    }
                },
                actions = {
                    // Bell with green dot badge
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
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .align(Alignment.TopEnd)
                                .offset(x = (-6).dp, y = 6.dp)
                                .clip(CircleShape)
                                .background(LandroidColors.PrimaryContainer)
                        )
                    }
                    Spacer(Modifier.width(4.dp))
                    // Initials avatar
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(LandroidColors.PrimaryFixed),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (state.userRole == "consultant") "AD" else "LO",
                            fontFamily = PlusJakartaSansFont,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = LandroidColors.OnPrimaryFixedVariant
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LandroidColors.Surface
                )
            )
        },
        floatingActionButton = {
            if (state.userRole == "consultant") {
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.ParcelCreate.route) },
                    shape = CircleShape,
                    containerColor = LandroidColors.PrimaryContainer,
                    contentColor = Color.White
                ) {
                    Icon(imageVector = Icons.Outlined.Add, contentDescription = "Add Parcel")
                }
            }
        },
        bottomBar = {
            BottomNavBar(
                activeRoute = "parcels",
                onNavigate = { route ->
                    when (route) {
                        "alerts"   -> navController.navigate(Screen.Alerts.route)
                        "settings" -> navController.navigate(Screen.Settings.route)
                        else -> {}
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Search bar
            item {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { q ->
                        searchQuery = q
                        viewModel.search(q)
                    }
                )
            }

            // Summary strip
            item {
                val atRisk = state.parcels.count { it.healthScore < 40 }
                SummaryStrip(
                    parcelCount = state.parcels.size,
                    atRiskCount = atRisk,
                    alertCount = 2
                )
            }

            // Parcel cards
            items(state.filteredParcels, key = { it.id }) { parcel ->
                ParcelCard(
                    parcel = parcel,
                    onClick = {
                        viewModel.selectParcel(parcel)
                        navController.navigate(Screen.Map.createRoute(parcel.id))
                    }
                )
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}
