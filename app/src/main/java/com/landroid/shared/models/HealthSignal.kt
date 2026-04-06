// app/src/main/java/com/landroid/shared/models/HealthSignal.kt
package com.landroid.shared.models

data class HealthSignal(
    val type: SignalType,
    val value: String,
    val unit: String,
    val trend: Trend,
    val historicalData: List<Float>,
    val confidence: Int
)

enum class SignalType { NDVI, RAINFALL, TEMPERATURE, SOIL }
enum class Trend { UP, DOWN, STABLE }
