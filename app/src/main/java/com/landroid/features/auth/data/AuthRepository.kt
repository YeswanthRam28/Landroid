// app/src/main/java/com/landroid/features/auth/data/AuthRepository.kt
package com.landroid.features.auth.data

import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.landroid.shared.models.UserProfile

interface AuthRepository {
    suspend fun sendOtp(phoneNumber: String, options: PhoneAuthOptions)
    suspend fun verifyOtp(credential: PhoneAuthCredential): Result<UserProfile>
    suspend fun signInWithGoogle(idToken: String): Result<UserProfile>
    suspend fun signOut()
    fun getCurrentUser(): UserProfile?
}
