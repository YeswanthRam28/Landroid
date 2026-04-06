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
    val zones: List<NdviZone> = emptyList(),
    val stressedZoneIncreased: Boolean = false,
    val isLoading: Boolean = false
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
            
            val parcelResult = repository.getParcelById(parcelId)
            val insightsResult = repository.getInsights(parcelId)
            
            parcelResult.fold(
                onSuccess = { parcel -> 
                    val zones = insightsResult.getOrNull()?.plantZones?.map { it.toNdviZone() } ?: emptyList()
                    val bareZone = zones.find { it.id == "bare" }?.areaPercent ?: 0f
                    
                    _state.update { 
                        it.copy(
                            parcel = parcel, 
                            zones = zones,
                            stressedZoneIncreased = bareZone > 10f,
                            isLoading = false
                        ) 
                    } 
                },
                onFailure = { _state.update { it.copy(isLoading = false) } }
            )
        }
    }
}
