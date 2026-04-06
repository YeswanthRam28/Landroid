// app/src/main/java/com/landroid/shared/models/Canopy.kt
package com.landroid.shared.models

data class Canopy(
    val lat: Double,
    val lng: Double,
    val radiusMeters: Float,
    val isStressed: Boolean
)
