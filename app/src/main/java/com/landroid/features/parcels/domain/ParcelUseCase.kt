// app/src/main/java/com/landroid/features/parcels/domain/ParcelUseCase.kt
package com.landroid.features.parcels.domain

import com.landroid.features.parcels.data.ParcelRepository
import com.landroid.shared.models.HealthStatus
import com.landroid.shared.models.Parcel
import javax.inject.Inject

class ParcelUseCase @Inject constructor(
    private val repository: ParcelRepository
) {
    suspend fun getParcels() = repository.getParcels()

    suspend fun getParcelById(id: String) = repository.getParcelById(id)

    suspend fun createParcel(parcel: Parcel) = repository.createParcel(parcel)

    fun computeHealthScore(ndvi: Double, rainfall: Double, soilScore: Double, tempScore: Double): Int {
        val score = (ndvi * 100 * 0.40 + rainfall.coerceAtMost(500.0) / 5.0 * 0.30 +
                soilScore * 0.20 + tempScore * 0.10).toInt()
        return score.coerceIn(0, 100)
    }

    fun getHealthStatus(score: Int): HealthStatus = when {
        score >= 70 -> HealthStatus.HEALTHY
        score >= 40 -> HealthStatus.MODERATE
        else -> HealthStatus.AT_RISK
    }
}
