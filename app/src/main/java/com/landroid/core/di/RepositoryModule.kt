// app/src/main/java/com/landroid/core/di/RepositoryModule.kt
package com.landroid.core.di

import com.landroid.features.alerts.data.AlertRepository
import com.landroid.features.alerts.data.AlertRepositoryImpl
import com.landroid.features.auth.data.AuthRepository
import com.landroid.features.auth.data.AuthRepositoryImpl
import com.landroid.features.dashboard.data.DashboardRepository
import com.landroid.features.dashboard.data.DashboardRepositoryImpl
import com.landroid.features.parcels.data.ParcelRepository
import com.landroid.features.parcels.data.ParcelRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindParcelRepository(impl: ParcelRepositoryImpl): ParcelRepository

    @Binds
    @Singleton
    abstract fun bindDashboardRepository(impl: DashboardRepositoryImpl): DashboardRepository

    @Binds
    @Singleton
    abstract fun bindAlertRepository(impl: AlertRepositoryImpl): AlertRepository
}
