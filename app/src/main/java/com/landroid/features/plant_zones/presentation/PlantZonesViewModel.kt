// app/src/main/java/com/landroid/features/plant_zones/presentation/PlantZonesViewModel.kt
package com.landroid.features.plant_zones.presentation

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.landroid.features.parcels.data.ParcelRepository
import com.landroid.shared.models.NdviZone
import com.landroid.shared.models.Parcel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlantZonesUiState(
    val parcel: Parcel? = null,
    val zones: List<NdviZone> = defaultZones(),
    val stressedZoneIncreased: Boolean = true,
    val isLoading: Boolean = false
)

fun defaultZones() = listOf(
    NdviZone("bare",    Color(0xFFE53E3E), 0.0f, 0.2f, areaPercent = 12f),
    NdviZone("sparse",  Color(0xFFED8936), 0.2f, 0.4f, areaPercent = 28f),
    NdviZone("healthy", Color(0xFF48BB78), 0.4f, 0.6f, areaPercent = 35f),
    NdviZone("dense",   Color(0xFF276749), 0.6f, 1.0f, areaPercent = 25f)
)

@HiltViewModel
class PlantZonesViewModel @Inject constructor(
    private val repository: ParcelRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PlantZonesUiState())
    val state = _state.asStateFlow()

    fun load(parcelId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repository.getParcelById(parcelId).fold(
                onSuccess = { parcel -> _state.update { it.copy(parcel = parcel, isLoading = false) } },
                onFailure = { _state.update { it.copy(isLoading = false) } }
            )
        }
    }
}
