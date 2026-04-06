// app/src/main/java/com/landroid/features/dashboard/presentation/DashboardViewModel.kt
package com.landroid.features.dashboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.landroid.features.dashboard.data.DashboardRepository
import com.landroid.features.parcels.data.ParcelRepository
import com.landroid.shared.models.HealthSignal
import com.landroid.shared.models.Parcel
import com.landroid.shared.models.SignalType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val parcel: Parcel? = null,
    val signals: List<HealthSignal> = emptyList(),
    val healthScore: Int = 0,
    val confidence: Int = 87,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val parcelRepository: ParcelRepository,
    private val dashboardRepository: DashboardRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardUiState())
    val state = _state.asStateFlow()

    fun load(parcelId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val parcelResult = parcelRepository.getParcelById(parcelId)
            val signalsResult = dashboardRepository.getSignals(parcelId)

            val parcel = parcelResult.getOrNull()
            val signals = signalsResult.getOrDefault(emptyList())

            val ndviSignal = signals.firstOrNull { it.type == SignalType.NDVI }
            val rainSignal = signals.firstOrNull { it.type == SignalType.RAINFALL }
            val soilSignal = signals.firstOrNull { it.type == SignalType.SOIL }
            val tempSignal = signals.firstOrNull { it.type == SignalType.TEMPERATURE }

            val ndvi = ndviSignal?.value?.toDoubleOrNull() ?: parcel?.ndvi ?: 0.0
            val rainfall = rainSignal?.value?.toDoubleOrNull()?.times(25.4) ?: parcel?.rainfall ?: 0.0
            val soilScore = (soilSignal?.value?.toDoubleOrNull() ?: 7.0).let { ph ->
                if (ph in 6.0..7.5) 80.0 else 50.0
            }
            val tempScore = (tempSignal?.value?.toDoubleOrNull() ?: 74.0).let {
                if (it in 60.0..85.0) 80.0 else 50.0
            }

            val healthScore = (ndvi * 100 * 0.40 +
                    rainfall.coerceAtMost(500.0) / 5.0 * 0.30 +
                    soilScore * 0.20 +
                    tempScore * 0.10).toInt().coerceIn(0, 100)

            val avgConfidence = if (signals.isEmpty()) 87
            else signals.map { it.confidence }.average().toInt()

            _state.update {
                it.copy(
                    parcel = parcel,
                    signals = signals,
                    healthScore = parcel?.healthScore ?: healthScore,
                    confidence = avgConfidence,
                    isLoading = false
                )
            }
        }
    }
}
