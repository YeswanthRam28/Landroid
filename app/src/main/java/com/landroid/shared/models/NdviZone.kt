// app/src/main/java/com/landroid/shared/models/NdviZone.kt
package com.landroid.shared.models

import androidx.compose.ui.graphics.Color

data class NdviZone(
    val id: String,
    val color: Color,
    val min: Float,
    val max: Float,
    val areaPercent: Float = 0f
)
