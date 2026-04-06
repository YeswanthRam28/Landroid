// app/src/main/java/com/landroid/features/settings/presentation/SettingsScreen.kt
package com.landroid.features.settings.presentation

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.landroid.core.navigation.Screen
import com.landroid.core.theme.LandroidColors
import com.landroid.core.theme.LandroidShapes
import com.landroid.core.theme.NewsreaderFont
import com.landroid.core.theme.PlusJakartaSansFont
import com.landroid.shared.components.BottomNavBar
import com.landroid.shared.models.UserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        containerColor = LandroidColors.Surface,
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontFamily = NewsreaderFont, fontSize = 22.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back", tint = LandroidColors.PrimaryContainer)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LandroidColors.Surface)
            )
        },
        bottomBar = {
            BottomNavBar(activeRoute = "settings", onNavigate = { route ->
                when (route) {
                    "parcels" -> navController.navigate(Screen.Parcels.route)
                    "alerts"  -> navController.navigate(Screen.Alerts.route)
                    else -> {}
                }
            })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(horizontal = 24.dp)
        ) {
            // — Language section —
            item { SectionHeader("Language") }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(LandroidShapes.Card)
                        .background(LandroidColors.SurfaceContainerLow)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("en" to "English", "ta" to "தமிழ்").forEach { (tag, label) ->
                        val isSelected = state.selectedLanguage == tag
                        TextButton(
                            onClick = {
                                viewModel.setLanguage(tag)
                                AppCompatDelegate.setApplicationLocales(
                                    LocaleListCompat.forLanguageTags(tag)
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .clip(LandroidShapes.Button)
                                .background(if (isSelected) LandroidColors.PrimaryContainer else Color.Transparent)
                        ) {
                            Text(
                                text = label,
                                fontFamily = PlusJakartaSansFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (isSelected) Color.White else LandroidColors.OnSurface
                            )
                        }
                    }
                }
            }

            // — Geofence section —
            item { SectionHeader("Geofence Alert Buffer") }
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Buffer Distance", fontFamily = PlusJakartaSansFont, fontSize = 14.sp, color = LandroidColors.OnSurface)
                        Text(
                            "${state.geofenceBuffer.toInt()}m",
                            fontFamily = PlusJakartaSansFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = LandroidColors.PrimaryContainer
                        )
                    }
                    Slider(
                        value = state.geofenceBuffer,
                        onValueChange = { viewModel.setGeofenceBuffer(it) },
                        valueRange = 0f..50f,
                        colors = SliderDefaults.colors(
                            thumbColor = LandroidColors.PrimaryContainer,
                            activeTrackColor = LandroidColors.PrimaryContainer,
                            inactiveTrackColor = LandroidColors.SurfaceContainerHigh
                        )
                    )
                }
            }

            // — Cache section —
            item { SectionHeader("Storage & Cache") }
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Used: ${state.cacheUsedMb} MB / ${state.cacheLimitMb} MB", fontFamily = PlusJakartaSansFont, fontSize = 14.sp, color = LandroidColors.OnSurface)
                    }
                    Spacer(Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { state.cacheUsedMb.toFloat() / state.cacheLimitMb },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                        color = LandroidColors.PrimaryContainer,
                        trackColor = LandroidColors.SurfaceContainerHigh
                    )
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = { viewModel.clearCache() }) {
                        Text(
                            "Clear All Cached Data",
                            fontFamily = PlusJakartaSansFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = LandroidColors.Error
                        )
                    }
                }
            }

            // — Account section —
            item { SectionHeader("Account") }
            item {
                state.user?.let { user ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(user.name, fontFamily = PlusJakartaSansFont, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = LandroidColors.OnSurface)
                            Text(user.phone, fontFamily = PlusJakartaSansFont, fontSize = 12.sp, color = LandroidColors.Outline)
                        }
                        Spacer(Modifier.width(8.dp))
                        AssistChip(
                            onClick = {},
                            label = {
                                Text(
                                    if (user.role == UserRole.CONSULTANT) "Consultant" else "Landowner",
                                    fontFamily = PlusJakartaSansFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (user.role == UserRole.CONSULTANT) LandroidColors.PrimaryFixed else LandroidColors.SecondaryContainer
                            )
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    TextButton(
                        onClick = {
                            viewModel.signOut {
                                navController.navigate(Screen.Onboarding.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                    ) {
                        Text(
                            "Log Out",
                            fontFamily = PlusJakartaSansFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = LandroidColors.Error
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        fontFamily = PlusJakartaSansFont,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        color = LandroidColors.Secondary,
        letterSpacing = 0.1.em,
        modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
    )
}
