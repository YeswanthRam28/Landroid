// app/src/main/java/com/landroid/shared/components/BottomNavBar.kt
package com.landroid.shared.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Landscape
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.landroid.core.theme.LandroidColors
import com.landroid.core.theme.LandroidShapes
import com.landroid.core.theme.PlusJakartaSansFont

data class NavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

@Composable
fun BottomNavBar(
    activeRoute: String,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        NavItem("Map", Icons.Outlined.Map, "map/default"),
        NavItem("Parcels", Icons.Outlined.Landscape, "parcels"),
        NavItem("Alerts", Icons.Outlined.Notifications, "alerts"),
        NavItem("Settings", Icons.Outlined.Settings, "settings")
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 0.dp,
                shape = LandroidShapes.BottomDrawer
            )
            .background(
                color = LandroidColors.Surface.copy(alpha = 0.92f),
                shape = LandroidShapes.BottomDrawer
            )
            .padding(horizontal = 8.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isActive = activeRoute == item.route ||
                    (item.route == "parcels" && activeRoute == "parcels") ||
                    (item.route == "alerts" && activeRoute == "alerts") ||
                    (item.route == "settings" && activeRoute == "settings")

                if (isActive) {
                    Box(
                        modifier = Modifier
                            .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(LandroidColors.PrimaryContainer)
                            .padding(horizontal = 24.dp, vertical = 8.dp)
                            .clickable { onNavigate(item.route) },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = item.label.uppercase(),
                                fontFamily = PlusJakartaSansFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = Color.White,
                                letterSpacing = 0.05.sp
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .clickable { onNavigate(item.route) },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = LandroidColors.OnSurface.copy(alpha = 0.5f),
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = item.label.uppercase(),
                                fontFamily = PlusJakartaSansFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = LandroidColors.OnSurface.copy(alpha = 0.5f),
                                letterSpacing = 0.05.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
