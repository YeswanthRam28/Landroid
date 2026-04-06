// app/src/main/java/com/landroid/core/di/DatabaseModule.kt
package com.landroid.core.di

import android.content.Context
import com.landroid.features.alerts.data.db.AlertDao
import com.landroid.features.alerts.data.db.AlertDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAlertDatabase(@ApplicationContext context: Context): AlertDatabase =
        AlertDatabase.getInstance(context)

    @Provides
    @Singleton
    fun provideAlertDao(database: AlertDatabase): AlertDao =
        database.alertDao()
}
