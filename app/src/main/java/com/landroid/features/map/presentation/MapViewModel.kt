// app/src/main/java/com/landroid/features/map/presentation/MapViewModel.kt
package com.landroid.features.map.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.landroid.features.parcels.data.ParcelRepository
import com.landroid.shared.models.Parcel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MapUiState(
    val parcel: Parcel? = null,
    val activeLayer: String = "Ortho",
    val isLoading: Boolean = false,
    val error: String? = null
)

val MAP_LAYERS = listOf("Ortho", "NDVI", "Elevation", "Health Zones", "Boundary")

@HiltViewModel
class MapViewModel @Inject constructor(
    private val repository: ParcelRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MapUiState())
    val state = _state.asStateFlow()

    fun loadParcel(parcelId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repository.getParcelById(parcelId).fold(
                onSuccess = { parcel -> _state.update { it.copy(parcel = parcel, isLoading = false) } },
                onFailure = { e -> _state.update { it.copy(isLoading = false, error = e.message) } }
            )
        }
    }

    fun toggleLayer(layer: String) {
        _state.update { it.copy(activeLayer = layer) }
    }
}
