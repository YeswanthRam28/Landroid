// app/src/main/java/com/landroid/core/security/TokenManager.kt
package com.landroid.core.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.auth.Auth
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext context: Context,
    private val supabaseAuth: Auth
) {
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    private val prefs = EncryptedSharedPreferences.create(
        "secure_prefs",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveFcmToken(token: String) {
        prefs.edit().putString("fcm_token", token).apply()
    }

    fun saveRole(role: String) {
        prefs.edit().putString("user_role", role).apply()
    }

    fun getRole(): String? {
        return prefs.getString("user_role", null)
    }

    fun saveToken(token: String) {
        prefs.edit().putString("auth_token", token).apply()
    }

    fun getToken(): String? {
        return prefs.getString("auth_token", null)
    }

    fun getAccessToken(): String? {
        return getToken()
    }

    suspend fun refreshToken(): Result<String> {
        return runCatching {
            val token = getToken() ?: throw Exception("No token available")
            token
        }
    }

    fun clearToken() {
        prefs.edit().remove("auth_token").apply()
        prefs.edit().remove("fcm_token").apply()
        prefs.edit().remove("user_role").apply()
    }

    fun logout() {
        clearToken()
    }
}
