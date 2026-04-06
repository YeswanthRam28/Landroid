// app/src/main/java/com/landroid/shared/models/Alert.kt
package com.landroid.shared.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alerts")
data class Alert(
    @PrimaryKey val id: String,
    val parcelId: String,
    val title: String,
    val description: String,
    val category: AlertCategory,
    val isRead: Boolean,
    val timestamp: Long
)

enum class AlertCategory { BOUNDARY, HEALTH, INSIGHT }
