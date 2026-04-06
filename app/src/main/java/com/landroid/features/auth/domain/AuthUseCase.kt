// app/src/main/java/com/landroid/features/auth/domain/AuthUseCase.kt
package com.landroid.features.auth.domain

import com.google.firebase.auth.PhoneAuthCredential
import com.landroid.features.auth.data.AuthRepository
import com.landroid.shared.models.UserProfile
import javax.inject.Inject

class AuthUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend fun verifyOtp(credential: PhoneAuthCredential): Result<UserProfile> =
        repository.verifyOtp(credential)

    suspend fun signInWithGoogle(idToken: String): Result<UserProfile> =
        repository.signInWithGoogle(idToken)

    suspend fun signOut() = repository.signOut()

    fun getCurrentUser(): UserProfile? = repository.getCurrentUser()
}
