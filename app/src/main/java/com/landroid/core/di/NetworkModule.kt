// app/src/main/java/com/landroid/core/di/NetworkModule.kt
package com.landroid.core.di

import com.landroid.core.network.AuthInterceptor
import com.landroid.core.network.LandroidApiService
import com.landroid.core.network.RetrofitClient
import com.landroid.core.security.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideAuthInterceptor(tokenManager: TokenManager): AuthInterceptor =
        AuthInterceptor(tokenManager)

    @Provides
    @Singleton
    fun provideLandroidApiService(authInterceptor: AuthInterceptor): LandroidApiService =
        RetrofitClient.create(authInterceptor)
}
