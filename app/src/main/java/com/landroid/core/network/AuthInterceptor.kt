// app/src/main/java/com/landroid/core/network/AuthInterceptor.kt
package com.landroid.core.network

import com.landroid.core.security.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenManager.getAccessToken()
        val request = chain.request().newBuilder().apply {
            if (token != null) {
                addHeader("Authorization", "Bearer $token")
                addHeader("apikey", token)
            }
            addHeader("Content-Type", "application/json")
        }.build()

        val response = chain.proceed(request)

        // On 401: refresh token and retry once
        if (response.code == 401) {
            response.close()
            val newToken = runBlocking {
                tokenManager.refreshToken().getOrNull()
            }
            if (newToken != null) {
                val retryRequest = chain.request().newBuilder()
                    .removeHeader("Authorization")
                    .addHeader("Authorization", "Bearer $newToken")
                    .addHeader("apikey", newToken)
                    .build()
                return chain.proceed(retryRequest)
            }
        }

        return response
    }
}
