// app/src/main/java/com/landroid/features/valuation/presentation/ValuationViewModel.kt
package com.landroid.features.valuation.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.landroid.features.parcels.data.ParcelRepository
import com.landroid.shared.models.Parcel
import com.landroid.shared.models.ValuationFactor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ValuationBand(
    val label: String,
    val lowValue: String,
    val highValue: String,
    val isFeatured: Boolean = false
)

data class ValuationUiState(
    val parcel: Parcel? = null,
    val estimatedValue: String = "₹0",
    val bands: List<ValuationBand> = emptyList(),
    val factors: List<ValuationFactor> = emptyList(),
    val confidence: Int = 72,
    val isLoading: Boolean = false
)

@HiltViewModel
class ValuationViewModel @Inject constructor(
    private val repository: ParcelRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ValuationUiState())
    val state = _state.asStateFlow()

    fun load(parcelId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repository.getParcelById(parcelId).fold(
                onSuccess = { parcel ->
                    val (bands, factors, estimate) = computeValuation(parcel)
                    _state.update {
                        it.copy(
                            parcel = parcel,
                            bands = bands,
                            factors = factors,
                            estimatedValue = estimate,
                            isLoading = false
                        )
                    }
                },
                onFailure = { _state.update { it.copy(isLoading = false) } }
            )
        }
    }

    private fun computeValuation(parcel: Parcel): Triple<List<ValuationBand>, List<ValuationFactor>, String> {
        val basePerAcre = 250_000.0
        val healthMultiplier = 0.5 + parcel.healthScore / 100.0
        val mid = basePerAcre * healthMultiplier * parcel.areaAcres
        val low = mid * 0.85
        val high = mid * 1.15

        val bands = listOf(
            ValuationBand("Conservative", formatINR(low * 0.9), formatINR(low), false),
            ValuationBand("Estimated", formatINR(low), formatINR(high), true),
            ValuationBand("Optimistic", formatINR(high), formatINR(high * 1.1), false)
        )

        val factors = listOf(
            ValuationFactor("Land Health Score", 0.35f, parcel.healthScore >= 50),
            ValuationFactor("NDVI Vegetation Index", 0.25f, parcel.ndvi >= 0.4),
            ValuationFactor("Water Access & Rainfall", 0.20f, parcel.rainfall >= 200.0),
            ValuationFactor("Soil Quality", 0.20f, parcel.soilType.contains("Alluvial", ignoreCase = true))
        )

        return Triple(bands, factors, formatINR(mid))
    }

    private fun formatINR(value: Double): String {
        return when {
            value >= 10_000_000 -> "₹${"%.1f".format(value / 10_000_000)}Cr"
            value >= 100_000    -> "₹${"%.1f".format(value / 100_000)}L"
            else                -> "₹${value.toInt()}"
        }
    }
}
