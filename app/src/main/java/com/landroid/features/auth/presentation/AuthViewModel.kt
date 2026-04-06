// app/src/main/java/com/landroid/features/auth/presentation/AuthViewModel.kt
package com.landroid.features.auth.presentation

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.landroid.core.security.TokenManager
import com.landroid.features.auth.data.AuthRepository
import com.landroid.shared.models.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
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
    private val repository: AuthRepository,
    private val tokenManager: TokenManager,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state = _state.asStateFlow()

    fun setPhone(phone: String) {
        _state.update { it.copy(phoneNumber = phone) }
    }

    fun verifyPhoneNumber(phoneNumber: String, activity: Activity, role: String) {
        _state.update { it.copy(authState = AuthState.Loading) }
        tokenManager.saveRole(role)

        // HACKATHON BYPASS: Prevent billing issues and SMS quotas
        if (phoneNumber == "+910000000000" || phoneNumber == "0000000000") {
            _state.update {
                it.copy(
                    verificationId = "bypass_id",
                    authState = AuthState.CodeSent("bypass_id")
                )
            }
            return
        }

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                viewModelScope.launch {
                    handleCredential(credential)
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                _state.update { it.copy(authState = AuthState.Error(e.message ?: "Verification failed")) }
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                _state.update {
                    it.copy(
                        verificationId = verificationId,
                        authState = AuthState.CodeSent(verificationId)
                    )
                }
            }
        }

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyOtp(code: String, verificationId: String) {
        viewModelScope.launch {
            _state.update { it.copy(authState = AuthState.Loading) }
            
            // HACKATHON BYPASS: Mock login
            if (verificationId == "bypass_id" && code == "000000") {
                val mockUser = UserProfile(
                    uid = "mock_uid_1234",
                    name = "Demo User",
                    phone = "+910000000000",
                    role = if (tokenManager.getRole() == "consultant") com.landroid.shared.models.UserRole.CONSULTANT else com.landroid.shared.models.UserRole.LANDOWNER
                )
                tokenManager.saveToken("mock_uid_1234")
                _state.update { it.copy(authState = AuthState.Success(mockUser)) }
                return@launch
            }
            
            val credential = PhoneAuthProvider.getCredential(verificationId, code)
            handleCredential(credential)
        }
    }

    private suspend fun handleCredential(credential: PhoneAuthCredential) {
        repository.verifyOtp(credential).fold(
            onSuccess = { user ->
                _state.update { it.copy(authState = AuthState.Success(user)) }
            },
            onFailure = { e ->
                _state.update { it.copy(authState = AuthState.Error(e.message ?: "Auth failed")) }
            }
        )
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _state.update { it.copy(authState = AuthState.Loading) }
            repository.signInWithGoogle(idToken).fold(
                onSuccess = { user ->
                    _state.update { it.copy(authState = AuthState.Success(user)) }
                },
                onFailure = { e ->
                    _state.update { it.copy(authState = AuthState.Error(e.message ?: "Google sign-in failed")) }
                }
            )
        }
    }

    fun resetState() {
        _state.update { it.copy(authState = AuthState.Idle) }
    }
}
