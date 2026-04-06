package com.landroid.features.auth.presentation

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.landroid.core.network.LandroidApiService
import com.landroid.core.security.TokenManager
import com.landroid.shared.models.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class CodeSent(val verificationId: String) : AuthState()
    data class Success(val user: UserProfile) : AuthState()
    data class Error(val message: String) : AuthState()
}

data class AuthUiState(
    val authState: AuthState = AuthState.Idle,
    val phoneNumber: String = "",
    val verificationId: String = "",
    val otpCode: String = ""
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val apiService: LandroidApiService,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state = _state.asStateFlow()

    fun setPhone(phone: String) {
        _state.update { it.copy(phoneNumber = phone) }
    }

    fun verifyPhoneNumber(phoneNumber: String, activity: Activity, role: String) {
        _state.update { it.copy(authState = AuthState.Loading) }
        tokenManager.saveRole(role)

        viewModelScope.launch {
            runCatching {
                val response = apiService.sendOtp(mapOf("phone" to phoneNumber))
                val verificationId = response["verificationId"] ?: "unknown"
                _state.update {
                    it.copy(
                        verificationId = verificationId,
                        authState = AuthState.CodeSent(verificationId)
                    )
                }
            }.onFailure { e ->
                _state.update { it.copy(authState = AuthState.Error(e.message ?: "Failed to reach FastAPI")) }
            }
        }
    }

    fun verifyOtp(code: String, verificationId: String) {
        viewModelScope.launch {
            _state.update { it.copy(authState = AuthState.Loading) }
            
            runCatching {
                val userProfile = apiService.verifyOtp(
                    mapOf(
                        "phone" to _state.value.phoneNumber,
                        "otp" to code,
                        "role" to (tokenManager.getRole() ?: "CONSULTANT")
                    )
                )
                tokenManager.saveToken(userProfile.uid)
                _state.update { it.copy(authState = AuthState.Success(userProfile)) }
            }.onFailure { e ->
                _state.update { it.copy(authState = AuthState.Error(e.message ?: "Invalid OTP")) }
            }
        }
    }

    fun resetState() {
        _state.update { it.copy(authState = AuthState.Idle) }
    }
}
