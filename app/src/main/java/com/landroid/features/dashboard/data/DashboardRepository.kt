// app/src/main/java/com/landroid/features/dashboard/data/DashboardRepository.kt
package com.landroid.features.dashboard.data

import com.landroid.shared.models.HealthSignal
import com.landroid.shared.models.SignalType
import com.landroid.shared.models.Trend
import com.landroid.core.network.LandroidApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface DashboardRepository {
    suspend fun getSignals(parcelId: String): Result<List<HealthSignal>>
}

// Default impl with sample data + future API hookup
class DashboardRepositoryImpl @javax.inject.Inject constructor(
    private val apiService: LandroidApiService
) : DashboardRepository {
    override suspend fun getSignals(parcelId: String): Result<List<HealthSignal>> = withContext(Dispatchers.IO) {
        runCatching {
            apiService.getSignals(parcelId)
        }
    }
}
