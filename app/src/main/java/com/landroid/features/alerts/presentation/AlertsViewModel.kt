// app/src/main/java/com/landroid/features/alerts/presentation/AlertsViewModel.kt
package com.landroid.features.alerts.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.landroid.features.alerts.data.AlertRepository
import com.landroid.shared.models.Alert
import com.landroid.shared.models.AlertCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlertsViewModel @Inject constructor(
    private val repository: AlertRepository
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<AlertCategory?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val alerts = _selectedCategory.flatMapLatest { category ->
        if (category == null) repository.getAllAlerts()
        else repository.getAlertsByCategory(category)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val unreadCount = repository.getUnreadCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    fun selectCategory(category: AlertCategory?) {
        _selectedCategory.update { category }
    }

    fun markAsRead(id: String) {
        viewModelScope.launch { repository.markAsRead(id) }
    }

    fun markAllAsRead() {
        viewModelScope.launch { repository.markAllAsRead() }
    }
}
