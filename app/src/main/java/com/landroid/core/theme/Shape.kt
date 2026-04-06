// app/src/main/java/com/landroid/core/theme/Shape.kt
package com.landroid.core.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

object LandroidShapes {
    val Card = RoundedCornerShape(16.dp)
    val SmallCard = RoundedCornerShape(14.dp)
    val Chip = RoundedCornerShape(50.dp)
    val Button = RoundedCornerShape(12.dp)
    val BottomDrawer = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    val OtpBox = RoundedCornerShape(10.dp)
    val Badge = RoundedCornerShape(6.dp)
    val ToolButton = RoundedCornerShape(12.dp)
}
