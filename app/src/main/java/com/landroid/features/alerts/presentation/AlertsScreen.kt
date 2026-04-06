// app/src/main/java/com/landroid/features/alerts/presentation/AlertsScreen.kt
package com.landroid.features.alerts.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.FmdBad
import androidx.compose.material.icons.outlined.Grass
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
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
import com.landroid.shared.components.BottomNavBar
import com.landroid.shared.models.Alert
import com.landroid.shared.models.AlertCategory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(
    navController: NavController,
    viewModel: AlertsViewModel = hiltViewModel()
) {
    val alerts by viewModel.alerts.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    Scaffold(
        containerColor = LandroidColors.Surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Alerts",
                        fontFamily = NewsreaderFont,
                        fontSize = 22.sp,
                        color = LandroidColors.OnSurface
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LandroidColors.Surface)
            )
        },
        bottomBar = {
            BottomNavBar(
                activeRoute = "alerts",
                onNavigate = { route ->
                    when (route) {
                        "parcels"  -> navController.navigate(Screen.Parcels.route)
                        "settings" -> navController.navigate(Screen.Settings.route)
                        else -> {}
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Category filter chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { viewModel.selectCategory(null) },
                        label = {
                            Text(
                                "All",
                                fontFamily = PlusJakartaSansFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = LandroidColors.PrimaryContainer,
                            selectedLabelColor = Color.White
                        )
                    )
                }
                items(AlertCategory.entries) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = {
                            viewModel.selectCategory(
                                if (selectedCategory == category) null else category
                            )
                        },
                        label = {
                            Text(
                                category.name.replaceFirstChar { it.uppercase() },
                                fontFamily = PlusJakartaSansFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = LandroidColors.PrimaryContainer,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Alert list
            if (alerts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No alerts",
                        fontFamily = PlusJakartaSansFont,
                        fontSize = 14.sp,
                        color = LandroidColors.Outline
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(alerts, key = { it.id }) { alert ->
                        AlertCard(
                            alert = alert,
                            onRead = { viewModel.markAsRead(alert.id) }
                        )
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
private fun AlertCard(alert: Alert, onRead: () -> Unit) {
    val categoryColor = when (alert.category) {
        AlertCategory.BOUNDARY -> LandroidColors.Error
        AlertCategory.HEALTH   -> LandroidColors.PrimaryContainer
        AlertCategory.INSIGHT  -> LandroidColors.AccentAmber
    }
    val categoryIcon: ImageVector = when (alert.category) {
        AlertCategory.BOUNDARY -> Icons.Outlined.FmdBad
        AlertCategory.HEALTH   -> Icons.Outlined.Grass
        AlertCategory.INSIGHT  -> Icons.Outlined.Lightbulb
    }
    val dateFormat = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(LandroidShapes.Card)
            .background(LandroidColors.SurfaceContainerLowest)
            .then(
                if (!alert.isRead) {
                    Modifier.drawBehind {
                        drawRect(
                            color = categoryColor,
                            size = Size(4.dp.toPx(), size.height)
                        )
                    }
                } else Modifier
            )
            .padding(16.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Category icon circle
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(categoryColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = categoryIcon,
                contentDescription = null,
                tint = categoryColor,
                modifier = Modifier.size(20.dp)
            )
        }

        // Text content
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = alert.title,
                fontFamily = PlusJakartaSansFont,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = LandroidColors.OnSurface
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = alert.description,
                fontFamily = PlusJakartaSansFont,
                fontSize = 12.sp,
                color = LandroidColors.OnSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = dateFormat.format(Date(alert.timestamp)),
                fontFamily = PlusJakartaSansFont,
                fontSize = 11.sp,
                color = LandroidColors.Outline
            )
        }

        // Read indicator
        if (!alert.isRead) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(LandroidColors.PrimaryContainer)
            )
        } else {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = "Read",
                tint = LandroidColors.Outline,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
