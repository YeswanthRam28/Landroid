// app/src/main/java/com/landroid/features/dashboard/presentation/DashboardScreen.kt
package com.landroid.features.dashboard.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.landroid.core.theme.LandroidColors
import com.landroid.core.theme.NewsreaderFont
import com.landroid.core.theme.PlusJakartaSansFont
import com.landroid.shared.components.ConfidenceBar
import com.landroid.shared.models.HealthStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    parcelId: String,
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(parcelId) { viewModel.load(parcelId) }

    Scaffold(
        containerColor = LandroidColors.Surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Land Health",
                        fontFamily = NewsreaderFont,
                        fontStyle = FontStyle.Italic,
                        fontSize = 24.sp,
                        color = LandroidColors.OnSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back", tint = LandroidColors.PrimaryContainer)
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Outlined.Search, "Search", tint = LandroidColors.OnSurface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LandroidColors.Surface)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Gauge
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    HealthGaugeCanvas(
                        score = state.healthScore,
                        status = state.parcel?.healthStatus ?: HealthStatus.MODERATE
                    )

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = state.parcel?.name ?: "Loading…",
                        fontFamily = NewsreaderFont,
                        fontStyle = FontStyle.Italic,
                        fontSize = 18.sp,
                        color = LandroidColors.OnSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "LAT: ${state.parcel?.centroidLat ?: "—"}  /  LNG: ${state.parcel?.centroidLng ?: "—"}",
                        fontFamily = PlusJakartaSansFont,
                        fontWeight = FontWeight.Normal,
                        fontSize = 10.sp,
                        color = LandroidColors.Outline,
                        letterSpacing = 0.08.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Signal grid (2 columns, fixed height via item)
            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(520.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    userScrollEnabled = false
                ) {
                    items(state.signals) { signal ->
                        SignalCard(signal = signal)
                    }
                }
            }

            // Confidence bar
            item {
                ConfidenceBar(confidence = state.confidence)
            }

            item { Spacer(Modifier.height(32.dp)) }
        }
    }
}
