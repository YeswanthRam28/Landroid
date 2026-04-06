// app/src/main/java/com/landroid/features/parcels/presentation/ParcelViewModel.kt
package com.landroid.features.parcels.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.landroid.features.parcels.data.ParcelRepository
import com.landroid.features.parcels.domain.ParcelUseCase
import com.landroid.shared.models.Parcel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.landroid.core.security.TokenManager
import javax.inject.Inject

data class ParcelListUiState(
    val parcels: List<Parcel> = emptyList(),
    val filteredParcels: List<Parcel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedParcel: Parcel? = null,
    val userRole: String = "landowner"
)

@HiltViewModel
class ParcelViewModel @Inject constructor(
    private val repository: ParcelRepository,
    private val useCase: ParcelUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow(ParcelListUiState())
    val state = _state.asStateFlow()

    init {
        _state.update { it.copy(userRole = tokenManager.getRole() ?: "landowner") }
        loadParcels()
    }

    fun loadParcels() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            repository.getParcels().fold(
                onSuccess = { parcels ->
                    _state.update {
                        it.copy(
                            parcels = parcels,
                            filteredParcels = parcels,
                            isLoading = false
                        )
                    }
                },
                onFailure = { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
            )
        }
    }

    fun search(query: String) {
        _state.update { state ->
            val filtered = if (query.isBlank()) state.parcels
            else state.parcels.filter {
                it.name.contains(query, ignoreCase = true) ||
                    it.location.contains(query, ignoreCase = true) ||
                    it.district.contains(query, ignoreCase = true)
            }
            state.copy(searchQuery = query, filteredParcels = filtered)
        }
    }

    fun createParcel(parcel: Parcel) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repository.createParcel(parcel).fold(
                onSuccess = { loadParcels() },
                onFailure = { e -> _state.update { it.copy(isLoading = false, error = e.message) } }
            )
        }
    }

    fun selectParcel(parcel: Parcel) {
        _state.update { it.copy(selectedParcel = parcel) }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
