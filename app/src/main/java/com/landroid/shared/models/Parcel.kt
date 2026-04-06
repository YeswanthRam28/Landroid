// app/src/main/java/com/landroid/shared/models/Parcel.kt
package com.landroid.shared.models

data class Parcel(
    val id: String,
    val name: String,
    val location: String,
    val district: String,
    val areaAcres: Double,
    val healthScore: Int,
    val healthStatus: HealthStatus,
    val ndvi: Double,
    val rainfall: Double,
    val soilType: String,
    val assignedTo: String,
    val boundaryGeoJson: String,
    val centroidLat: Double,
    val centroidLng: Double,
    val createdAt: Long
)

enum class HealthStatus { HEALTHY, MODERATE, AT_RISK }
