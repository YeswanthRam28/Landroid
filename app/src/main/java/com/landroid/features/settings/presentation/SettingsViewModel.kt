// app/src/main/java/com/landroid/features/settings/presentation/SettingsViewModel.kt
package com.landroid.features.settings.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.landroid.core.security.TokenManager
import com.landroid.shared.models.UserProfile
import com.landroid.shared.models.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.maplibre.android.offline.OfflineManager
import javax.inject.Inject

data class SettingsUiState(
    val user: UserProfile? = null,
    val geofenceBuffer: Float = 25f,
    val selectedLanguage: String = "en",
    val cacheUsedMb: Int = 312,
    val cacheLimitMb: Int = 500,
    val isSigningOut: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val tokenManager: TokenManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state = _state.asStateFlow()

    init {
        loadUser()
    }

    private fun loadUser() {
        val fbUser = firebaseAuth.currentUser
        val role = tokenManager.getRole() ?: "landowner"
        if (fbUser != null) {
            _state.update {
                it.copy(
                    user = UserProfile(
                        uid = fbUser.uid,
                        name = fbUser.displayName ?: "User",
                        phone = fbUser.phoneNumber ?: "",
                        role = if (role == "consultant") UserRole.CONSULTANT else UserRole.LANDOWNER
                    )
                )
            }
        }
    }

    fun setGeofenceBuffer(value: Float) {
        _state.update { it.copy(geofenceBuffer = value) }
    }

    fun setLanguage(tag: String) {
        _state.update { it.copy(selectedLanguage = tag) }
    }

    fun clearCache() {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                context.cacheDir.deleteRecursively()

            }
            _state.update { it.copy(cacheUsedMb = 0) }
        }
    }

    fun signOut(onSignedOut: () -> Unit) {
        viewModelScope.launch {
            _state.update { it.copy(isSigningOut = true) }
            firebaseAuth.signOut()
            tokenManager.clearToken()
            onSignedOut()
        }
    }
}
