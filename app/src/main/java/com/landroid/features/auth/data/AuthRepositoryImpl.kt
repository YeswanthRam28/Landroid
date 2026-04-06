// app/src/main/java/com/landroid/features/auth/data/AuthRepositoryImpl.kt
package com.landroid.features.auth.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.landroid.core.security.TokenManager
import com.landroid.shared.models.UserProfile
import com.landroid.shared.models.UserRole
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun sendOtp(phoneNumber: String, options: PhoneAuthOptions) {
        com.google.firebase.auth.PhoneAuthProvider.verifyPhoneNumber(options)
    }

    override suspend fun verifyOtp(credential: PhoneAuthCredential): Result<UserProfile> =
        runCatching {
            val result = firebaseAuth.signInWithCredential(credential).await()
            val user = result.user ?: error("Firebase user is null after sign-in")
            tokenManager.saveToken(user.uid)
            val role = tokenManager.getRole() ?: "landowner"
            UserProfile(
                uid = user.uid,
                name = user.displayName ?: "",
                phone = user.phoneNumber ?: "",
                role = if (role == "consultant") UserRole.CONSULTANT else UserRole.LANDOWNER
            )
        }

    override suspend fun signInWithGoogle(idToken: String): Result<UserProfile> =
        runCatching {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            val user = result.user ?: error("Firebase user is null after Google sign-in")
            tokenManager.saveToken(user.uid)
            UserProfile(
                uid = user.uid,
                name = user.displayName ?: "",
                phone = user.phoneNumber ?: "",
                role = UserRole.LANDOWNER
            )
        }

    override suspend fun signOut() {
        firebaseAuth.signOut()
        tokenManager.clearToken()
    }

    override fun getCurrentUser(): UserProfile? {
        val user = firebaseAuth.currentUser ?: return null
        val role = tokenManager.getRole() ?: "landowner"
        return UserProfile(
            uid = user.uid,
            name = user.displayName ?: "",
            phone = user.phoneNumber ?: "",
            role = if (role == "consultant") UserRole.CONSULTANT else UserRole.LANDOWNER
        )
    }
}
